package com.example.traficprocessor.app.benchmark;

import org.springframework.context.annotation.Bean;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.security.AuthenticationProcessInterceptor;
import org.springframework.grpc.server.security.GrpcSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;

public class BenchmarkIntegrationTestConfig {
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.httpBasic(HttpBasicConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .csrf(CsrfConfigurer::disable)
        .build();
  }

  @Bean
  @GlobalServerInterceptor
  AuthenticationProcessInterceptor grpcAuthenticationInterceptor(GrpcSecurity grpc)
      throws Exception {
    return grpc.authorizeRequests(requests -> requests.allRequests().permitAll()).build();
  }
}
