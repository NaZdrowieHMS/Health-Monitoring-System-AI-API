# Health-Monitoring-System-AI-API

### How to run

1. Add to environment variables in project configuration (during run Spring boot application):
```
HMS_API_BASE_URL=
AI_MISTRALAI_API_KEY=
AI_HUGGING_FACE_BASE_URL=
AI_HUGGING_FACE_API_KEY=
```
secrets are available -> ask Karolina

### Run Docker
To build Docker image locally
1.  Build image with your GitHub credentials and token:
```
docker build --build-arg GITHUB_USERNAME=username --build-arg GITHUB_TOKEN=token -t hms-ai-app .
```
2.  Run container with secrets:
```
docker run -e HMS_API_BASE_URL="" -e AI_MISTRALAI_API_KEY="" -e AI_HUGGING_FACE_BASE_URL="" -e AI_HUGGING_FACE_API_KEY="" -p 8081:8081 hms-ai-app
```
Remember to fill the command above with correct credentials. (Put them inside the `""`)


### How to run via docker compose

1. Put all credentials in the docker-compose.yml
2. Update path for HMS Dockerfile here: 
```bash
services:
  hms:
    build:
      context: ../../Backend-API/Health-Monitoring-System-API/HealthMonitoringSystemApplication/
```
3. Run
```bash
docker-compose up --build
```



