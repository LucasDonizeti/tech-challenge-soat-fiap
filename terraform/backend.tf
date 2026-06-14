terraform {
  backend "s3" {
    bucket       = "bucket-tfstate-1029"
    key          = "global/s3/terraform.tfstate"
    region       = "us-east-1"
    use_lockfile = true
    encrypt      = true
  }
}
