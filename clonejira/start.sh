#!/bin/bash

# 1. Chạy lệnh build (tạo file JAR)
./mvnw clean install -DskipTests

# 2. Định nghĩa biến tên file JAR (thay đổi theo tên file của bạn)
JAR_FILE=$(find target -name "*.jar" | head -n 1)

# 3. Chạy ứng dụng Spring Boot
java -jar "$JAR_FILE"