# Isso cria o bucket S3 e a tabela DynamoDB necessários para o estado principal do Terraform
# Execute primeiro com: terraform init && terraform apply

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

# Bucket
resource "aws_s3_bucket" "terraform_state" {
  bucket        = "bucket-tfstate-1029"
  force_destroy = false
}

resource "aws_s3_bucket_versioning" "enabled" {
  bucket = "bucket-tfstate-1029"
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_dynamodb_table" "terraform_locks" {
  name         = "meu-terraform-state-lock"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "LockID"

  attribute {
    name = "LockID"
    type = "S"
  }
}


module "ecr" {
  source  = "terraform-aws-modules/ecr/aws"

  repository_name = "teste-bbbbbbb"

  repository_image_tag_mutability = "MUTABLE"
  
  repository_image_scan_on_push   = true 

  repository_lifecycle_policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Manter apenas as ultimas 3 imagens"
        selection = {
          tagStatus   = "any"
          countType   = "imageCountMoreThan"
          countNumber = 3
        }
        action = {
          type = "expire"
        }
      }
    ]
  })

  repository_read_write_access_arns = [
    "arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"
  ]

  tags = {
    Environment = "training"
  }
}

data "aws_caller_identity" "current" {}

output "ecr_repository_url" {
  value       = module.ecr.repository_url
}
