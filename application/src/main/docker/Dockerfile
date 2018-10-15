FROM openjdk:8-jdk
# time zone
RUN mkdir /app
RUN mkdir /app/logs
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/shanghai" > /etc/timezone
ADD qbao-api.jar qbao-api.jar
ENTRYPOINT exec java $JAVA_OPTS -jar /qbao-api.jar --spring.config.location=/home/config/