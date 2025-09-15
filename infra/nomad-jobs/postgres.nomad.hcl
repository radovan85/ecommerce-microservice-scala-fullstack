job "postgres" {
  datacenters = ["dc1"]
  type        = "service"

  group "db" {
    count = 1

    network {
      mode = "host"
      port "db" {
        static = 5432
      }
    }

    volume "pgdata" {
      type      = "host"
      source    = "pgdata"
      read_only = false
    }

    task "postgres" {
      driver = "docker"

      config {
        image        = "postgres:15"
        ports        = ["db"]
        network_mode = "host"
        args = ["-c", "listen_addresses=*"]
      }

      env {
        POSTGRES_DB       = "ecommerce-db"
        POSTGRES_USER     = "postgres"
        POSTGRES_PASSWORD = "1111"
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
        volume      = "pgdata"
        destination = "/var/lib/postgresql/data"
      }

      resources {
        cpu    = 700
        memory = 768
      }

      service {
        name     = "postgres"
        port     = "db"
        tags     = ["sql", "database"]
        provider = "nomad"

        check {
          name     = "postgres-tcp"
          type     = "tcp"
          interval = "10s"
          timeout  = "2s"
        }
      }
    }
  }
}
