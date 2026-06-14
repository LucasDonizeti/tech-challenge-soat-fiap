variable "region" {
  description = "Região AWS"
  type        = string
  default     = "us-east-1"
}

variable "vpc_cidr" {
  description = "CIDR da VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "app_name" {
  description = "Nome da aplicação"
  type        = string
  default     = "api-java"
}

variable "container_image" {
  description = "Docker container image for ECS"
  type        = string
}