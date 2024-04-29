package com.zerobase.repository;

import com.zerobase.domain.Account;
import com.zerobase.domain.AccountUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
  Optional<Account> findByAccountName(String accountName);

  Integer countByEmail(String email);
}
