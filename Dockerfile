FROM java:8-jdk-alpine

ENV MONGODB_HOST=mongo

WORKDIR /app

COPY target/FileService-0.0.1-SNAPSHOT.jar .
COPY rootfs/ .

ENTRYPOINT ["/bin/sh"]
CMD ["/app/app-entrypoint.sh"]