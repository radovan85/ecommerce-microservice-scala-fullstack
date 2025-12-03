terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = ">= 3.0.2"
    }
    nomad = {
      source  = "hashicorp/nomad"
      version = ">= 1.5.0"
    }
  }
}


provider "docker" {
  host = "unix:///var/run/docker.sock"
}

provider "nomad" {
  address = "http://127.0.0.1:4646"
}

resource "docker_image" "consul" {
  name = var.consul_image
}

resource "docker_container" "consul" {
  name         = var.consul_container_name
  image        = docker_image.consul.name   # <-- promenjeno
  network_mode = "host"

  command = [
    "agent", "-dev", "-client=0.0.0.0"
  ]

  restart = "unless-stopped"
}

# === Eureka server job ===
resource "nomad_job" "eureka_server" {
  jobspec = file("${path.module}/nomad-jobs/eureka.nomad.hcl")
}

resource "null_resource" "eureka_ready" {
  depends_on = [nomad_job.eureka_server]

  provisioner "local-exec" {
    command = "bash -c 'while ! curl -sf http://127.0.0.1:8761/actuator/health; do sleep 5; done'"
  }
}

# === API Gateway job ===
resource "nomad_job" "api_gateway" {
  jobspec    = file("${path.module}/nomad-jobs/api-gateway.nomad.hcl")
  depends_on = [null_resource.eureka_ready]
}

# === NATS job ===
resource "nomad_job" "nats_server" {
  jobspec = file("${path.module}/nomad-jobs/nats.nomad.hcl")
}

# === Postgres job ===
resource "nomad_job" "postgres" {
  jobspec = file("${path.module}/nomad-jobs/postgres.nomad.hcl")
}

resource "null_resource" "api_gateway_ready" {
  depends_on = [nomad_job.api_gateway]

  provisioner "local-exec" {
    command = "bash -c 'while ! curl -sf http://127.0.0.1:8080/api/health; do sleep 10; done'"
  }
}

resource "null_resource" "nats_ready" {
  depends_on = [nomad_job.nats_server]

  provisioner "local-exec" {
    command = "bash -c 'while ! curl -sf --max-time 5 http://127.0.0.1:8222/; do sleep 10; done'"
  }
}

resource "null_resource" "postgres_ready" {
  depends_on = [nomad_job.postgres]

  provisioner "local-exec" {
    command = "bash -c 'while ! nc -z 127.0.0.1 5432; do sleep 10; done'"
  }
}


# === Prometheus job ===
resource "nomad_job" "prometheus" {
  jobspec    = file("${path.module}/nomad-jobs/prometheus.nomad.hcl")
  depends_on = [null_resource.api_gateway_ready]
}

# === Grafana job ===
resource "nomad_job" "grafana" {
  jobspec    = file("${path.module}/nomad-jobs/grafana.nomad.hcl")
  depends_on = [null_resource.api_gateway_ready]
}

# === Auth service job ===
resource "nomad_job" "auth_service" {
  jobspec    = file("${path.module}/nomad-jobs/auth-service.nomad.hcl")
  depends_on = [
    null_resource.eureka_ready,
    null_resource.postgres_ready,
    null_resource.nats_ready
  ]
}

resource "null_resource" "auth_service_ready" {
  depends_on = [nomad_job.auth_service]

  provisioner "local-exec" {
    command = "bash -c 'while ! curl -sf http://127.0.0.1:8081/api/health; do sleep 10; done'"
  }
}

# === Customer service job ===
resource "nomad_job" "customer_service" {
  jobspec    = file("${path.module}/nomad-jobs/customer-service.nomad.hcl")
  depends_on = [
    null_resource.eureka_ready,
    null_resource.postgres_ready,
    null_resource.nats_ready,
    null_resource.auth_service_ready
  ]
}

# === Cart service job ===
resource "nomad_job" "cart_service" {
  jobspec    = file("${path.module}/nomad-jobs/cart-service.nomad.hcl")
  depends_on = [
    null_resource.eureka_ready,
    null_resource.postgres_ready,
    null_resource.nats_ready,
    null_resource.auth_service_ready
  ]
}

# === Product service job ===
resource "nomad_job" "product_service" {
  jobspec    = file("${path.module}/nomad-jobs/product-service.nomad.hcl")
  depends_on = [
    null_resource.eureka_ready,
    null_resource.postgres_ready,
    null_resource.nats_ready,
    null_resource.auth_service_ready
  ]
}

# === Order service job ===
resource "nomad_job" "order_service" {
  jobspec    = file("${path.module}/nomad-jobs/order-service.nomad.hcl")
  depends_on = [
    null_resource.eureka_ready,
    null_resource.postgres_ready,
    null_resource.nats_ready,
    null_resource.auth_service_ready
  ]
}

# === Angular Ecommerce job ===
resource "nomad_job" "angular_ecommerce" {
  jobspec    = file("${path.module}/nomad-jobs/angular-ecommerce.nomad.hcl")
  depends_on = [
    null_resource.api_gateway_ready,
    null_resource.auth_service_ready
  ]
}
