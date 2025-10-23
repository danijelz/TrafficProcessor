package com.example.traficprocessor.adapter.presentation.grpc;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.example.traficprocessor.adapter.spring.commons.SpringCommonsConfig;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.grpc.test.AutoConfigureInProcessTransport;

@Target(TYPE)
@Retention(RUNTIME)
@AutoConfigureInProcessTransport
@SpringBootTest(
    classes = {
      SpringCommonsConfig.class,
      GrpcPresentationConfig.class,
      PresentationGrpcTestConfig.class
    })
public @interface PresentationGrpcTest {}
