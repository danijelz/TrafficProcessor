package com.example.traficprocessor.adapter.presentation.grpc;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import com.example.traficprocessor.adapter.presentation.grpc.api.GrpcTrafficProcessorServiceGrpc;
import com.example.traficprocessor.adapter.presentation.grpc.api.GrpcTrafficProcessorServiceGrpc.GrpcTrafficProcessorServiceBlockingStub;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.stub.MetadataUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.grpc.client.GlobalClientInterceptor;
import org.springframework.grpc.client.GrpcChannelFactory;

@SpringBootApplication
public class PresentationGrpcTestConfig {
  @Bean
  GrpcTrafficProcessorServiceBlockingStub grpcTrafficProcessorServiceBlockingStub(
      GrpcChannelFactory channelFactory) {
    var channel = channelFactory.createChannel("localhost:18081");
    return GrpcTrafficProcessorServiceGrpc.newBlockingStub(channel);
  }

  @Bean
  @Order(HIGHEST_PRECEDENCE)
  @GlobalClientInterceptor
  ClientInterceptor localeInterceptor() {
    var extraHeaders = new Metadata();
    extraHeaders.put(Key.of("accept-language", ASCII_STRING_MARSHALLER), "sl");
    return MetadataUtils.newAttachHeadersInterceptor(extraHeaders);
  }
}
