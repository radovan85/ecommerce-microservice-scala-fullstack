# === Osnovna konfiguracija agenta ===
region     = "global"         # Možeš promeniti ako simuliraš više regiona
datacenter = "dc1"            # Lokalni datacenter za testiranje
data_dir   = "/tmp/nomad"     # Gde Nomad čuva stanje i job definicije

log_level  = "INFO"           # Možeš staviti "DEBUG" za dublje troubleshooting
bind_addr  = "0.0.0.0"        # Omogućava pristup sa svih interfejsa

# === Pokrećemo kao server ===
server {
  enabled          = true
  bootstrap_expect = 1        # Očekujemo samo jedan server (lokalni test)
}

# === Pokrećemo kao client ===
client {
  enabled = true

  # === Host volume definicije za konfiguraciju i persistenciju ===
  host_volume "pgdata" {
    path      = "/opt/nomad-volumes/pgdata"
    read_only = false
  }

  host_volume "prometheus-config" {
    path      = "/opt/nomad-volumes/prometheus"
    read_only = true
  }

  host_volume "grafana-data" {
    path      = "/opt/nomad-volumes/grafana"
    read_only = false
  }
}

# === Consul integracija ===
consul {
  address = "127.0.0.1:8500"  # Consul je u Dockeru sa host mrežom, dostupan lokalno

  # Ako koristiš ACL token u Consul-u, dodaj ovde:
  # token = "your-consul-token"
}

# === Docker plugin konfiguracija ===
plugin "docker" {
  config {
    allow_privileged = false  # Za sigurnost; možeš staviti true ako testiraš specifične image-e
  }
}
