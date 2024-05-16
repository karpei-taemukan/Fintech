package com.zerobase.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zerobase.client.MailgunClient;
import com.zerobase.config.filter.JwtFilter;
import com.zerobase.domain.Account;
import com.zerobase.domain.AccountUser;
import com.zerobase.domain.SignUpAccountForm;
import com.zerobase.domain.SignUpUserForm;
import com.zerobase.dto.AccountDto;
import com.zerobase.dto.AccountUserDto;
import com.zerobase.exception.AccountException;
import com.zerobase.repository.AccountRepository;
import com.zerobase.repository.AccountUserRepository;
import com.zerobase.token.config.JwtAuthProvider;
import com.zerobase.type.AccountStatus;
import com.zerobase.type.ErrorCode;
import com.zerobase.type.LoginCheck;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SignupServiceTest {

  @Mock
  private JwtFilter jwtFilter;

  @Mock
  private JwtAuthProvider provider;

  @Mock
  private MailgunClient mailgunClient;

  @Mock
  private VerifyService verifyService;

  @Mock
  private AccountUserRepository accountUserRepository;

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private SignupService signupService;

  @Test
  @DisplayName("사용자 계정 만들기 - 성공")
  void successCrateAccountUser() {
    SignUpUserForm form = SignUpUserForm.builder()
        .email("abc@gmail.com")
        .name("abc")
        .password("abc123")
        .role("ROLE_USER")
        .build();

    //given
    given(accountUserRepository.save(any()))
        .willReturn(AccountUser.builder()
            .email(form.getEmail())
            .name(form.getName())
            .accountPassword(form.getPassword())
            .role(form.getRole())
            .loginCheck(LoginCheck.LOGIN)
            .build());
    ArgumentCaptor<AccountUser> captor = ArgumentCaptor.forClass(AccountUser.class);

    //when
    AccountUserDto accountUserDto = signupService.userSignUp(form);
    //then
    verify(accountUserRepository, times(1)).save(captor.capture());
    assertEquals(form.getEmail(), accountUserDto.getEmail());
    assertEquals(form.getName(), accountUserDto.getName());
  }

  @Test
  @DisplayName("사용자 계정 만들기 - 실패(이미 가입된 이메일이 있는 경우)")
  void failCreateAccountUser() {
    SignUpUserForm form = SignUpUserForm.builder()
        .email("abc@gmail.com")
        .name("abc")
        .password("abc123")
        .role("ROLE_USER")
        .build();

    // given
    given(accountUserRepository.existsByEmail(anyString()))
        .willReturn(true);

    //when
    AccountException accountException = assertThrows(AccountException.class,
        () -> signupService.userSignUp(form));

    //then
    assertEquals(ErrorCode.ACCOUNT_ALREADY_EXIST, accountException.getErrorCode());
  }


  @Test
  @DisplayName("계좌 만들기 - 성공")
  void successCreateAccount() {

    SignUpAccountForm form = SignUpAccountForm.builder()
        .email("abc@gmail.com")
        .name("abc")
        .accountName("aaa")
        .build();

    //given

    given(accountUserRepository.findByEmail(anyString()))
        .willReturn(Optional.of(AccountUser.builder()
            .email("abc@gmail.com")
            .name("abc")
            .build()));

    given(accountRepository.save(any()))
        .willReturn(Account.builder()
            .id(1L)
            .email("abc@gmail.com")
            .name("abc")
            .accountStatus(AccountStatus.IN_USE)
            .accountName("aaa")
            .accountNumber("111111-22-333333")
            .balance(0L)
            .accountUser(AccountUser.builder()
                .email("abc@gmail.com")
                .name("abc")
                .accountPassword("abc123")
                .build())
            .build());

    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
    //when
    AccountDto accountDto = signupService.accountSignUp("abc@gmail.com", form);
    //then
    verify(accountRepository, times(1)).save(captor.capture());

    assertEquals("abc@gmail.com", accountDto.getEmail());
    assertEquals("abc", accountDto.getName());
    assertEquals("aaa", accountDto.getAccountName());
    assertEquals(0L, accountDto.getBalance());
    assertEquals("111111-22-333333", accountDto.getAccountNumber());
  }


  @Test
  @DisplayName("계좌 만들기 - 실패(계좌의 갯수가 최대인 경우)")
  void failCrateAccount() {

    SignUpAccountForm form = SignUpAccountForm.builder()
        .email("abc@gmail.com")
        .name("abc")
        .accountName("aaa")
        .build();

    AccountUser accountUser = AccountUser.builder()
        .email("abc@gmail.com")
        .name("abc")
        .build();

    //given

    given(accountUserRepository.findByEmail(anyString()))
        .willReturn(Optional.of(accountUser));

    given(accountRepository.countByEmail(any()))
        .willReturn(5);

    //when
    AccountException accountException = assertThrows(AccountException.class,
        () -> signupService.accountSignUp("abc@gmail.com", form));
    //then

    assertEquals(ErrorCode.ACCOUNT_MAX, accountException.getErrorCode());
  }
}