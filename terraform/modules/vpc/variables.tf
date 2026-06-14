variable "name" {
  description = "Nome da VPC"
  type        = string
}

variable "cidr" {
  description = "Bloco CIDR da VPC"
  type        = string
}

variable "azs" {
  description = "Lista de Availability Zones"
  type        = list(string)
}

variable "private_subnets" {
  description = "Lista de CIDRs para subnets privadas"
  type        = list(string)
}

variable "public_subnets" {
  description = "Lista de CIDRs para subnets públicas"
  type        = list(string)
}

variable "database_subnets" {
  description = "Lista de CIDRs para subnets de banco de dados"
  type        = list(string)
  default     = []
}

variable "project" {
  description = "Nome do projeto"
  type        = string
  default     = "api-java"
}

variable "tags" {
  description = "Tags adicionais"
  type        = map(string)
  default     = {}
}