package com.zerobase.service;

import com.zerobase.domain.AccountUser;
import com.zerobase.exception.AccountException;
import com.zerobase.repository.AccountUserRepository;
import com.zerobase.token.util.Aes256Util;
import com.zerobase.type.ErrorCode;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerifyService {

  private final AccountUserRepository accountUserRepository;

  @Transactional
  public LocalDateTime ChangeUserVerification(Long id, String code) {
    Optional<AccountUser> userOptional = accountUserRepository.findById(id);

    if (userOptional.isPresent()) {
      AccountUser accountUser = userOptional.get();

      accountUser.setVerificationCode(code);
      accountUser.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));

      return accountUser.getVerifyExpiredAt();
    } else {
      throw new AccountException(ErrorCode.USER_NOT_FOUND);
    }

  }

  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    String name = Aes256Util.decrypt(username);

    return accountUserRepository.findByName(name)
        .orElseThrow(() -> new UsernameNotFoundException("Couldn't find user -> " + username));
  }
}
