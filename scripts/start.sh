#!/usr/bin/env bash

preconditions() {
    if [[ -f "env.properties" ]]
    then
        . "env.properties"
    else
        echo "Can't find env.properties. Maybe forgot to create one in scripts dir?"
        exit 100
    fi

    if [[ -z ${VERSION} ]]; then
        echo "VERSION not set. Please run the script with VERSION variable set: VERSION=x.x.x sudo -E ./start.sh";
        exit 101;
    else
        VERSION="autocoin-arbitrage-monitor-${VERSION}";
    fi

    if [[ -z ${APP_PORT_ON_HOST} ]]; then
        echo "APP_PORT_ON_HOST not set. Please edit env.properties file in deployment directory.";
        exit 104;
    fi

    if [[ -z ${LOG_PATH} ]]; then
        echo "LOG_PATH not set. Please edit env.properties file in deployment directory.";
        exit 105;
    fi

    if [[ -z ${APP_DATA_PATH} ]]; then
        echo "APP_DATA_PATH not set. Please edit env.properties file in deployment directory.";
        exit 106;
    fi

    if [[ -z ${SERVICE_NAME} ]]; then
        echo "SERVICE_NAME not set. Please edit env.properties file in deployment directory.";
        exit 107;
    fi
}

preconditions

# Run new container
echo "Starting new version of container. Using version: ${VERSION}";
echo "Using port: ${APP_PORT_ON_HOST}";

docker run --name ${SERVICE_NAME} -d \
    -p 127.0.0.1:${APP_PORT_ON_HOST}:10021  \
    -e autocoin.environment=${ENV} \
    -e BASIC_PASS=${BASIC_PASS} \
    -e DOCKER_TAG=${VERSION} \
    -e JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1" \
    -v ${LOG_PATH}:/app/log \
    -v ${APP_DATA_PATH}:/app/data \
    --memory=200m \
    --restart=no \
    --network autocoin-services-admin \
    localhost:5000/autocoin-arbitrage-monitor:${VERSION}