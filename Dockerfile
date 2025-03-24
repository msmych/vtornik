FROM eclipse-temurin:23-jre

WORKDIR /app

COPY web/build/libs/web-all.jar /app/web-all.jar

EXPOSE 8080

CMD ["java", "-jar", "web-all.jar"]
