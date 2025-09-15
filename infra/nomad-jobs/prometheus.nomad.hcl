job "prometheus" {
  datacenters = ["dc1"]
  type        = "service"

  group "prometheus" {
    count = 1

    network {
      mode = "host"
      port "web" {
        static = 9090
      }
    }

    volume "prometheus-config" {
      type      = "host"
      source    = "prometheus-config"
      read_only = true
    }

    task "prometheus" {
      driver = "docker"

      config {
        image        = "prom/prometheus:latest"
        ports        = ["web"]
        network_mode = "host"
        args = [
          "--config.file=/etc/prometheus/prometheus.yml",
          "--web.listen-address=0.0.0.0:9090"
        ]
      }

      volume_mount {
        volume      = "prometheus-config"
        destination = "/etc/prometheus"
      }

      resources {
        cpu    = 700
        memory = 768
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

      service {
        name     = "prometheus"
        port     = "web"
        tags     = ["metrics", "scraper"]
        provider = "nomad"

        check {
          name     = "prometheus-health"
          type     = "http"
          path     = "/graph"
          interval = "10s"
          timeout  = "2s"
        }
      }
    }
  }
}
