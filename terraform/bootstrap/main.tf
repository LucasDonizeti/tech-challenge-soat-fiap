# ==============================================================================
# Bootstrap — executar UMA VEZ antes do main
# Cria o backend remoto (S3 + lock) e o repositório ECR
# Uso: cd terraform/bootstrap && terraform init && terraform apply
# ==============================================================================

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}

data "aws_caller_identity" "current" {}

# ------------------------------------------------------------------------------
# Backend remoto — S3 + lock nativo
# ------------------------------------------------------------------------------
resource "aws_s3_bucket" "terraform_state" {
  bucket        = "bucket-tfstate-1029"
  force_destroy = false

  tags = { Name = "terraform-state" }
}

resource "aws_s3_bucket_versioning" "enabled" {
  bucket = aws_s3_bucket.terraform_state.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "state" {
  bucket = aws_s3_bucket.terraform_state.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

# DynamoDB mantido para compatibilidade com pipelines que usam dynamodb_table
resource "aws_dynamodb_table" "terraform_locks" {
  name         = "meu-terraform-state-lock"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "LockID"

  attribute {
    name = "LockID"
    type = "S"
  }

  tags = { Name = "terraform-state-lock" }
}

# ------------------------------------------------------------------------------
# ECR — repositório da imagem da aplicação
# ------------------------------------------------------------------------------
module "ecr" {
  source = "terraform-aws-modules/ecr/aws"

  repository_name                 = "oficina-api"
  repository_image_tag_mutability = "MUTABLE"
  repository_image_scan_on_push   = true

  # Mantém apenas as 5 últimas imagens para economizar espaço
  repository_lifecycle_policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Manter apenas as 5 ultimas imagens"
        selection = {
          tagStatus   = "any"
          countType   = "imageCountMoreThan"
          countNumber = 5
        }
        action = { type = "expire" }
      }
    ]
  })

  # AWS Academy: LabRole tem permissão de leitura/escrita
  repository_read_write_access_arns = [
    "arn:aws:iam::${data.aws_caller_identity.current.account_id}:role/LabRole"
  ]

  tags = {
    Project     = "oficina"
    Environment = "prod"
  }
}

# ------------------------------------------------------------------------------
# Outputs
# ------------------------------------------------------------------------------
output "ecr_repository_url" {
  description = "URL do repositório ECR para usar na pipeline"
  value       = module.ecr.repository_url
}

output "s3_bucket_name" {
  value = aws_s3_bucket.terraform_state.id
}
