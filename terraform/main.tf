data "aws_caller_identity" "current" {}

locals {
  cluster_name = "${var.app_name}-cluster"
}

# ------------------------------------------------------------------------------
# VPC
# ------------------------------------------------------------------------------
module "vpc" {
  source = "./modules/vpc"

  name             = "${var.app_name}-vpc"
  cidr             = var.vpc_cidr
  azs              = ["${var.region}a", "${var.region}b"]
  private_subnets  = ["10.0.1.0/24", "10.0.2.0/24"]
  public_subnets   = ["10.0.101.0/24", "10.0.102.0/24"]
  database_subnets = ["10.0.201.0/24", "10.0.202.0/24"]
  cluster_name     = local.cluster_name

  tags = { Project = var.app_name }
}

# ------------------------------------------------------------------------------
# EKS
# ------------------------------------------------------------------------------
module "eks" {
  source = "./modules/eks"

  cluster_name    = local.cluster_name
  vpc_id          = module.vpc.vpc_id
  private_subnets = module.vpc.private_subnets
  public_subnets  = module.vpc.public_subnets
  account_id      = data.aws_caller_identity.current.account_id

  tags = { Project = var.app_name }
}

# ------------------------------------------------------------------------------
# RDS — MySQL 8.0
# ------------------------------------------------------------------------------
module "rds" {
  source = "./modules/rds"

  name                 = "${var.app_name}-rds"
  db_name              = var.db_name
  db_username          = var.db_username
  subnet_ids           = module.vpc.database_subnets
  db_subnet_group_name = module.vpc.database_subnet_group_name
  vpc_id               = module.vpc.vpc_id
  eks_node_sg_id       = module.eks.node_security_group_id

  tags = { Project = var.app_name }
}
