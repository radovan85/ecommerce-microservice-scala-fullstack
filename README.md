🚀 Fullstack Scala Microservices E-commerce Platform
A fully containerized, production-grade e-commerce platform built with idiomatic Scala microservices and a modular orchestration layer. Designed for observability, and rollback-friendly deployment — this project runs seamlessly on Linux or WSL (Ubuntu), and scales from local testing to cloud-ready infrastructure.

Whether you're refining your Scala skills or exploring orchestration across frameworks, this platform is your playground for precision engineering.

🧰 Tech Stack Overview


Service	Framework	Port
Eureka Server Spring Boot 8761
API Gateway	Scalatra	8080
Auth Service	Spring MVC	8081
Customer Service	Scalatra	8083
Cart Service	Play Framework	9001
Product Service	Play Framework	9002
Order Service	Play Framework	9003

🎯 Project Goals
Strengthen Scala proficiency across multiple frameworks

Showcase orchestration with modular job definitions

Enable full observability via Prometheus and Grafana

Provide a clean, reproducible infrastructure for mentees and collaborators


🚀 Project Setup & Execution
This project is designed to run on Linux or WSL (Windows Subsystem for Linux). It has been tested and verified on WSL Ubuntu, and requires the following tools to be installed and running:

✅ Prerequisites
[x] Docker installed and running

[x] Nomad installed

[x] Terraform installed

[x] WSL or native Linux environment (macOS works too)

🧱 Step-by-Step Initialization
Before launching the orchestration, you must prepare the host volumes that Nomad will mount into containers. These folders are required for persistence and configuration sharing across services.

# Create host volumes for Postgres, Prometheus, and Grafana
sudo mkdir -p /opt/nomad-volumes/pgdata
sudo mkdir -p /opt/nomad-volumes/prometheus
sudo mkdir -p /opt/nomad-volumes/grafana

# Assign ownership of Grafana volume to UID 472 (used by grafana/grafana Docker image)
sudo chown 472:472 /opt/nomad-volumes/grafana

🔍 Why this matters: Nomad validates all host_volume paths before starting. Grafana requires write access to /var/lib/grafana, which is mounted from the host. Without proper ownership, Grafana will fail with GF_PATHS_DATA is not writable.


🧭 Starting Nomad Agent
Once the volumes are created, you can start the Nomad agent using the provided configuration file:

nomad agent -config=./nomad-config/nomad.hcl


This launches Nomad in server + client mode, with Consul integration and Docker driver enabled. The agent will be ready to accept job submissions.

📦 Terraform Orchestration
Navigate to the infrastructure folder to initialize and apply the orchestration:

cd infra

terraform init       # Initializes Terraform modules and providers
terraform plan       # Shows the execution plan
terraform apply      # Deploys all services via Docker containers


🧠 Note: All Nomad job definitions are located in infra/nomad-jobs. You can manually run each job using Nomad CLI if needed:

nomad job run infra/nomad-jobs/auth-service.nomad.hcl

However, this manual approach lacks orchestration — Terraform ensures proper sequencing, dependency resolution, and resource allocation across all services.

🧪 Local Testing
Once deployed, you can access:

Grafana at http://localhost:3000 (default login: admin/admin)

Prometheus at http://localhost:9090

Microservices via their respective ports (see prometheus.yml for targets)
