FROM openjdk:11

ENV APP_PATH=/usr/local/bin
ENV SNAPSHOT_VERSION=wifree-1.0-SNAPSHOT
COPY target/universal/$SNAPSHOT_VERSION.zip .
RUN unzip $SNAPSHOT_VERSION.zip -d $APP_PATH
RUN chmod +X $APP_PATH/$SNAPSHOT_VERSION/bin/wifree