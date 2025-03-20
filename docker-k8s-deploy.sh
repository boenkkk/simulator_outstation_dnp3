#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Define variables
APP_NAME="simulator_outstation_dnp3"
DOCKERFILE="DOCKERFILE"
REGISTRY_IP="1.2.3.4"
REGISTRY_PORT=""
NAMESPACE="scada"
DEPLOYMENT_FILE="k8s-deploy.yaml"
FULL_IMAGE_NAME="$REGISTRY_IP/$NAMESPACE/$APP_NAME"

# Login to Docker
echo "Logging into Docker..."
docker login || { echo "Docker login failed!"; exit 1; }

# Build the Docker image
echo "Building Docker image..."
docker build -t $APP_NAME:latest -f $DOCKERFILE . || { echo "Docker build failed!"; exit 1; }

# List images and filter by name
echo "Checking if the image was built successfully..."
docker images | grep $APP_NAME || { echo "Image not found!"; exit 1; }

# Tag the image for the remote repository
echo "Tagging image..."
docker tag $APP_NAME:latest $FULL_IMAGE_NAME || { echo "Docker tag failed!"; exit 1; }

# Push the image to the remote repository
echo "Pushing image to registry..."
docker push $FULL_IMAGE_NAME || { echo "Docker push failed!"; exit 1; }

# Apply the Kubernetes deployment
echo "Applying Kubernetes deployment..."
kubectl apply -f $DEPLOYMENT_FILE || { echo "Kubernetes deployment failed!"; exit 1; }

# Verify the deployment status
echo "Verifying deployment status..."
kubectl rollout status deployment/$APP_NAME -n $NAMESPACE || { echo "Deployment verification failed!"; exit 1; }

# Display deployed resources
echo "Deployment and Service status:"
kubectl get deployments,pods,services -n $NAMESPACE

echo "Deployment completed successfully!"
