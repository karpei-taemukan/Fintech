package com.zerobase.service;

import static org.junit.jupiter.api.Assertions.*;

import com.zerobase.domain.AccountUser;
import com.zerobase.exception.AccountException;
import com.zerobase.repository.AccountUserRepository;
import com.zerobase.token.util.Aes256Util;
import com.zerobase.type.ErrorCode;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VerifyServiceTest {

  @Mock
  private AccountUserRepository accountUserRepository;

  @InjectMocks
  private VerifyService verifyService;


  @Test
  @DisplayName("사용자 계정이 DB에 존재해, 정상적으로 토큰의 유효기간이 하루로 설정된 경우")
  void successChangeUserVerification() {
    Long userId = 1L;
    String verificationCode = "123456";
    AccountUser accountUser = AccountUser.builder()
        .email("abc@naver.com")
        .id(1L)
        .name("abc")
        .accountPassword("abc123")
        .build();

    //given
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(accountUser));
    //when

    LocalDateTime localDateTime
        = verifyService.ChangeUserVerification(userId, verificationCode);

    //then
    assertEquals(accountUser.getVerifyExpiredAt(), localDateTime);

  }

  @Test
  @DisplayName("사용자 계정이 DB에 존재하지 않아, 정상적으로 토큰의 유효기간이 설정 안된 경우")
  void failChangeUserVerification_UserNotFound() {
    Long userId = 1L;
    String verificationCode = "123456";

    //given
    given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.empty());
    //when
    AccountException accountException = assertThrows(AccountException.class,
        () -> verifyService.ChangeUserVerification(userId, verificationCode));
    //then
    assertEquals(ErrorCode.USER_NOT_FOUND, accountException.getErrorCode());
  }


  @Test
  void successLoadUserByUsername() {

    String name = "username";

    String encryptName = Aes256Util.encrypt(name);
    String decryptedName = Aes256Util.decrypt(name);

    AccountUser accountUser = AccountUser.builder()
        .name(decryptedName)
        .build();

    //given
    given(accountUserRepository.findByName(anyString()))
        .willReturn(Optional.of(accountUser));

    //when

    UserDetails userDetails = verifyService.loadUserByUsername(encryptName);

    //then

    assertEquals(accountUser.getUsername(), userDetails.getUsername());
  }

  @Test
  void failLoadUserByUsername() {

    String name = "username";

    String encryptName = Aes256Util.encrypt(name);
    String decryptedName = Aes256Util.decrypt(name);

    AccountUser accountUser = AccountUser.builder()
        .name(decryptedName)
        .build();

    //given
    given(accountUserRepository.findByName(anyString()))
        .willReturn(Optional.empty());

    //when
    //then
    assertThrows(UsernameNotFoundException.class,
        () -> verifyService.loadUserByUsername(encryptName));


  }
}