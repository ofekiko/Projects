FROM python:3.9-slim

# Set working directory
WORKDIR /app

# Copy client code
COPY . /app

# Run with command passed in docker-compose.yml
CMD ["python", "client.py", "server" , "5555"]
