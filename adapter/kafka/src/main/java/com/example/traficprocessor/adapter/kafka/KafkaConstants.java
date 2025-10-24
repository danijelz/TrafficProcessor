package com.example.traficprocessor.adapter.kafka;

interface KafkaConstants {
  String TRAFFIC_EVENTS_TOPIC = "trafficEvents";
  String DEDUPLICATION_STORE_NAME = "trafficEventDeduplicationStore";
  String DEDUPLICATED_TRAFFIC_EVENTS_TOPIC = "deduplicatedTrafficEvents";
}
