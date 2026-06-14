output "security_group_id" {
  value = module.alb.security_group_id
}

output "id" {
  value = module.alb.id
}

output "dns_name" {
  value = module.alb.dns_name
}

output "target_group_arn" {
  value       = module.alb.target_groups["api-tg"].arn
}