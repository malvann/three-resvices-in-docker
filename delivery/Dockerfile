FROM openjdk:19 as delivery
WORKDIR /app
COPY build/libs/* ./
CMD ["java","-jar","delivery.jar"]
