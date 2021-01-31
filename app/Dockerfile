FROM openjdk:8
EXPOSE 8000:8080
COPY ./build/libs/app-1.0.0.jar /app/myapp.jar
ENTRYPOINT [ "java", "-jar" , "/app/myapp.jar" ]