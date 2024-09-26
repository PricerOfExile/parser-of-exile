FROM eclipse-temurin:21

RUN mkdir /opt/app

COPY target/poeai-1.0.0-SNAPSHOT.jar /opt/app/poeai.jar

CMD ["java", "-jar", "/opt/app/poeai.jar"]
