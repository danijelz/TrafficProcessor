package com.example.traficprocessor.adapter.presentation.grpc.observability;

import static org.slf4j.LoggerFactory.getLogger;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.vavr.control.Try;
import org.slf4j.Logger;

public class GrpcLoggingInterceptor implements ServerInterceptor {
  private static final Logger LOGGER = getLogger(GrpcLoggingInterceptor.class);

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> next) {
    Try.run(() -> logMessage(serverCall));
    return next.startCall(serverCall, metadata);
  }

  private <ReqT, RespT> void logMessage(ServerCall<ReqT, RespT> call) {
    var methodDescriptor = call.getMethodDescriptor();
    LOGGER.info(
        "GrpcClientRequest: {} {}",
        methodDescriptor.getBareMethodName(),
        methodDescriptor.getServiceName());
  }
}
