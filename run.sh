#!/bin/bash

# Load .env file if it exists
if [ -f .env ]; then
    export $(cat .env | grep -v '#' | xargs)
fi

# Run Spring Boot application
mvn spring-boot:run
