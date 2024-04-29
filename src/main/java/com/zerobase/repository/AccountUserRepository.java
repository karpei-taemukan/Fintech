package com.zerobase.repository;

import com.zerobase.domain.AccountUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {

  Optional<AccountUser> findByEmail(String email);

  Optional<AccountUser> findByName(String name);

}
