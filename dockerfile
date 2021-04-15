#base image
FROM openjdk:8
COPY /src /src/java
WORKDIR /src/java
RUN ["javac", "Application.java"]
ENTRYPOINT ["java", "Application"]
