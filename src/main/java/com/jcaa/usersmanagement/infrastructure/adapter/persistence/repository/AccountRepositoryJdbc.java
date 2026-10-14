package com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository;

import com.jcaa.usersmanagement.application.port.out.SaveAccountPort;
import com.jcaa.usersmanagement.domain.model.AccountModel;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.PersistenceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryJdbc implements SaveAccountPort {

  // Si el usuario ya tiene una cuenta de ese tipo (UNIQUE user_id, type) no se duplica
  private static final String SQL_INSERT =
      "INSERT INTO accounts (id, user_id, owner_name, owner_email, type, status, created_at, updated_at) "
          + "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW()) "
          + "ON CONFLICT (user_id, type) DO NOTHING";

  private final DataSource dataSource;

  @Override
  public void save(final AccountModel account) {
    try (final Connection connection = dataSource.getConnection();
        final PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
      statement.setString(1, account.getId());
      statement.setString(2, account.getUserId().value());
      statement.setString(3, account.getOwnerName().value());
      statement.setString(4, account.getOwnerEmail().value());
      statement.setString(5, account.getType().name());
      statement.setString(6, account.getStatus().name());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw PersistenceException.becauseAccountSaveFailed(account.getUserId().value(), exception);
    }
  }
}
