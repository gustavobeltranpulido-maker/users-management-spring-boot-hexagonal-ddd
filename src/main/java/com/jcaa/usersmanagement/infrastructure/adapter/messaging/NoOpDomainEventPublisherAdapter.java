package com.jcaa.usersmanagement.infrastructure.adapter.messaging;

import com.jcaa.usersmanagement.application.port.out.PublishDomainEventPort;
import com.jcaa.usersmanagement.domain.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

// Activo cuando Kafka está deshabilitado (desarrollo local sin broker)
@Slf4j
@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpDomainEventPublisherAdapter implements PublishDomainEventPort {

  private static final String LOG_SKIPPED =
      "[NoOpDomainEventPublisherAdapter] Kafka deshabilitado; evento omitido. event={}";

  @Override
  public void publish(final DomainEvent event) {
    log.debug(LOG_SKIPPED, event.getEventName());
  }
}
