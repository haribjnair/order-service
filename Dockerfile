FROM openjdk:17 as build

ADD ./target/order-service.jar order-service.jar
CMD ["java", "-jar","/order-service.jar"]


EXPOSE 8091