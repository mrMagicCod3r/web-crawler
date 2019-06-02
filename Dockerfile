FROM maven:3.6.1-jdk-11-slim as builder
RUN mkdir /web-crawler
WORKDIR web-crawler

COPY pom.xml .
RUN mvn verify
COPY src src/
RUN mvn clean package

FROM openjdk:11-slim as runner
COPY --from=builder /web-crawler/target/web-crawler.jar .

ENV LOG_LEVEL=info

ENTRYPOINT ["java","-jar","web-crawler.jar"]
