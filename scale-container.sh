#!/bin/bash

# Base variables
BASE_NAME="simulator_outstation_dnp3"
BASE_PORT=30000
NUM_CONTAINERS=3
NETWORK="app-tier"  # Adjust if network differs

# Create containers with incrementing names and ports
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
done

echo "All containers created!"
