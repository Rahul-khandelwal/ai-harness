# Getting Started

### Install Docker
`brew install --cask docker`

### Build images and run containers
`docker compose build && docker compose up -d`

### Build and deploy a single image
`docker compose up -d --build order-service`

### Execute a basic test
`curl -X POST --location "http://localhost:8081/order" -H "Content-Type: application/json; charset=utf-8" -d '{"product":  "software", "price":  10.0, "quantity":  1}'`

### Stop all containers
`docker compose down`