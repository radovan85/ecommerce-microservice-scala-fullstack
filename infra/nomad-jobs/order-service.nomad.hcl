job "order-service" {
  datacenters = ["dc1"]
  type        = "service"

  group "order" {
    count = 1

    network {
      mode = "host"
      port "http" {
        static = 9003
      }
    }

    task "order" {
      driver = "docker"

      config {
        image        = "ghcr.io/radovan85/ecommerce-scl/order-service:main"
        ports        = ["http"]
        network_mode = "host"
      }

      env {
        DB_URL      = "jdbc:postgresql://localhost:5432/ecommerce-db"
        DB_USERNAME = "postgres"
        DB_PASSWORD = "1111"
        PLAY_PORT = "9003"
        PLAY_SECRET = "Wm3xGzcK4yZ5eanK41YAT[=<qyz6=/fJfyanYtEXyl/r/JNI28X;>0[q3epxiGnb"
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
        cpu    = 700
        memory = 768
      }

      service {
        name     = "order-service"
        port     = "http"
        tags     = ["order", "play", "metrics"]
        provider = "nomad"

        check {
          name     = "order-health"
          type     = "http"
          path     = "/api/health"
          interval = "10s"
          timeout  = "2s"
        }
      }
    }
  }
}
