# https://registry.terraform.io/modules/terraform-aws-modules/ecs/aws/latest
module "ecs" {
  source = "terraform-aws-modules/ecs/aws"

  cluster_name = var.name

  cluster_configuration = {
    execute_command_configuration = {
      logging = "NONE"
    }
  }

  create_infrastructure_iam_role = false
  create_task_exec_iam_role        = false
  create_cloudwatch_log_group = false

  services = {
    api-java = {
      cpu    = 512
      memory = 1024

      assign_public_ip = true

      enable_backend_networking = true

      load_balancer = {
        service = {
          target_group_arn = var.alb_target_group_arn
          container_name   = "api"
          container_port   = 8080
        }
      }

      container_definitions = {
        api = {
          name  = "api"
          image = var.container_image

          cpu    = 512
          memory = 1024

          readonly_root_filesystem = false

          tmpfs = [
            {
              container_path = "/tmp"
              mount_options  = ["defaults", "noatime"]
              size           = 512 # Tamanho em MB alocado para o Tomcat trabalhar
            }
          ]

          portMappings = [
            {
              containerPort = 8080
              protocol      = "tcp"
            }
          ]
          environment = []
          secrets     = [] # pode vir do Secrets Manager
          
          log_configuration = {
            log_driver = "awslogs"
            options = {
              "awslogs-group"         = "/ecs/${var.name}-log-group"
              "awslogs-region"        = "us-east-1"
              "awslogs-stream-prefix" = "ecs"
            }
          }
        }
      }

      subnet_ids = var.subnet_ids

      security_group_rules = {
        ingress_alb = {
          type                     = "ingress"
          from_port                = 8080
          to_port                  = 8080
          protocol                 = "tcp"
          source_security_group_id = var.alb_security_group_id
        }
        egress_all = {
          type        = "egress"
          from_port   = 0
          to_port     = 0
          protocol    = "-1"
          cidr_blocks  = ["0.0.0.0/0"]
        }
      }

      # Não criar Roles, usar LabRole
      create_iam_role          = false
      create_task_exec_iam_role = false
      create_task_exec_policy   = false
      create_tasks_iam_role     = false
      task_role_arn            = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:role/LabRole"
      task_exec_iam_role_arn   = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:role/LabRole"
      tasks_iam_role_arn       = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:role/LabRole"

      /*
      health_check = {
        enabled             = true
        healthy_threshold   = 2
        unhealthy_threshold = 3
        interval            = 30
        timeout             = 5
        path                = "/actuator/health"
        matcher             = "200"
      }
      */
      
    }
  }

  tags = var.tags
}

data "aws_caller_identity" "current" {}