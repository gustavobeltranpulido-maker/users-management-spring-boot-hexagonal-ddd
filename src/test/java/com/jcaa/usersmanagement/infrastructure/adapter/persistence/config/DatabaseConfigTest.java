package com.jcaa.usersmanagement.infrastructure.adapter.persistence.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DatabaseConfig")
class DatabaseConfigTest {

  @Test
  @DisplayName("buildJdbcUrl() arma la URL de PostgreSQL con el modo SSL indicado")
  void shouldBuildPostgresUrlWithSslMode() {
    // Arrange
    final DatabaseConfig config =
        new DatabaseConfig(
            "aws-0-us-east-1.pooler.supabase.com", 5432, "postgres", "user", "secret", "require");

    // Act
    final String url = config.buildJdbcUrl();

    // Assert
    assertEquals(
        "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require", url);
  }
}
