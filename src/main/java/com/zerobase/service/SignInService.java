package com.zerobase.service;

import com.zerobase.domain.AccountUser;
import com.zerobase.domain.SignInForm;
import com.zerobase.exception.AccountException;
import com.zerobase.repository.AccountUserRepository;
import com.zerobase.token.config.JwtAuthProvider;
import com.zerobase.type.ErrorCode;
import com.zerobase.token.domain.UserType;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInService {

  private final AccountUserRepository accountUserRepository;
  private final JwtAuthProvider provider;


  public Optional<AccountUser> findValidUser(String email, String pw) {
    return accountUserRepository.findByEmail(email).stream().filter(
        accountUser -> accountUser.getAccountPassword().equals(pw) && accountUser.isVerify()
    ).findFirst();
  }


  public String accountSignIn(SignInForm form) {

    AccountUser accountUser = findValidUser(form.getEmail(), form.getPassword())
        .orElseThrow(() -> new AccountException(ErrorCode.LOGIN_CHECK_FAIL));

    return provider.createToken(accountUser.getName(), accountUser.getId(), UserType.USER,
        form.getEmail());
  }
}