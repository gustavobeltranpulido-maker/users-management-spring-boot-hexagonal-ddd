package com.jcaa.usersmanagement.infrastructure.adapter.messaging;

import java.util.Map;

public record DomainEventMessage(
    String eventName, String occurredOn, Map<String, String> payload) {}
