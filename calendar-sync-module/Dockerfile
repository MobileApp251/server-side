# Sử dụng image chính thức của PHP kèm Apache
# Phiên bản 8.2 là ổn định và phổ biến hiện nay
FROM php:8.2-apache

# 1. Cài đặt các gói hệ thống cần thiết và PHP Extensions
# Raw PHP thường cần kết nối MySQL (mysqli/pdo) và xử lý zip/unzip
RUN apt-get update && apt-get install -y \
    libzip-dev \
    zip \
    unzip \
    && docker-php-ext-install pdo pdo_mysql mysqli zip

# 2. Bật mod_rewrite của Apache
# Cực kỳ quan trọng để bạn có thể rewrite URL (VD: /api/product -> index.php)
RUN a2enmod rewrite

# 3. Cấu hình Document Root (Tùy chọn)
# Mặc định Apache trỏ vào /var/www/html. 
# Nếu code của bạn nằm trong thư mục con như 'public', hãy bỏ comment dòng dưới:
# ENV APACHE_DOCUMENT_ROOT /var/www/html/public
# RUN sed -ri -e 's!/var/www/html!${APACHE_DOCUMENT_ROOT}!g' /etc/apache2/sites-available/*.conf
# RUN sed -ri -e 's!/var/www/html!${APACHE_DOCUMENT_ROOT}!g' /etc/apache2/apache2.conf

# 4. Copy custom php.ini (Khuyên dùng)
# Để tối ưu performance và tăng giới hạn upload
COPY php.ini /usr/local/etc/php/conf.d/custom-php.ini

# 5. Copy source code vào container
COPY . /var/www/html/

# 6. Phân quyền cho Apache user (www-data)
# Để PHP có thể ghi file (upload ảnh, ghi log) nếu cần
RUN chown -R www-data:www-data /var/www/html \
    && chmod -R 755 /var/www/html

# 7. Expose port 80 (Port mặc định của Apache)
EXPOSE 8080

# Command mặc định của image này đã là "apache2-foreground", không cần khai báo lại