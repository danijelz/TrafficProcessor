package com.example.traficprocessor.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.traficprocessor")
public class TrafficProcessorApplication {
  public static void main(String[] args) {
    SpringApplication.run(TrafficProcessorApplication.class, args);
  }
}
