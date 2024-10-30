#!/bin/bash

# Base variables
BASE_NAME="simulator_outstation_dnp3"
BASE_PORT=30000
NUM_CONTAINERS=3
NETWORK="app-tier"  # Adjust if network differs
DOCKERFILE_PATH="."  # Adjust if Dockerfile is in a different directory

# Step 1: Build the Docker image
echo "Building Docker image: $BASE_NAME"
sudo docker build -t "$BASE_NAME" "$DOCKERFILE_PATH"

if [ $? -ne 0 ]; then
  echo "Docker build failed! Exiting."
  exit 1
fi

# Step 2: Create containers with incrementing names and ports
for i in $(seq 1 $NUM_CONTAINERS); do
  CONTAINER_NAME="${BASE_NAME}_${i}"
  PORT=$((BASE_PORT + i - 1))

  echo "Creating container: $CONTAINER_NAME with port: $PORT"

  sudo docker run -d \
    --name "$CONTAINER_NAME" \
    --hostname "$CONTAINER_NAME" \
    --network "$NETWORK" \
    -p "${PORT}:20000" \
    -e "SPRING_PROFILES_ACTIVE" \
    "$BASE_NAME"

  if [ $? -ne 0 ]; then
    echo "Failed to create container: $CONTAINER_NAME"
  fi
done

echo "All containers created!"
