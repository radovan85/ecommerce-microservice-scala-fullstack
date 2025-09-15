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
        DB_URL      = "jdbc:postgresql://localhost:5432/ecommerce-db"
        DB_USERNAME = "postgres"
        DB_PASSWORD = "1111"

        # JWT Configuration
        JWT_EXPIRATION = "7200"
        JWT_PRIVATE_KEY = <<EOF
-----BEGIN PRIVATE KEY-----
MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCugC5RPMTMFM+S92/rDjF+xR8JM3KNvLcVEOx1/ofdHZyHaKalw6Gi1dXk5ldr0q/FwYCJZ40m0Pwc6k1lWx4awEqNgS7mok2XFXLvOKRX6cFyr3/VSp5YbZ739KCzUanlw9uAR4uRu1BeRewA811gjEZpPV+RNlmsZTxXIM64nUpjmtahhzcnskuecele0CCzbNdXULwOdPhrua5fMTQZXGVic0lpHzqTvnlHtgEDvEt4k/WghZfMphcGJWb22KUm8V6q7xtyUjAZxSthG8C0urQPbbIftnPbhRIHnrVTQZOkmYfWLWEBm83M064nc8S7vVmaFD5S4r5qIp5MPW/vAgMBAAECggEAMMXP7FHVWYs5h0TUibjSWbHj3lD8ZCRKcb9ksAgf1kwX+3rjFTLMQzSMT6mX5qGhEl9mu0DvoVlJFhXg4wLoM6bqT4m7X6gekqtij/1fZdkGgjUOQEHWZzXgeEJuBiC9oWQwPjcBCg72RBs7/voGb91VMsX+J5quzrIO/vVDAjMxqZTP3SmhK6ldpI+JGA/X/33i1N/Wpi7vPv23t5nBFTGqYuijnj8zRl9qoYPnkCouLCPtMPbVOeVbO2CTaNBuaeAzOrvrWKIolGfBHxVfNWyUD+OuR/Ei30Dlivp0QMOcoipYJcFMkxTNNuiEWau6ySXEhifrQK2T3UBy6p1tlQKBgQC/x/pG7TJFlJEj89h0E4I//o9FTcJesMgkODTV/vDA7UvAc7kJ5UKX2BnUmkpv0bZHmEcqBdGxvu/YO0oBrMzNB1gIqAj5oSAVp0gIy5zyIToXQGbPtaSPQvo5NWePyDzVJcPDNw3kfYpv2gEX6LKArDe4Xvb44XgW2bg1fKXSawKBgQDo7t+0UGtpdEqmzCVIcBFz9PPmLKdaWpXXASFJTyjtHGNGRib2+cxjfCKFvHUh5+gsG5VjM0g78CUnmnuYXXCVvdSQRR0gGPCfyBQO8YEmmRh8miV7WxM68Hd+Ur4394qyGiPv0bAWonlFsNVEQQgogl3aQLu+OgpOMR4wyAphjQKBgB3YajuVeEhZyYiVzy6pRpDc9cDsS/5edpckZByVczQi+bH2kGpY0lc9Gy/53vGt6jcUHKcOhsaplzvIZkkCclDJ/spCFDt89rhH8y7jOQZ5Y0fxDs7uTrC41KYIiJhYqhblrKR5seDIJL4paBgnb3gU1QsNRFQRCpZLxZ4VSt0HAoGAbA40FG+iBF+AKfRV706dqg8d/c8FK3MOQ5kwmdNxBt2+Z+EvPi8PU69ITIXqSHpOR/fbyxMFFVkHw2F0qG/gRHa1Xt/Q7QzkwxqLw4rhAMjQhSz06k6LkzHuHNnBiDD7VDewKBY7+6QPYfbwWCNa53/ijdatwUcndLJwcOy7dBkCgYEAnkCTKO3IvEbMI2QEAw57fv6xonsvxv8xDe1QEoayTHs6SHo/cbaNvLOhfDS3pK4krgPcO4rE2R9jaXYliS84cTOyvaHJfTy1NvA9KxjNrvOI1/6Rz8Nih4bADDPBenwjuFSkmBgd/CqZCSkHK4EpD7sO8DOZbhB+LQHl2hERI2U=
-----END PRIVATE KEY-----
EOF

        JWT_PUBLIC_KEY = <<EOF
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAroAuUTzEzBTPkvdv6w4xfsUfCTNyjby3FRDsdf6H3R2ch2impcOhotXV5OZXa9KvxcGAiWeNJtD8HOpNZVseGsBKjYEu5qJNlxVy7zikV+nBcq9/1UqeWG2e9/Sgs1Gp5cPbgEeLkbtQXkXsAPNdYIxGaT1fkTZZrGU8VyDOuJ1KY5rWoYc3J7JLnnHpXtAgs2zXV1C8DnT4a7muXzE0GVxlYnNJaR86k755R7YBA7xLeJP1oIWXzKYXBiVm9tilJvFequ8bclIwGcUrYRvAtLq0D22yH7Zz24USB561U0GTpJmH1i1hAZvNzNOuJ3PEu71ZmhQ+UuK+aiKeTD1v7wIDAQAB
-----END PUBLIC KEY-----
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
