#!/bin/bash

# This script is used to clean the project, build it, and then create a Docker image and run it.
BLUE='\033[0;34m'
echo -e "${BLUE}Cleaning and building the project..."
./gradlew clean build

echo -e "${BLUE}Building the Docker image..."
./gradlew docker

echo -e "${BLUE}Stopping and removing existing Docker containers..."
docker-compose -f ../deployment/docker-compose.yml up -d
