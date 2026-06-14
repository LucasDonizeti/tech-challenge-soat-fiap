output "vpc_id" {
  description = "ID da VPC"
  value       = module.vpc.vpc_id
}

output "private_subnets" {
  description = "IDs das subnets privadas"
  value       = module.vpc.private_subnets
}

output "public_subnets" {
  description = "IDs das subnets públicas"
  value       = module.vpc.public_subnets
}

output "database_subnets" {
  description = "IDs das subnets de banco"
  value       = module.vpc.database_subnets
}

output "nat_public_ips" {
  description = "IPs públicos dos NAT Gateways"
  value       = module.vpc.nat_public_ips
}

output "default_security_group_id" {
  description = "O ID do grupo de segurança criado por padrão na criação da VPC"
  value       = module.vpc.default_security_group_id
}