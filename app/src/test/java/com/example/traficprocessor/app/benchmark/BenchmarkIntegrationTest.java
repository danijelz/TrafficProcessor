package com.example.traficprocessor.app.benchmark;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.example.traficprocessor.app.TrafficProcessorApplication;
import com.example.traficprocessor.app.IntegrationTestConfig;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@Target(TYPE)
@Retention(RUNTIME)
@Rollback
@WithMockUser
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = NONE)
@TestPropertySource("/benchmark_test.properties")
@ActiveProfiles({"benchmark_test", "integration-test"})
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = {
      TrafficProcessorApplication.class,
      IntegrationTestConfig.class,
      BenchmarkIntegrationTestConfig.class
    })
public @interface BenchmarkIntegrationTest {}
