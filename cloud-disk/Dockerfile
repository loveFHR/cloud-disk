FROM openjdk:8-alpine
VOLUME ["/tmp","/log"]
COPY disk-server-1.0-SNAPSHOT.jar disk-server-1.0-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Xmx400m","./disk-server-1.0-SNAPSHOT.jar","&"]
