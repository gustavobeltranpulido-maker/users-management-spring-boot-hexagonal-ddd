package com.jcaa.usersmanagement.domain.model;

import com.jcaa.usersmanagement.domain.enums.AccountStatus;
import com.jcaa.usersmanagement.domain.enums.AccountType;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import java.util.UUID;
import lombok.Value;

@Value
public class AccountModel {

  String id;
  UserId userId;
  UserName ownerName;
  UserEmail ownerEmail;
  AccountType type;
  AccountStatus status;

  // Cuenta de ahorros activa que se abre automáticamente al registrar un usuario
  public static AccountModel openSavingsAccountFor(final UserModel user) {
    return new AccountModel(
        UUID.randomUUID().toString(),
        user.getId(),
        user.getName(),
        user.getEmail(),
        AccountType.SAVINGS,
        AccountStatus.ACTIVE);
  }
}
