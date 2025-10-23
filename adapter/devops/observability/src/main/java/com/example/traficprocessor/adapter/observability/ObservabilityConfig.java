package com.example.traficprocessor.adapter.observability;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration(proxyBeanMethods = false)
@PropertySource(value = "classpath:observability.properties")
class ObservabilityConfig {}
