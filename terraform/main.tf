module "vpc" {
  source = "./modules/vpc"

  name = "${var.app_name}-vpc"
  cidr = var.vpc_cidr

  azs              = ["us-east-1a", "us-east-1b", "us-east-1c"]
  private_subnets  = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  public_subnets   = ["10.0.101.0/24", "10.0.102.0/24", "10.0.103.0/24"]
  database_subnets = ["10.0.201.0/24", "10.0.202.0/24", "10.0.203.0/24"]
}

module "alb" {
  source = "./modules/alb"

  name              = "${var.app_name}-alb"
  vpc_id            = module.vpc.vpc_id
  public_subnet_ids = module.vpc.public_subnets
}

module "ecs" {
  source = "./modules/ecs"

  name                   = "${var.app_name}-ecs"
  container_image        = var.container_image
  subnet_ids     = module.vpc.public_subnets
  alb_target_group_arn   = module.alb.target_group_arn
  alb_security_group_id  = module.alb.security_group_id
  tags                   = {
    Project = var.app_name
  }
}

/*
module "rds" {
  source = "./modules/rds"

  name                 = "${var.app_name}-rds"
  database_subnets     = module.vpc.database_subnets
  instance_class       = "db.t3.micro"
  security_group_id    = module.vpc.default_security_group_id
  db_subnet_group_name = "${var.app_name}-db-subnet-group"
  tags                 = {
    Project = var.app_name
  }
}
*/