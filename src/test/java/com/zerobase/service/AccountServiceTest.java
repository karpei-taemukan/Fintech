package com.zerobase.service;

import static org.junit.jupiter.api.Assertions.*;

import com.zerobase.config.filter.JwtFilter;
import com.zerobase.domain.Account;
import com.zerobase.domain.AccountUser;
import com.zerobase.dto.AccountDto;
import com.zerobase.exception.AccountException;
import com.zerobase.repository.AccountRepository;
import com.zerobase.repository.AccountUserRepository;
import com.zerobase.token.config.JwtAuthProvider;
import com.zerobase.type.AccountStatus;
import com.zerobase.type.ErrorCode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock
  private JwtFilter jwtFilter;

  @Mock
  private JwtAuthProvider provider;

  @Mock
  private AccountUserRepository accountUserRepository;

  @Mock
  private AccountRepository accountRepository;


  @InjectMocks
  private AccountService accountService;

  @Test
  @DisplayName("계좌 조회 - 성공")
  void successShowAccount() {
    String email = "abc@gmail.com";
    AccountUser accountUser = AccountUser.builder()
        .email("abc@naver.com")
        .name("abc")
        .role("USER")
        .build();

    List<Account> accounts = Arrays.asList(
        new Account(1L, "abc@naver.com", "abc", AccountStatus.IN_USE, "a1", "111111-11-111111",
            100L, accountUser),
        new Account(2L, "abc@naver.com", "abc", AccountStatus.IN_USE, "a2", "222222-22-222222",
            200L, accountUser)
    );

    //given
    given(accountUserRepository.existsByEmail(anyString()))
        .willReturn(true);

    given(accountUserRepository.findByEmail(anyString()))
        .willReturn(Optional.of(accountUser));

    given(accountRepository.findByAccountUser(any()))
        .willReturn(accounts);
    //when
    List<AccountDto> accountDtoList = accountService.showAllAccounts(email);
    //then

    assertEquals(accounts.getFirst().getEmail(), accountDtoList.getFirst().getEmail());
    assertEquals(accounts.getFirst().getName(), accountDtoList.getFirst().getName());
    assertEquals(accounts.getFirst().getAccountStatus(),
        accountDtoList.getFirst().getAccountStatus());

    assertEquals("a1", accountDtoList.getFirst().getAccountName());
    assertEquals("a2", accountDtoList.get(1).getAccountName());

    assertEquals(100L, accountDtoList.getFirst().getBalance());
    assertEquals(200L, accountDtoList.get(1).getBalance());

    assertEquals("111111-11-111111", accountDtoList.getFirst().getAccountNumber());
    assertEquals("222222-22-222222", accountDtoList.get(1).getAccountNumber());

  }


  @Test
  @DisplayName("계좌 조회 - 실패(사용자 게정과 요청한 이메일이 서로 다른 경우)")
  void failShowAccount() {
    String email = "abc@gmail.com";
    AccountUser accountUser = AccountUser.builder()
        .email("abc@naver.com")
        .name("abc")
        .role("USER")
        .build();

    //given
    given(accountUserRepository.existsByEmail(anyString()))
        .willReturn(true);
    given(accountUserRepository.findByEmail(anyString()))
        .willReturn(Optional.empty());

    //when
    AccountException accountException = assertThrows(AccountException.class,
        () -> accountService.showAllAccounts(email));
    //then
    assertEquals(ErrorCode.EMAIL_NOT_MATCH, accountException.getErrorCode());
  }

  @Test
  @DisplayName("계좌 삭제 - 성공")
  void successDeleteAccount() {
    String email = "abc@gmail.com";
    String accountName = "a1";

    AccountUser accountUser = AccountUser.builder()
        .email("abc@naver.com")
        .name("abc")
        .role("USER")
        .build();

    List<Account> accounts = Arrays.asList(
        new Account(1L, "abc@naver.com", "abc", AccountStatus.IN_USE, "a1", "111111-11-111111", 0L,
            accountUser),
        new Account(2L, "abc@naver.com", "abc", AccountStatus.IN_USE, "a2", "222222-22-222222", 10L,
            accountUser)
    );

    //given

    given(accountUserRepository.findByEmail(anyString()))
        .willReturn(Optional.of(accountUser));
    given(accountRepository.findByAccountUser(any()))
        .willReturn(accounts);
    //when

    AccountDto accountDto = accountService.deleteAccount(email, accountName);
    //then
    assertEquals(AccountStatus.NOT_USE, accountDto.getAccountStatus());

  }

  @Test
  @DisplayName("계좌 삭제 - 실패(계좌에 잔액이 남아있는 데 계좌를 삭제하려는 경우)")
  void failDeleteAccount() {
    String email = "abc@gmail.com";
    String accountName = "a1";

    AccountUser accountUser = AccountUser.builder()
        .email("abc@naver.com")
        .name("abc")
        .role("USER")
        .build();

    List<Account> accounts = Arrays.asList(
        new Account(1L, "abc@naver.com", "abc", AccountStatus.IN_USE, "a1", "111111-11-111111",
            100L, accountUser),
        new Account(2L, "abc@naver.com", "abc", AccountStatus.IN_USE, "a2", "222222-22-222222", 10L,
            accountUser)
    );
    //given
    given(accountUserRepository.findByEmail(anyString()))
        .willReturn(Optional.of(accountUser));
    given(accountRepository.findByAccountUser(any()))
        .willReturn(accounts);

    //when
    AccountException accountException = assertThrows(AccountException.class,
        () -> accountService.deleteAccount(email, accountName));
    //then

    assertEquals(ErrorCode.BALANCE_EXISTS, accountException.getErrorCode());
  }
}