FROM openjdk:19 as kitchen
WORKDIR /app
COPY build/libs/* ./
CMD ["java","-jar","kitchen.jar"]
