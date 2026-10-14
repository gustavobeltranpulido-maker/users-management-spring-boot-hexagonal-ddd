package com.jcaa.usersmanagement.infrastructure.adapter.messaging;

import com.jcaa.usersmanagement.domain.event.DomainEvent;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DomainEventMessagingMapper {

  private static final String PAYLOAD_ID_KEY = "id";

  public DomainEventMessage fromEventToMessage(final DomainEvent event) {
    return new DomainEventMessage(
        event.getEventName(), event.getOccurredOn().toString(), event.payload());
  }

  // Clave del mensaje: el id del usuario, para que sus eventos lleguen ordenados a la misma partición
  public String fromEventToKey(final DomainEvent event) {
    return event.payload().get(PAYLOAD_ID_KEY);
  }
}
