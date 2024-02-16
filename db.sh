#!/usr/bin/env bash
RUNTIME=docker

if command -v podman &> /dev/null
then
    RUNTIME=podman
fi

$RUNTIME run -it --rm \
	 -e POSTGRES_PASSWORD=password \
	 -p 5432:5432 \
	 postgres -c shared_buffers=256MB -c max_connections=200
