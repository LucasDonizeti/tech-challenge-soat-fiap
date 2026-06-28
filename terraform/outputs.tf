output "vpc_id" {
  description = "ID da VPC"
  value       = module.vpc.vpc_id
}

output "eks_cluster_name" {
  description = "Nome do cluster EKS"
  value       = module.eks.cluster_name
}

output "eks_cluster_endpoint" {
  description = "Endpoint do cluster EKS"
  value       = module.eks.cluster_endpoint
}

output "rds_endpoint" {
  description = "Endpoint do RDS para a aplicação"
  value       = module.rds.endpoint
  sensitive   = true
}

output "rds_port" {
  description = "Porta do RDS"
  value       = module.rds.port
}
