package com.example.traficprocessor.adapter.persistence.jpa;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@Target(TYPE)
@Retention(RUNTIME)
@Rollback
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = NONE)
@TestPropertySource("/persistence_jpa_test.properties")
@ContextConfiguration(classes = {JpaPersistenceConfig.class, PersistenceJpaTestConfig.class})
public @interface PersistenceJpaTest {}
