# TeeShirtBazz Deployment Notes

## Local Docker
Run the full stack with:

```bash
docker compose up --build
```

Services:
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- MySQL: `localhost:3306`

Docker Compose persists:
- MySQL data in `mysql-data`
- Uploaded custom logos in `upload-data`

## Environment Variables
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`
- `FRONTEND_ORIGIN`
- `UPLOAD_DIR`

## AWS Target
- EC2: run Docker Compose or separated backend/frontend containers.
- RDS MySQL: replace local MySQL URL with the RDS endpoint.
- S3 future upgrade: replace local upload storage with S3 object storage for custom logos and product assets.
- GitHub Actions: builds backend, frontend, and Docker images on pull requests and pushes to `main`.

## Production Hardening Later
- Use a long random JWT secret.
- Replace `ddl-auto: update` with migrations.
- Add HTTPS through a load balancer or reverse proxy.
- Move uploaded files to S3.
- Add real payment gateway integration only after the simulated flow is stable.
