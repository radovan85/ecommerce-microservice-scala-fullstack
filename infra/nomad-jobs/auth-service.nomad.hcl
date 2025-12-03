job "auth-service" {
  datacenters = ["dc1"]
  type        = "service"

  group "auth" {
    count = 1

    network {
      mode = "host"

      port "http" {
        static = 8081
      }
    }

    task "auth" {
      driver = "docker"

      config {
        image        = "ghcr.io/radovan85/ecommerce-scl/auth-service:main"
        ports        = ["http"]
        network_mode = "host"
      }

      env {
        PERSISTENCE_URL       = "jdbc:postgresql://localhost:5432/ecommerce-db"
        PERSISTENCE_USERNAME  = "postgres"
        PERSISTENCE_PASSWORD  = "1111"
        SPRING_PORT  = "8081"

        # JWT Configuration
        JWT_EXPIRATION  = "7200"
        JWT_PRIVATE_KEY = <<EOF
-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC7ZeESswF22oOV0SkkbUD1eCC6AyN5WIS0M0rM5l/jEIHHVSw595J3JKSPzbruj1zuWpjfvu22OKg2L1CWEqhXrmInRhVdpQ+hxu2DZ/s2b2KzNwrGHhRHNIodZbaB3WHg/aQcAoKKhhve8VHjJb7Fxvfzwu26Iz8uezUMqz6TVd0owz7Iw9yrR/BjDbgI73e2B76MSics1lCLZeL3JBG1O7hllUf03P4IPUh8rJSTPr5nXows2ur+PiLWrFSN7OOz5YkNZHXUIZ3UHEQpLiE60gNuaGYao0Npbx3eSQdFpdVQbp0ojJU4pSH3v7/d51ZIN5TXhDpQV1WUTKEWMl/FAgMBAAECggEAUfpr/w+i1Nkblib1ThOjRp7iBi2IT7W6+8+yabdf/AeFFmu6mLMmdgtoF+aCX4kEuNqWJM8z0zKu12FUagpIbW8CTjb48snZ8EYDMiiDO1l3vnmWM3wGF+4ye1C7Cc2MW23p5Dzu2WV4fMtprKoe7gsv/glokLSJl65bUyr4iV/WsNbCZAunR7/IzyQfhUQdbsJuUUeaCJcNrwme/TxkiJgT3VkWWw+Y2V0CLFDk4aUWZ9OMQCHDANPPRnps+0rs6Tvm8rDiIv2XkfV9JYFxao0y+9gTDkISa2x1kg35Ak9U9ehJy6XaNzkn2wZL4cEpETaUktV5hEVUkIg1dcVuMwKBgQDu2W/PDEDPHuPlJ5LM3EIuEzHShYBZ/qpgtkVatk+nmseBkF+CPE93W1zcWrI5Zn8htdCH7d9frzzzssLM7WnCZe2x8AuQ2iCTVZ2K0k8gvFNOA6VQXECsIJ2K4IXESA8SXsuFIxMNJRTobgctZ5aa4oOlDVq/mzP72gtS45qrxwKBgQDI2qfNqnuxNfscL3UXPw6H7INOk+9sKUgzRCN01CbUeEl7sA9WruDYvaf6yiKHDH72kFQ9EPuh2+AvT9RWGlYP1Ta29Y8/ugg7qJ5i8qtq8ytVwtBYjfk39GpAssBYfpDces2WOHALmuXTRL2KyXTG5RsDuIRnXUm9PxOlMktgEwKBgDRU0amEnsKCmx3/IKvf6mQb8oOUmn2dTYkpBmMMpMEtKV8a2cI4IpUdIGhrOrdW3K3vHwRZOuLFC069sO6jadOc74pX9MDE+fQuAvmCgLHEcWAbbmIABG9yKfJepRBPVXYJ7P97otXzdPRD/zCUjKvcy0kjdumaQLaCnI6Jrb7LAoGAOiKXuT6kKKnAMetGj+DvesYpR2AoR303aadKP/F7/7mFQ2i1N9jMOc/DRCKnlcE3KnSh/T7iJno2zFrl0bozuMd028X9nWtiIKpwlaE5nm4d1+fYWBlXzitPacSTScnwcfCVeuqA+8rsxJa76eTfiQYdKJmKsUReKImOTR1elcMCgYEA4+vLhiflzUMf5uZJniQj3jOytSS1K5lkD17MB9kZKr+ERffkXvXYxbZ5/pWozuU9xhUfiFfR4oCq+utrb0ueuCdtQLYhJjTYjhkZ9yhgEkl+mCYskImp9m944lWA5tEPHTmjzpWmo8dOAbMGzrxsYvvxvTgmpnaQ5tII/84IxZQ\=\n-----END PRIVATE KEY-----
EOF

        JWT_PUBLIC_KEY = <<EOF
-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu2XhErMBdtqDldEpJG1A9XggugMjeViEtDNKzOZf4xCBx1UsOfeSdySkj8267o9c7lqY377ttjioNi9QlhKoV65iJ0YVXaUPocbtg2f7Nm9iszcKxh4URzSKHWW2gd1h4P2kHAKCioYb3vFR4yW+xcb388LtuiM/Lns1DKs+k1XdKMM+yMPcq0fwYw24CO93tge+jEonLNZQi2Xi9yQRtTu4ZZVH9Nz+CD1IfKyUkz6+Z16MLNrq/j4i1qxUjezjs+WJDWR11CGd1BxEKS4hOtIDbmhmGqNDaW8d3kkHRaXVUG6dKIyVOKUh97+/3edWSDeU14Q6UFdVlEyhFjJfxQIDAQAB\n-----END PUBLIC KEY-----
EOF
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
        name     = "auth-service"
        port     = "http"
        tags     = ["auth", "security", "metrics"]
        provider = "nomad"

        check {
          name     = "auth-health"
          type     = "http"
          path     = "/api/health"
          interval = "10s"
          timeout  = "2s"
        }
      }
    }
  }
}
