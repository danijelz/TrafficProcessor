package com.example.traficprocessor.adapter.kafka;

import static io.vavr.control.Try.withResources;

import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;

class DeduplicationProcessor
    implements Processor<String, KafkaTrafficEvent, String, KafkaTrafficEvent> {
  private final String storeName;
  private final long searchWindowMs;

  private ProcessorContext<String, KafkaTrafficEvent> context;
  private WindowStore<String, Long> eventIdStore;

  DeduplicationProcessor(String storeName, long expirationInSeconds) {
    this.storeName = storeName;
    searchWindowMs = (expirationInSeconds * 1000 / 2) + 1;
  }

  @Override
  public void init(ProcessorContext<String, KafkaTrafficEvent> context) {
    this.context = context;
    eventIdStore = context.getStateStore(storeName);
  }

  @Override
  public void process(Record<String, KafkaTrafficEvent> record) {
    var eventId = record.key();
    var trafficEvent = record.value();

    if (eventId == null) {
      context.forward(record);
    }

    var timestamp = context.currentStreamTimeMs();
    var duplicate = isDuplicate(eventId, timestamp);
    eventIdStore.put(eventId, timestamp, timestamp);
    if (!duplicate) {
      context.forward(record.withValue(trafficEvent));
    }
  }

  private boolean isDuplicate(String eventId, long timestamp) {
    var timeFrom = timestamp - searchWindowMs;
    var timeTo = timestamp + searchWindowMs;
    return withResources(() -> eventIdStore.fetch(eventId, timeFrom, timeTo))
        .of(WindowStoreIterator::hasNext)
        .getOrElse(false);
  }
}
