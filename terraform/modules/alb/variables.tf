variable "name" { type = string }
variable "vpc_id" { type = string }
variable "public_subnet_ids" { type = list(string) }
variable "tags" {
  type    = map(string)
  default = {}
}
variable "project" {
  description = "Nome do projeto"
  type        = string
  default     = "api-java"
}