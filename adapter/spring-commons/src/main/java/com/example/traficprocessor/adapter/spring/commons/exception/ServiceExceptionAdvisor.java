package com.example.traficprocessor.adapter.spring.commons.exception;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.aot.hint.annotation.Reflective;

@Documented
@Reflective
@Target(METHOD)
@Retention(RUNTIME)
public @interface ServiceExceptionAdvisor {}
