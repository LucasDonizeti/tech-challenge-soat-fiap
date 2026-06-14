variable "name" { type = string }
variable "instance_class" { type = string }
variable "security_group_id" { type = string }
variable "db_subnet_group_name" { type = string }
variable "database_subnets" { type = list(string) }
variable "tags" {
  type    = map(string)
  default = {}
}
