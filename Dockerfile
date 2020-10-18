# Dockerfile for creation of images on the x64 platform (windows 10 etc)
# The images are not compatible with raspberry pi and ARM architecture! For that use the Dockerfile-arm one
FROM maven:3.6.3-jdk-11 AS MAVEN_BUILD

COPY ./ ./
ARG TREECREATE_JDBC_URL=url
ENV TREECREATE_JDBC_URL=${TREECREATE_JDBC_URL}
RUN mvn clean package

FROM openjdk:11-jre

COPY --from=MAVEN_BUILD docker/treecreate-*.jar /treecreate.jar

CMD ["java", "-jar", "/treecreate.jar"]