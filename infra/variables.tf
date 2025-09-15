variable "consul_image" {
  description = "Consul Docker image"
  default     = "hashicorp/consul:1.21"
}

variable "consul_container_name" {
  description = "Name of the Consul container"
  default     = "consul"
}

