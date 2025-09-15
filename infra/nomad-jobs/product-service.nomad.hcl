job "product-service" {
  datacenters = ["dc1"]
  type        = "service"

  group "product" {
    count = 1

    network {
      mode = "host"
      port "http" {
        static = 9002
      }
    }

    task "product" {
      driver = "docker"

      config {
        image        = "ghcr.io/radovan85/ecommerce-scl/product-service:main"
        ports        = ["http"]
        network_mode = "host"
      }

      env {
        DB_URL      = "jdbc:postgresql://localhost:5432/ecommerce-db"
        DB_USERNAME = "postgres"
        DB_PASSWORD = "1111"
        PLAY_PORT = "9002"
        PLAY_SECRET = "4eNPew?:b<22r?9;HEN>FveN<Q83@s=oQ1v3ILZ0V^03XVSKVOIj^lm8/xnf/Zwo"
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
        name     = "product-service"
        port     = "http"
        tags     = ["product", "play", "metrics"]
        provider = "nomad"

        check {
          name     = "product-health"
          type     = "http"
          path     = "/api/health"
          interval = "10s"
          timeout  = "2s"
        }
      }
    }
  }
}
