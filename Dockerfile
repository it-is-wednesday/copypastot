FROM eclipse-temurin:11-jdk-jammy
RUN mkdir /app
WORKDIR /app
COPY target/copypastot.jar /app/copypastot.jar
COPY resources /app/resources
CMD ["java", "-jar", "copypastot.jar", "-m", "server", "0.0.0.0", "1337"]
EXPOSE 1337
