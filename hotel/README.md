# 호텔 프로젝트

## MySQL DB 쿼리
```
CREATE DATABASE hotel DEFAULT CHARACTER SET utf8mb4;
CREATE USER 'hotel_user'@'%' IDENTIFIED BY 'hotel9876!';
GRANT ALL PRIVILEGES ON hotel.* TO 'hotel_user'@'%';
FLUSH PRIVILEGES;
```
