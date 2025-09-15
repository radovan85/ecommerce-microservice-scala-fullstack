job "nats-server" {
  datacenters = ["dc1"]
  type        = "service"

  group "nats" {
    count = 1

    network {
      mode = "host"
      port "client" {
        static = 4222
      }
      port "monitor" {
        static = 8222
      }
    }

    task "nats" {
      driver = "docker"

      config {
        image        = "nats:2.10.11"
        ports        = ["client", "monitor"]
        network_mode = "host"
        args         = [
          "--name", "nats-server",
          "--http_port", "8222",
          "--log", "debug"
        ]
      }

      resources {
        cpu    = 500
        memory = 512
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
        name     = "nats-server"
        port     = "client"
        tags     = ["messaging", "pubsub"]
        provider = "nomad"

        check {
          name     = "nats-client-port"
          type     = "tcp"
          interval = "10s"
          timeout  = "2s"
        }
      }

      service {
        name     = "nats-monitor"
        port     = "monitor"
        tags     = ["monitoring", "nats"]
        provider = "nomad"

        check {
          name     = "nats-monitor-http"
          type     = "http"
          path     = "/"
          interval = "10s"
          timeout  = "2s"
        }
      }
    }
  }
}
