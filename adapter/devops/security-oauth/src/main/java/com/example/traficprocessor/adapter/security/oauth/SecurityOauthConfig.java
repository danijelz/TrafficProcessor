package com.example.traficprocessor.adapter.security.oauth;

import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.API_PATH;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.INDEX_PATH;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.OPENAPI_PATH;
import static com.example.traficprocessor.adapter.presentation.rest.RestPresentationConstants.SWAGGER_PATH;
import static com.example.traficprocessor.adapter.security.oauth.i18n.SecurityOauthI18nMessageConstants.UNAUTHORIZED_MESSAGE;
import static com.example.traficprocessor.core.domain.exception.ServiceExceptionStatus.UNAUTHORIZED;
import static org.springframework.security.config.Customizer.withDefaults;

import com.example.traficprocessor.adapter.presentation.grpc.GrpcPresentationConfig;
import com.example.traficprocessor.adapter.presentation.rest.RestPresentationConfig;
import com.example.traficprocessor.adapter.spring.commons.exception.ServiceExceptionAdvisor;
import com.example.traficprocessor.adapter.spring.commons.i18n.I18nMessagesBasenameProvider;
import com.example.traficprocessor.core.domain.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.security.AuthenticationProcessInterceptor;
import org.springframework.grpc.server.security.GrpcSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:security_oauth.properties")
public class SecurityOauthConfig {
  @Bean
  I18nMessagesBasenameProvider securityOauthI18nMessagesBasenameProvider() {
    return () -> "classpath:i18n/messages-oauth";
  }

  @ServiceExceptionAdvisor
  ServiceException translateAuthenticationException(AuthenticationException exception) {
    return new ServiceException(exception, UNAUTHORIZED, UNAUTHORIZED_MESSAGE.toMessage());
  }
}

@Configuration
@ConditionalOnMissingClass(
    "com.example.traficprocessor.adapter.presentation.rest.RestPresentationConfig")
class DefaultRestSecurityConfig {
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.httpBasic(HttpBasicConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .oauth2ResourceServer(c -> c.jwt(withDefaults()))
        .cors(withDefaults())
        .csrf(CsrfConfigurer::disable)
        .build();
  }
}

@Configuration
@ConditionalOnClass(RestPresentationConfig.class)
class RestSecurityConfig {
  @Bean
  SecurityFilterChain filterChain(
      HttpSecurity http,
      @Value("${management.endpoints.web.base-path:/actuator}") String actuatorPath)
      throws Exception {
    return http.httpBasic(HttpBasicConfigurer::disable)
        .authorizeHttpRequests(
            auth -> {
              auth.requestMatchers(INDEX_PATH).permitAll();
              auth.requestMatchers(API_PATH).permitAll();
              auth.requestMatchers(OPENAPI_PATH + "/**").permitAll();
              auth.requestMatchers(SWAGGER_PATH + "/**").permitAll();
              auth.requestMatchers(actuatorPath + "/**").permitAll();
              auth.anyRequest().authenticated();
            })
        .oauth2ResourceServer(c -> c.jwt(withDefaults()))
        .cors(withDefaults())
        .csrf(CsrfConfigurer::disable)
        .build();
  }
}

@Configuration
@ConditionalOnClass(GrpcPresentationConfig.class)
class GrpcAuthenticationInterceptorConfig {
  @Bean
  @GlobalServerInterceptor
  AuthenticationProcessInterceptor grpcAuthenticationInterceptor(GrpcSecurity grpc)
      throws Exception {
    return grpc.authorizeRequests(
            requests -> {
              requests.methods("grpc.*/*").permitAll();
              requests.allRequests().authenticated();
            })
        .oauth2ResourceServer(c -> c.jwt(withDefaults()))
        .build();
  }
}
