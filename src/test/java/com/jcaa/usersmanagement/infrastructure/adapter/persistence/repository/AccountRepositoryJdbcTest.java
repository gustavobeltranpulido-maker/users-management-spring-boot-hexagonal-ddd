package com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jcaa.usersmanagement.domain.enums.AccountStatus;
import com.jcaa.usersmanagement.domain.enums.AccountType;
import com.jcaa.usersmanagement.domain.model.AccountModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.PersistenceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("AccountRepositoryJdbc")
@ExtendWith(MockitoExtension.class)
class AccountRepositoryJdbcTest {

  private static final String ACCOUNT_ID = "acc-001";

  @Mock private DataSource dataSource;
  @Mock private Connection connection;
  @Mock private PreparedStatement statement;

  private AccountRepositoryJdbc repository;
  private AccountModel account;

  @BeforeEach
  void setUp() {
    repository = new AccountRepositoryJdbc(dataSource);
    account =
        new AccountModel(
            ACCOUNT_ID,
            new UserId("2300"),
            new UserName("Gos"),
            new UserEmail("gos@example.com"),
            AccountType.SAVINGS,
            AccountStatus.ACTIVE);
  }

  @Test
  @DisplayName("save() inserta la cuenta con los datos del usuario")
  void shouldInsertAccount() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(statement);

    // Act
    repository.save(account);

    // Assert
    verify(statement).setString(1, ACCOUNT_ID);
    verify(statement).setString(2, "2300");
    verify(statement).setString(3, "Gos");
    verify(statement).setString(4, "gos@example.com");
    verify(statement).setString(5, "SAVINGS");
    verify(statement).setString(6, "ACTIVE");
    verify(statement).executeUpdate();
  }

  @Test
  @DisplayName("save() lanza PersistenceException cuando el INSERT falla")
  void shouldThrowPersistenceExceptionWhenInsertFails() throws SQLException {
    // Arrange
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeUpdate()).thenThrow(new SQLException("Insert failed"));

    // Act + Assert
    assertThrows(PersistenceException.class, () -> repository.save(account));
  }
}
