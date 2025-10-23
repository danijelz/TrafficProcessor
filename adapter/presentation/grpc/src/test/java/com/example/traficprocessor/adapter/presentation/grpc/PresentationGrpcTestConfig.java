package com.example.traficprocessor.adapter.presentation.grpc;

import com.example.traficprocessor.adapter.presentation.grpc.api.GrpcTrafficProcessorServiceGrpc;
import com.example.traficprocessor.adapter.presentation.grpc.api.GrpcTrafficProcessorServiceGrpc.GrpcTrafficProcessorServiceBlockingStub;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;

@SpringBootApplication
public class PresentationGrpcTestConfig {
  @Bean
  GrpcTrafficProcessorServiceBlockingStub grpcTrafficProcessorServiceBlockingStub(
      GrpcChannelFactory channelFactory) {
    var channel = channelFactory.createChannel("localhost:18081");
    return GrpcTrafficProcessorServiceGrpc.newBlockingStub(channel);
  }
}
