package com.example.traficprocessor.adapter.presentation.grpc.i18n;

import static com.example.traficprocessor.core.domain.utils.CharSequences.isNotBlank;
import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.vavr.control.Try;
import java.io.IOException;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;

public class LocaleResolverServerInterceptor implements ServerInterceptor {
  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> next) {
    Try.run(() -> initLocale(metadata));
    return next.startCall(serverCall, metadata);
  }

  private void initLocale(Metadata metadata) throws IOException {
    var localeCode = metadata.get(Key.of("accept-language", ASCII_STRING_MARSHALLER));
    if (isNotBlank(localeCode)) {
      LocaleContextHolder.setLocale(parseLocale(localeCode));
    }
  }

  private Locale parseLocale(String localeCode) throws IOException {
    var endOfFirstLocale = localeCode.indexOf(',');
    var firstLocale = endOfFirstLocale < 0 ? localeCode : localeCode.substring(0, endOfFirstLocale);
    return Locale.forLanguageTag(firstLocale);
  }
}
