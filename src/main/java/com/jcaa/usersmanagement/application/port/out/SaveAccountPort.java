package com.jcaa.usersmanagement.application.port.out;

import com.jcaa.usersmanagement.domain.model.AccountModel;

public interface SaveAccountPort {
  void save(AccountModel account);
}
