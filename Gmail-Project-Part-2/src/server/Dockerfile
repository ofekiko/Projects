FROM gcc:11

# Create working directory
WORKDIR /app

# Install cmake
RUN apt-get update && apt-get install -y cmake

# Copy all source files and the data directory
COPY . .

# Ensure data folder is available at runtime (relative to /app/build)
RUN mkdir -p build && \
    cp -r data build/data && \
    cd build && cmake .. && cmake --build .

# Set working directory to build so the binary runs from there
WORKDIR /app/build

# Run the server with port and parameters
CMD ["./server","5555", "8", "2"]
