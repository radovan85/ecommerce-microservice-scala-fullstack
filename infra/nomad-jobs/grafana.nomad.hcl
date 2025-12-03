job "grafana" {
  datacenters = ["dc1"]
  type        = "service"

  group "grafana" {
    count = 1

    # 🛜 Host networking: izlažemo Grafanu direktno na hostu
    network {
      mode = "host"
      port "ui" {
        static = 3000  
      }
    }

    volume "grafana-data" {
      type      = "host"
      source    = "grafana-data"
      read_only = false
    }

    task "grafana" {
      driver = "docker"

      config {
        image        = "grafana/grafana:10.2.3"
        ports        = ["ui"]
        network_mode = "host"
      }

      env {
        GF_SECURITY_ADMIN_USER     = "admin"
        GF_SECURITY_ADMIN_PASSWORD = "grafana123"
        GF_SERVER_HTTP_PORT        = "3000"  # Obavezno za promenu porta
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

      volume_mount {
        volume      = "grafana-data"
        destination = "/var/lib/grafana"
      }

      resources {
        cpu    = 700
        memory = 768
      }

      service {
        name     = "grafana"
        port     = "ui"
        tags     = ["dashboard", "metrics"]
        provider = "nomad"

        check {
          name          = "grafana-health"
          type          = "http"
          path          = "/"
          interval      = "10s"
          timeout       = "2s"
          address_mode  = "host"
        }
      }
    }
  }
}