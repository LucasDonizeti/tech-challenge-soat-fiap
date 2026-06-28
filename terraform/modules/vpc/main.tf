# https://registry.terraform.io/modules/terraform-aws-modules/vpc/aws/latest
module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "6.6.1"

  name = var.name
  cidr = var.cidr

  azs              = var.azs
  private_subnets  = var.private_subnets
  public_subnets   = var.public_subnets
  database_subnets = var.database_subnets

  # NAT Gateway para as subnets privadas (EKS nodes e RDS)
  enable_nat_gateway = true
  single_nat_gateway = true # economiza custo em ambiente de estudo

  # DNS obrigatório para ECR, EKS, RDS e Secrets Manager funcionarem
  enable_dns_hostnames = true
  enable_dns_support   = true

  # Subnet group do RDS criado automaticamente
  create_database_subnet_group = true

  # Tags necessárias para o AWS Load Balancer Controller descobrir as subnets
  public_subnet_tags = {
    "kubernetes.io/role/elb" = "1"
  }

  private_subnet_tags = {
    "kubernetes.io/role/internal-elb" = "1"
  }

  tags = merge(var.tags, { Name = var.name })
}
