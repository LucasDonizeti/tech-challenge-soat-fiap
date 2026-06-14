output "endpoint" {
  value     = module.db.db_instance_address
  sensitive = true
}

output "port" {
  value = module.db.db_instance_port
}

output "db_instance_address" {
  value = module.db.db_instance_address
}

