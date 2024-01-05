FROM openjdk:17-slim-buster

ADD build/libs/user-0.0.1-SNAPSHOT.jar boot.jar

CMD ["java","-jar","boot.jar"]