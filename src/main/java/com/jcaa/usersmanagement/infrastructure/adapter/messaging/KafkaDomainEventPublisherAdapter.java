package com.jcaa.usersmanagement.infrastructure.adapter.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcaa.usersmanagement.application.port.out.PublishDomainEventPort;
import com.jcaa.usersmanagement.domain.event.DomainEvent;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class KafkaDomainEventPublisherAdapter implements PublishDomainEventPort {

  private static final String PROP_TOPIC = "${kafka.topic.user-events}";

  private static final String LOG_PUBLISHED =
      "[KafkaDomainEventPublisherAdapter] evento publicado. event={} topic={} partition={} offset={}";
  private static final String LOG_PUBLISH_FAILED =
      "[KafkaDomainEventPublisherAdapter] evento no publicado. event={} topic={} cause={}";

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final String topic;

  public KafkaDomainEventPublisherAdapter(
      final KafkaTemplate<String, String> kafkaTemplate,
      final ObjectMapper objectMapper,
      @Value(PROP_TOPIC) final String topic) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
    this.topic = topic;
  }

  @Override
  public void publish(final DomainEvent event) {
    try {
      final String key = DomainEventMessagingMapper.fromEventToKey(event);
      final String value =
          objectMapper.writeValueAsString(DomainEventMessagingMapper.fromEventToMessage(event));
      kafkaTemplate
          .send(topic, key, value)
          .whenComplete((result, exception) -> logResult(event, result, exception));
    } catch (final JsonProcessingException | RuntimeException exception) {
      // Ej.: broker inaccesible (timeout de metadatos). Se registra sin datos personales.
      logFailure(event, exception);
    }
  }

  private void logResult(
      final DomainEvent event,
      final SendResult<String, String> result,
      final Throwable exception) {
    if (Objects.nonNull(exception)) {
      logFailure(event, exception);
      return;
    }
    log.info(
        LOG_PUBLISHED,
        event.getEventName(),
        topic,
        result.getRecordMetadata().partition(),
        result.getRecordMetadata().offset());
  }

  private void logFailure(final DomainEvent event, final Throwable exception) {
    log.warn(LOG_PUBLISH_FAILED, event.getEventName(), topic, exception.getClass().getSimpleName());
  }
}
