FROM maven:3.3.9 as maven
COPY $PWD /build/
WORKDIR /build
RUN ["mvn", "clean", "package"]

FROM openjdk:8-jre
COPY --from=maven /build/RestDocSpringExamples/target/ROOT.jar /data/
WORKDIR /data
CMD ["java","-jar","ROOT.jar"]

