package com.example.traficprocessor.core.model;

import java.util.List;

public record I18nMessage(String code, String content, List<String> parameters) {}
