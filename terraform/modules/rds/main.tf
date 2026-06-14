# https://registry.terraform.io/modules/terraform-aws-modules/rds/aws/latest
module "db" {
  source = "terraform-aws-modules/rds/aws"

  identifier = var.name

  engine            = "mysql"
  engine_version    = "8.0"
  instance_class    = var.instance_class
  allocated_storage = 20

  db_name                     = "oficinadb"
  username                    = "adminelson"
  port                        = "3306"
  manage_master_user_password = true

  iam_database_authentication_enabled = true

  vpc_security_group_ids = [var.security_group_id]

  maintenance_window = "Mon:00:00-Mon:03:00"
  backup_window      = "03:00-06:00"


  # DB subnet group
  create_db_subnet_group = true
  db_subnet_group_name   = var.db_subnet_group_name
  subnet_ids             = var.database_subnets

  # DB parameter group
  family = "mysql8.0"

  # DB option group
  major_engine_version = "8.0"

  # Database Deletion Protection
  deletion_protection = true

  parameters = [
    {
      name  = "character_set_client"
      value = "utf8mb4"
    },
    {
      name  = "character_set_server"
      value = "utf8mb4"
    }
  ]

  options = [
    {
      option_name = "MARIADB_AUDIT_PLUGIN"

      option_settings = [
        {
          name  = "SERVER_AUDIT_EVENTS"
          value = "CONNECT"
        },
        {
          name  = "SERVER_AUDIT_FILE_ROTATIONS"
          value = "37"
        },
      ]
    },
  ]

  tags = var.tags
}