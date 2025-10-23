package com.example.traficprocessor.adapter.persistence.dynamo;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@Target(TYPE)
@Retention(RUNTIME)
@Testcontainers
@EnableAutoConfiguration
@SpringBootTest(classes = {DynamoPersistenceConfig.class, PersistenceDynamoTestConfig.class})
public @interface PersistenceDynamoTest {}
