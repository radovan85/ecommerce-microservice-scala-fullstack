job "eureka-server" {
  datacenters = ["dc1"]
  type        = "service"

  group "eureka" {
    count = 1

    # Koristimo host mrežu da izbegnemo CNI/iptables komplikacije u WSL-u
    network {
      mode = "host"
      port "http" {
        static = 8761
      }
    }

    update {
      stagger      = "30s"
      max_parallel = 1
    }

    task "eureka" {
      driver = "docker"

      config {
        image        = "ghcr.io/radovan85/ecommerce-scl/eureka-server:main"
        ports        = ["http"]
        network_mode = "host"  # Mora da se poklapa sa group.network.mode
      }

      env {
        JAVA_OPTS                             = "-Xms128m -Xmx256m"
        SERVER_PORT                           = "8761"
        EUREKA_CLIENT_REGISTER_WITH_EUREKA    = "false"  # Standalone režim
        EUREKA_CLIENT_FETCH_REGISTRY          = "false"
        EUREKA_INSTANCE_HOSTNAME              = "eureka-server"
        EUREKA_INSTANCE_PREFER_IP_ADDRESS     = "false"
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
        name     = "eureka-server"
        port     = "http"
        tags     = ["eureka", "discovery"]
        provider = "nomad"

        check {
          name     = "eureka-health"
          type     = "http"
          path     = "/actuator/health"
          interval = "10s"
          timeout  = "2s"
          method   = "GET"
        }
      }
    }
  }
}
