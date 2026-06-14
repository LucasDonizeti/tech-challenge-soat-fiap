output "cluster_id" {
  value = module.ecs.cluster_id
}
output "cluster_name" {
  value = module.ecs.cluster_name
}
output "services" {
  value = module.ecs.services
}
output "cloudwatch_log_group_name" {
  value = module.ecs.cloudwatch_log_group_name
}