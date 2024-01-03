FROM openjdk:17-slim-buster

ADD build/libs/user-0.0.1-SNAPSHOT.jar boot.jar

EXPOSE 9001

CMD ["sh","-c","java -jar boot.jar"]