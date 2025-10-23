package com.example.traficprocessor.app.aot;

import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_METHODS;
import static org.springframework.aot.hint.MemberCategory.PUBLIC_FIELDS;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

public class CaffeineRuntimeHintsRegistrar implements RuntimeHintsRegistrar {
  private static final MemberCategory[] MEMBER_CATEGORIES = {
    PUBLIC_FIELDS, INVOKE_DECLARED_CONSTRUCTORS, INVOKE_PUBLIC_METHODS
  };

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    var reference = TypeReference.of("com.github.benmanes.caffeine.cache.SSSMSA");
    hints.reflection().registerType(reference, MEMBER_CATEGORIES);
  }
}
