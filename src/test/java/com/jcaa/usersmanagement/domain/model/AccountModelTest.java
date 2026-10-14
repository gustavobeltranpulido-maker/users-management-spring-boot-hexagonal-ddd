package com.jcaa.usersmanagement.domain.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.jcaa.usersmanagement.domain.enums.AccountStatus;
import com.jcaa.usersmanagement.domain.enums.AccountType;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AccountModel")
class AccountModelTest {

  private final UserModel user =
      new UserModel(
          new UserId("2300"),
          new UserName("Gos"),
          new UserEmail("gos@example.com"),
          UserPassword.fromHash("$2a$12$hash"),
          UserRole.ADMIN,
          UserStatus.PENDING);

  @Test
  @DisplayName("openSavingsAccountFor() abre una cuenta de ahorros activa con los datos del usuario")
  void shouldOpenActiveSavingsAccountWithUserData() {
    // Act
    final AccountModel account = AccountModel.openSavingsAccountFor(user);

    // Assert
    assertAll(
        "cuenta abierta",
        () -> assertDoesNotThrow(() -> UUID.fromString(account.getId()), "id es un UUID"),
        () -> assertEquals(user.getId(), account.getUserId(), "userId"),
        () -> assertEquals(user.getName(), account.getOwnerName(), "ownerName"),
        () -> assertEquals(user.getEmail(), account.getOwnerEmail(), "ownerEmail"),
        () -> assertEquals(AccountType.SAVINGS, account.getType(), "type"),
        () -> assertEquals(AccountStatus.ACTIVE, account.getStatus(), "status"));
  }

  @Test
  @DisplayName("openSavingsAccountFor() genera un id distinto en cada cuenta")
  void shouldGenerateDifferentIds() {
    // Act
    final AccountModel first = AccountModel.openSavingsAccountFor(user);
    final AccountModel second = AccountModel.openSavingsAccountFor(user);

    // Assert
    assertNotEquals(first.getId(), second.getId());
  }
}
