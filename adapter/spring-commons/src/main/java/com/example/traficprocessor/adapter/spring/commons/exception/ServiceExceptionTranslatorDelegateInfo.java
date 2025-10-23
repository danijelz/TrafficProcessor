package com.example.traficprocessor.adapter.spring.commons.exception;

record ServiceExceptionTranslatorDelegateInfo(
    ServiceExceptionTranslatorDelegate<?> delegate, Class<? extends Throwable> exceptionType) {}
