package com.example.lab8shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point ของแอปพลิเคชัน
 * ต้องอยู่ที่ root package (com.example.lab8shop) เพื่อให้ Spring Boot
 * component-scan ลงไปเจอ Controller/Service/Repository/Strategy ในแพ็กเกจย่อยทั้งหมดอัตโนมัติ
 */
@SpringBootApplication
public class Lab8ProductshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lab8ProductshopApplication.class, args);
    }
}
