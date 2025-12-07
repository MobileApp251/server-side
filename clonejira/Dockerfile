# -----------------------------------------------------------------------------
# GIAI ĐOẠN 1: BUILD (Sử dụng Maven kèm JDK 21 của Eclipse Temurin)
# Đây là image chính thức "maven" mà bạn thấy, nhưng với tag đúng.
# -----------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy file cấu hình và tải dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy mã nguồn và build
COPY src ./src
RUN mvn clean install -DskipTests

# -----------------------------------------------------------------------------
# GIAI ĐOẠN 2: RUNTIME (Chạy ứng dụng)
# Sử dụng JRE 21 của Eclipse Temurin (nhẹ, ổn định thay vì openjdk cũ)
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy file JAR từ giai đoạn build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]