package com.example.traficprocessor.app.benchmark;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Test
@Tag("BenchmarkTest")
@Target(METHOD)
@Retention(RUNTIME)
public @interface BenchmarkTest {}
