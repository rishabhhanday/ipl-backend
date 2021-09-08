FROM amd64/openjdk
COPY ./target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]