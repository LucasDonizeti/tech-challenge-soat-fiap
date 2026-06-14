# https://registry.terraform.io/modules/terraform-aws-modules/alb/aws/latest
module "alb" {
  source  = "terraform-aws-modules/alb/aws"

  name    = var.name
  vpc_id  = var.vpc_id
  subnets = var.public_subnet_ids

  target_groups = {
    api-tg = { 
      backend_protocol                  = "HTTP"
      backend_port                      = 8080
      target_type                       = "ip"
      deregistration_delay              = 5
      load_balancing_algorithm_type     = "round_robin"

      create_attachment = false
      
      health_check = {
        enabled             = true
        path                = "/actuator/health"
        port                = "8080"
        protocol            = "HTTP"
        healthy_threshold   = 2
        unhealthy_threshold = 3
        timeout             = 5
        interval            = 30
        matcher             = "200"
      }
    }
  }

  listeners = {
    http = {
      port     = 80
      protocol = "HTTP"
      forward = {
        target_group_key = "api-tg"
      }
    }
  }

  tags = var.tags
}

/*

resource "aws_lb_target_group" "app" {
  name        = "${var.name}-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  health_check {
    enabled             = true
    interval            = 30
    path                = "/actuator/health"
    port                = "8080"
    healthy_threshold   = 2
    unhealthy_threshold = 3
    timeout             = 5
    matcher             = "200"
  }

  tags = merge(
    var.tags,
    {
      Project = var.project
    }
  )
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = module.alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.app.arn
  }
}
*/