# Stage 1: Build the application
FROM gradle:8.7-jdk17 AS build
COPY --chown=gradle:gradle . /nmn/domusvocationis
WORKDIR /nmn/domusvocationis

#skip task: test
RUN gradle clean build -x test --no-daemon

# Stage 2: Run the application
FROM openjdk:17-slim
EXPOSE 8080
COPY --from=build /nmn/domusvocationis/build/libs/*.jar /nmn/spring-boot-domus-vocationis.jar
ENTRYPOINT ["java", "-jar", "/nmn/spring-boot-domus-vocationis.jar"]
