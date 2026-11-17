#!/bin/sh
set -e
java -jar /app/app.jar --server.port=8081 &
exec nginx -g 'daemon off;'