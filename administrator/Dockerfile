FROM maven:3.8.4-jdk-11
WORKDIR /administrator
COPY . .
RUN mvn clean install -DskipTests
CMD mvn spring-boot:run