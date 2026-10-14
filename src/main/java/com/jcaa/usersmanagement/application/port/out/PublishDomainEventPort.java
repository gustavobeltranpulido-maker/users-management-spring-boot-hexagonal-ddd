package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.event.DomainEvent;

public interface PublishDomainEventPort {

  // Publicación asíncrona: un fallo del broker no debe interrumpir el caso de uso
  void publish(DomainEvent event);
}
