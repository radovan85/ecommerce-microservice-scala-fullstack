job "cart-service" {
  datacenters = ["dc1"]
  type        = "service"

  group "cart" {
    count = 1

    network {
      mode = "host"
      port "http" {
        static = 9001
      }
    }

    task "cart" {
      driver = "docker"

      config {
        image        = "ghcr.io/radovan85/ecommerce-scl/cart-service:main"
        ports        = ["http"]
        network_mode = "host"
      }

      env {
        PERSISTENCE_URL      = "jdbc:postgresql://localhost:5432/ecommerce-db"
        PERSISTENCE_USERNAME = "postgres"
        PERSISTENCE_PASSWORD = "1111"
        PLAY_PORT = "9001"
        PLAY_SECRET = "4Z2PJ;WnJ:Qo`>JTW4[7dSIIX5<w9/1rhl;yw_Q/qZxJ?HbLOhoZX0iAboYKT`65"
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
        name     = "cart-service"
        port     = "http"
        tags     = ["cart", "play", "metrics"]
        provider = "nomad"

        check {
          name     = "cart-health"
          type     = "http"
          path     = "/api/health"
          interval = "10s"
          timeout  = "2s"
        }
      }
    }
  }
}
