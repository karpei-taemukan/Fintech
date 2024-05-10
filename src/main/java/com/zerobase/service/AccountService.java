package com.zerobase.service;

import com.zerobase.domain.AccountUser;
import com.zerobase.domain.SearchForm;
import com.zerobase.exception.AccountException;
import com.zerobase.repository.AccountRepository;
import com.zerobase.repository.AccountUserRepository;
import com.zerobase.type.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountUserRepository accountUserRepository;
  private final AccountRepository accountRepository;

  public void searchAllAccount(String email, SearchForm form) {
    /*
     * 사용자 계정에 등록된 모든 계좌를 보여준다
     * */

    // 토큰 안 이메일 정보와 요청 form 의 이메일이 일치하는 지 확인
    if (!email.equals(form.getEmail())) {
      throw new AccountException(ErrorCode.ACCOUNT_NOT_FOUND);
    }

    AccountUser accountUser = accountUserRepository.findByEmail(email)
        .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

  }
}
