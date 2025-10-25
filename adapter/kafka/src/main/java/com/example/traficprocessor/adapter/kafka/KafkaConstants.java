package com.example.traficprocessor.adapter.kafka;

import static org.springframework.kafka.retrytopic.RetryTopicConstants.DEFAULT_DLT_SUFFIX;
import static org.springframework.kafka.retrytopic.RetryTopicConstants.DEFAULT_RETRY_SUFFIX;

interface KafkaConstants {
  String TRAFFIC_EVENTS_TOPIC = "trafficEvents";
  String DEDUPLICATION_STORE_NAME = "trafficEventDeduplicationStore";
  String DEDUPLICATED_TRAFFIC_EVENTS_TOPIC = "deduplicatedTrafficEvents";
  String DEDUPLICATED_TRAFFIC_EVENTS_DLT_TOPIC =
      DEDUPLICATED_TRAFFIC_EVENTS_TOPIC + DEFAULT_DLT_SUFFIX;
  String DEDUPLICATED_TRAFFIC_EVENTS_RETRY_TOPIC =
      DEDUPLICATED_TRAFFIC_EVENTS_TOPIC + DEFAULT_RETRY_SUFFIX;
}
