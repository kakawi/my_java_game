FROM java:8
ADD . /usr/src/myapp
WORKDIR /usr/src/myapp
CMD java -jar server.jar
EXPOSE 8080
CMD ["java", "-jar", "server.jar"]
