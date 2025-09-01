# -------- Build stage --------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy only the pom first to leverage Docker layer caching on deps
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -DskipTests dependency:go-offline

# Now copy sources and build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -DskipTests package

# -------- Runtime stage --------
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /workspace/target/tickets-*.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]

