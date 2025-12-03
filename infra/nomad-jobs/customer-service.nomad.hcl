job "customer-service" {
  datacenters = ["dc1"]
  type        = "service"

  group "customer" {
    count = 1

    network {
      mode = "host"
      port "http" {
        static = 8083
      }
    }

    task "customer" {
      driver = "docker"

      config {
        image        = "ghcr.io/radovan85/ecommerce-scl/customer-service:main"
        ports        = ["http"]
        network_mode = "host"
      }

      env {
        PERSISTENCE_URL      = "jdbc:postgresql://localhost:5432/ecommerce-db"
        PERSISTENCE_USERNAME = "postgres"
        PERSISTENCE_PASSWORD = "1111"
        SCALATRA_PORT = "8083"
      }
      
      lifecycle {
        hook    = "prestart"
        sidecar = false
      }

      restart {
        attempts = 10
        interval = "5m"
        delay    = "15s"
        mode     = "delay"
      }

      resources {
        cpu    = 500
        memory = 512
      }

      service {
        name     = "customer-service"
        port     = "http"
        tags     = ["customer", "scalatra", "metrics"]
        provider = "nomad"

        check {
          name     = "customer-health"
          type     = "http"
          path     = "/api/health"
          interval = "10s"
          timeout  = "2s"
        }
      }
    }
  }
}
