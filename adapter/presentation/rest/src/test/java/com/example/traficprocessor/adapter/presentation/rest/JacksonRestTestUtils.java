package com.example.traficprocessor.adapter.presentation.rest;

import com.example.traficprocessor.core.domain.utils.Values;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import java.util.List;
import org.springframework.test.web.servlet.MvcResult;

public interface JacksonRestTestUtils {
  ObjectMapper mapper();

  default String toJson(final Object obj) {
    return Try.success(mapper()).mapTry(m -> m.writeValueAsString(obj)).get();
  }

  default <T> String toJson(final Object obj, TypeReference<T> typeReference) {
    return Try.success(mapper())
        .mapTry(m -> m.writerFor(typeReference).writeValueAsString(obj))
        .get();
  }

  default <T> T fromJson(String json, Class<? extends T> type) {
    return Try.success(mapper()).mapTry(m -> m.readValue(json, type)).get();
  }

  default <T> T fromJson(MvcResult result, Class<? extends T> type) {
    return Try.success(mapper())
        .mapTry(m -> m.readValue(result.getResponse().getContentAsString(), type))
        .get();
  }

  default <T> T fromJson(MvcResult result, JavaType type) {
    return Try.success(mapper())
        .mapTry(m -> m.readValue(result.getResponse().getContentAsString(), type))
        .map(Values::<T>cast)
        .get();
  }

  default <T> List<T> fromJsonArray(MvcResult result, Class<? extends T> type) {
    var collectionType = mapper().getTypeFactory().constructCollectionType(List.class, type);
    return Try.success(mapper())
        .mapTry(
            m -> m.<List<T>>readValue(result.getResponse().getContentAsString(), collectionType))
        .get();
  }
}
