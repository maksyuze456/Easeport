
#Stage 1 Build

FROM maven:3.9-eclipse-temurin-21 as build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

#Stage 2 Test (CI pipeline)
FROM maven:3.9-eclipse-temurin-21 AS test
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

CMD ["mvn", "test"]