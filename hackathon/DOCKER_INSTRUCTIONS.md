# Docker Setup Instructions for StockMaster

## Prerequisites
- Docker Desktop installed on Windows
- Docker Desktop running

## Files Created
1. **Dockerfile** - Multi-stage build for the Spring Boot application
2. **docker-compose.yml** - Orchestrates the app and MySQL database
3. **.dockerignore** - Excludes unnecessary files from Docker build

## Running the Application with Docker

### Option 1: Using Docker Compose (Recommended)

1. **Build and start all services:**
   ```powershell
   docker-compose up --build
   ```

2. **Run in detached mode (background):**
   ```powershell
   docker-compose up -d --build
   ```

3. **View logs:**
   ```powershell
   docker-compose logs -f app
   ```

4. **Stop all services:**
   ```powershell
   docker-compose down
   ```

5. **Stop and remove volumes (clean database):**
   ```powershell
   docker-compose down -v
   ```

### Option 2: Using Docker Commands Directly

1. **Build the application image:**
   ```powershell
   docker build -t stockmaster-app .
   ```

2. **Run MySQL container:**
   ```powershell
   docker run -d --name stockmaster-mysql -e MYSQL_ROOT_PASSWORD=5360 -e MYSQL_DATABASE=inventory_manage -p 3307:3306 mysql:8.0
   ```

3. **Run the application container:**
   ```powershell
   docker run -d --name stockmaster-app -p 8080:8080 --link stockmaster-mysql:mysql -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/inventory_manage?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC stockmaster-app
   ```

## IntelliJ IDEA Integration

### Running Docker Compose from IntelliJ:
1. Open `docker-compose.yml` file
2. You should see a green play button (►) in the gutter
3. Click the play button and select "Run 'docker-compose.yml'"
4. View logs in the Run tool window

### Using IntelliJ's Docker Plugin:
1. Go to **View → Tool Windows → Services**
2. Expand **Docker** section
3. Right-click on **docker-compose.yml** → **Up**
4. Monitor containers, view logs, and manage containers from this panel

### Debugging with Docker (Advanced):
1. Modify the `docker-compose.yml` to expose debug port:
   ```yaml
   app:
     environment:
       JAVA_TOOL_OPTIONS: "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
     ports:
       - "8080:8080"
       - "5005:5005"  # Debug port
   ```
2. Create a Remote JVM Debug configuration in IntelliJ
3. Set host to `localhost` and port to `5005`
4. Start debugging

## Accessing the Application

- **Application URL**: http://localhost:8080
- **API Base URL**: http://localhost:8080/api
- **MySQL Port**: 3307 (mapped to avoid conflicts with local MySQL)
- **MySQL Connection String**: `jdbc:mysql://localhost:3307/inventory_manage`

## Useful Docker Commands

### Container Management:
```powershell
# List running containers
docker ps

# List all containers
docker ps -a

# View container logs
docker logs stockmaster-app

# Access container shell
docker exec -it stockmaster-app sh

# Access MySQL shell
docker exec -it stockmaster-mysql mysql -u root -p5360

# Restart a container
docker restart stockmaster-app

# Remove a container
docker rm -f stockmaster-app
```

### Image Management:
```powershell
# List images
docker images

# Remove an image
docker rmi stockmaster-app

# Clean up unused images and containers
docker system prune -a
```

### Docker Compose Commands:
```powershell
# Start services
docker-compose up

# Start in background
docker-compose up -d

# Stop services
docker-compose down

# Rebuild and start
docker-compose up --build

# View logs
docker-compose logs -f

# View logs for specific service
docker-compose logs -f app

# Restart a service
docker-compose restart app

# Scale a service (if configured)
docker-compose up -d --scale app=3
```

## Troubleshooting

### Problem: Port already in use
**Solution**: Stop local MySQL or change port mapping in docker-compose.yml

### Problem: Cannot connect to database
**Solution**: 
- Wait for MySQL healthcheck to pass
- Check logs: `docker-compose logs mysql`
- Verify connection string in environment variables

### Problem: Build fails
**Solution**: 
- Ensure Maven dependencies are accessible
- Check internet connection
- Try: `docker-compose build --no-cache`

### Problem: Application crashes on startup
**Solution**:
- Check logs: `docker-compose logs app`
- Verify environment variables in docker-compose.yml
- Ensure MySQL is fully started before app starts

## Production Considerations

1. **Use environment variables for secrets** (not hardcoded)
2. **Use Docker secrets** for sensitive data
3. **Configure proper logging** (external log management)
4. **Set resource limits** in docker-compose.yml:
   ```yaml
   app:
     deploy:
       resources:
         limits:
           cpus: '1'
           memory: 1G
   ```
5. **Use production-ready MySQL configuration**
6. **Set up proper backup strategy** for MySQL data volume
7. **Use health checks** for monitoring
8. **Consider using Kubernetes** for production orchestration

## Next Steps

1. Test the containerized application locally
2. Set up CI/CD pipeline for automated builds
3. Push images to Docker Hub or private registry
4. Deploy to cloud platform (AWS ECS, Azure Container Instances, Google Cloud Run)

