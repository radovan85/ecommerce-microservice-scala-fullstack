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
        PERSISTENCE_URL      = "jdbc:postgresql://localhost:5432/ecommerce-db"
        PERSISTENCE_USERNAME = "postgres"
        PERSISTENCE_PASSWORD = "1111"
        SPRING_PORT = "8081"

        # JWT Configuration
        JWT_EXPIRATION = "7200"
        JWT_PRIVATE_KEY = <<EOF
-----BEGIN PRIVATE KEY-----
MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQD3mSA2p3UhKz62QqYRtO8SMqGPwpCEG+MdX+874jJ6BFY408aXab5nrMl3Zn76DpWBLl+fZezlGgvM84B2b7qgdPt1N53TrUn7dFH49jCOqdorTIDrR9eJmTE+T4K/g5/SxXe4/CZpFCuo1fHA6wuQBTJcATb07j5c0C6MRFJTYhBIJVUZHqseOUeWx9WsSdTNqgyQQ97xhL8n1uwT++KUIzt4uWa9UmEj1rCGEj83BR0sD5BQbPdYWF9aSEza54VhegHcOdMdd4EOGStXTPOuXRVEC0C6cucpvFl79LbwSROJqIb+IUBBnsSvM2iPrLtU9Bvf3AO7KJJfFn2fLbmZAgMBAAECggEADUUMRk2XgBN1oRZFmPuX2AN1Z/NjJCdr9sQwWcbj0mkG13UXJsCEs0D+C6uHON7c+Z9LAeuYJIeus/llbEOM7qJSYzv7tg9qoILBjWyhWdYF0q3/F00CgtX+XwLrJJk48LaDVftSki1h8SKlOiQGur1ij26Du+EEX9xK8FrNiDklXhibNl0WWeqexeppWRJ2/ZNdwzW10fBSykSxo0i+UCbwsKFmN3wgSIReOPSuz2A2l0F9M+LjXDHFOv2U4X5ruCaGSf8leSqeP/NPS9fg1Ad2eZn83vJpYM6zZqo/5XOka82fMzJJtrChv50XMIAtoRsuBHDSCqrW6LCMfubNYQKBgQD+b4JzsNscCuRFDXmYTQKDL2raCCLI1C8vGDJFDWkaHcCe5RY5r+G4+++V2c+HnuWduxn861JC3VNPEyGpwjS7lXUv6UWsAC6PuwxECXFOLz4WdkGMJQuYFJBPz5fz7TlQvjCMJEp3VdfpfAAseWfRepblrLSmdk81dy6yvN2CuQKBgQD5HtqY0NwOL9wNYm9txzn57N0rZtTyUS56x/PirLbx+mhFkJ2C3XA8h87/iTgHey+ZSAKdufRJIgbPJ3ympOX5fGkT+z/F+Nzu12DQFD0ZglffbmSf8+foyBJuSGYDgn+adPNrdm8DFcBtuhFP2bYV5oNuVLFYqOuBeVB81jX94QKBgASroVZkUbFTFduaorfD+h7UWP7FlXXgRzlzFUElPN+o2idQhzbp3pfZsA0tIV0Y8pgRnimBXFIz49qYvE50bSlgVfVaL9g3bKNgyJnbHoRT46zf4NYku/C/t+JvKhflK98qjpOT+vXz0nHLv+kRFXHH8Usn/2zq0QtnM6k+REapAoGAVMNtx49RQLaCHj+/AFBIZsR0YJ8ll6mNshM05Yy01tLIBdt17KAh30aqZQIwgUMl5/a7hSVPfE7KmFOsMxtqcKAnNLhJc7gAnHfGJiasv3A9K/Xo4Q/hebARrVqO82TEvC5M4OS0Uw1kozErUMsYDI2ZbMH+cddQAobfnwpX5qECgYB7ysKfBaLs3MwTPJkIravNkQgYtxiewxJamxit0I2oA0HDWhBtZto2Ym4NXO3VJcTWFuLmB642AjnbUnrLwWKXlE2IaPMHGsTeXYIgPCSjyI/k/0CSwROBUwpturSqyibeI4BelklAeQ5MP6R8Ia28F39QHb5FsJ3iaoDt7hfB9w==
-----END PRIVATE KEY-----
EOF

        JWT_PUBLIC_KEY = <<EOF
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA95kgNqd1ISs+tkKmEbTvEjKhj8KQhBvjHV/vO+IyegRWONPGl2m+Z6zJd2Z++g6VgS5fn2Xs5RoLzPOAdm+6oHT7dTed061J+3RR+PYwjqnaK0yA60fXiZkxPk+Cv4Of0sV3uPwmaRQrqNXxwOsLkAUyXAE29O4+XNAujERSU2IQSCVVGR6rHjlHlsfVrEnUzaoMkEPe8YS/J9bsE/vilCM7eLlmvVJhI9awhhI/NwUdLA+QUGz3WFhfWkhM2ueFYXoB3DnTHXeBDhkrV0zzrl0VRAtAunLnKbxZe/S28EkTiaiG/iFAQZ7ErzNoj6y7VPQb39wDuyiSXxZ9ny25mQIDAQAB
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