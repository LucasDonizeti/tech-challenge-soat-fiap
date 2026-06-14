output "api_url" {
  description = "URL da API"
  value       = "http://${module.alb.dns_name}"
}

output "ecs_cluster_name" {
  description = "Nome do Cluster ECS"
  value       = module.ecs.cluster_name
}

/*
output "rds_endpoint" {
  description = "Endpoint do banco de dados"
  value       = module.rds.endpoint
  sensitive   = true
}
*/

output "vpc_id" {
  description = "ID da VPC"
  value       = module.vpc.vpc_id
}