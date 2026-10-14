package com.jcaa.usersmanagement.infrastructure.adapter.messaging;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.event.UserCreatedDomainEvent;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@DisplayName("KafkaDomainEventPublisherAdapter")
@ExtendWith(MockitoExtension.class)
class KafkaDomainEventPublisherAdapterTest {

  private static final String TOPIC = "users.events.test";
  private static final String USER_ID = "u-01";

  @Mock private KafkaTemplate<String, String> kafkaTemplate;

  private final ObjectMapper objectMapper = new ObjectMapper();
  private KafkaDomainEventPublisherAdapter adapter;
  private UserCreatedDomainEvent event;

  @BeforeEach
  void setUp() {
    adapter = new KafkaDomainEventPublisherAdapter(kafkaTemplate, objectMapper, TOPIC);
    final UserModel user =
        new UserModel(
            new UserId(USER_ID),
            new UserName("John Arrieta"),
            new UserEmail("john@example.com"),
            UserPassword.fromHash("$2a$12$hash"),
            UserRole.ADMIN,
            UserStatus.PENDING);
    event = new UserCreatedDomainEvent(user);
  }

  @Test
  @DisplayName("publish() envía el evento en JSON al tópico usando el id del usuario como clave")
  void shouldSendEventAsJsonWithUserIdKey() throws Exception {
    // Arrange
    when(kafkaTemplate.send(eq(TOPIC), eq(USER_ID), anyString()))
        .thenReturn(new CompletableFuture<>());
    final ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

    // Act
    adapter.publish(event);

    // Assert
    verify(kafkaTemplate).send(eq(TOPIC), eq(USER_ID), valueCaptor.capture());
    final JsonNode message = objectMapper.readTree(valueCaptor.getValue());
    assertAll(
        "mensaje publicado",
        () -> assertEquals("user.created", message.get("eventName").asText()),
        () -> assertEquals(USER_ID, message.get("payload").get("id").asText()),
        () -> assertEquals("john@example.com", message.get("payload").get("email").asText()),
        () -> assertEquals("PENDING", message.get("payload").get("status").asText()));
  }

  @Test
  @DisplayName("publish() no propaga el error cuando el envío asíncrono falla")
  void shouldNotThrowWhenAsyncSendFails() {
    // Arrange
    when(kafkaTemplate.send(eq(TOPIC), eq(USER_ID), anyString()))
        .thenReturn(CompletableFuture.failedFuture(new KafkaException("broker caído")));

    // Act + Assert
    assertDoesNotThrow(() -> adapter.publish(event));
  }

  @Test
  @DisplayName("publish() no propaga el error cuando el broker no responde al enviar")
  void shouldNotThrowWhenSendThrowsSynchronously() {
    // Arrange
    when(kafkaTemplate.send(eq(TOPIC), eq(USER_ID), anyString()))
        .thenThrow(new KafkaException("timeout de metadatos"));

    // Act + Assert
    assertDoesNotThrow(() -> adapter.publish(event));
  }
}
