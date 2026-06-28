# 1. Cluster EKS
resource "aws_eks_cluster" "this" {
  name     = var.cluster_name
  version  = "1.36"
  role_arn = "arn:aws:iam::${var.account_id}:role/LabRole"

  vpc_config {
    subnet_ids              = var.private_subnets
    endpoint_public_access  = true
    endpoint_private_access = true
  }

  access_config {
    authentication_mode                         = "API_AND_CONFIG_MAP"
    bootstrap_cluster_creator_admin_permissions = false
  }

  tags = var.tags
}

# 2. Node Group Gerenciado (Equivalente ao eks_managed_node_groups)
resource "aws_eks_node_group" "main" {
  cluster_name    = aws_eks_cluster.this.name
  node_group_name = "main"
  node_role_arn   = "arn:aws:iam::${var.account_id}:role/LabRole"
  subnet_ids      = var.private_subnets

  scaling_config {
    desired_size = 1
    max_size     = 2
    min_size     = 1
  }

  instance_types = ["t3.medium"]

  depends_on = [aws_eks_cluster.this]
  tags       = var.tags
}

# 3. Add-ons do EKS (vpc-cni, kube-proxy, coredns)
resource "aws_eks_addon" "addons" {
  for_each     = toset(["vpc-cni", "kube-proxy", "coredns"])
  cluster_name = aws_eks_cluster.this.name
  addon_name   = each.value

  depends_on = [aws_eks_cluster.this]
}

# ==============================================================================
# PERMISSÕES DE ACESSO (Substituindo o access_entries do módulo)
# ==============================================================================

# Entrada para a LabRole
resource "aws_eks_access_entry" "lab_role" {
  cluster_name  = aws_eks_cluster.this.name
  principal_arn = "arn:aws:iam::${var.account_id}:role/LabRole"
  type          = "STANDARD"
}

resource "aws_eks_access_policy_association" "lab_role_admin" {
  cluster_name  = aws_eks_cluster.this.name
  policy_arn    = "arn:aws:eks::aws:cluster-access-policy/AmazonEKSClusterAdminPolicy"
  principal_arn = aws_eks_access_entry.lab_role.principal_arn
  access_scope  { type = "cluster" }
}

# Entrada para a sua Role atual do Vocareum (para rodar o kubectl de fora)
resource "aws_eks_access_entry" "voclabs_user" {
  cluster_name  = aws_eks_cluster.this.name
  principal_arn = "arn:aws:iam::${var.account_id}:role/voclabs"
  type          = "STANDARD"
}

resource "aws_eks_access_policy_association" "voclabs_admin" {
  cluster_name  = aws_eks_cluster.this.name
  policy_arn    = "arn:aws:eks::aws:cluster-access-policy/AmazonEKSClusterAdminPolicy"
  principal_arn = aws_eks_access_entry.voclabs_user.principal_arn
  access_scope  { type = "cluster" }
}