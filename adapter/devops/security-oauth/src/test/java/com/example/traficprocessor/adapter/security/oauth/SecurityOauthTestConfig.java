package com.example.traficprocessor.adapter.security.oauth;

import com.example.traficprocessor.adapter.spring.commons.SpringCommonsConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({
  SpringCommonsConfig.class,
  SecurityOauthConfig.class,
  DefaultRestSecurityConfig.class,
  RestSecurityConfig.class
})
@SpringBootApplication(scanBasePackages = "com.example.traficprocessor")
class SecurityOauthTestConfig {}
