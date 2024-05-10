package com.zerobase.service;

import static org.junit.jupiter.api.Assertions.*;

import com.zerobase.client.MailgunClient;
import com.zerobase.config.filter.JwtFilter;
import com.zerobase.domain.AccountUser;
import com.zerobase.domain.SignInForm;
import com.zerobase.exception.AccountException;
import com.zerobase.repository.AccountRepository;
import com.zerobase.repository.AccountUserRepository;
import com.zerobase.token.config.JwtAuthProvider;
import com.zerobase.token.domain.UserType;
import com.zerobase.token.domain.UserVo;
import com.zerobase.token.util.Aes256Util;
import com.zerobase.type.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SignInServiceTest {

  @Mock
  private JwtFilter jwtFilter;

  @Mock
  private JwtAuthProvider provider;

  @Mock
  private VerifyService verifyService;

  @Mock
  private AccountUserRepository accountUserRepository;

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private SignInService signInService;

  @Test
  @DisplayName("사용자 계정 signIn - 성공")
  void successSignInAccountUser() {
    SignInForm form = SignInForm.builder()
        .email("abc@naver.com")
        .password("abc123")
        .build();

    Claims claims = Jwts.claims()
        .setSubject(Aes256Util.encrypt("qwe"))
        .setAudience(Aes256Util.encrypt("abc@naver.com"))
        .setId(Aes256Util.encrypt("1"));

    Date now = new Date();
    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + 1000L * 60 * 60 * 24))
        .signWith(SignatureAlgorithm.HS256, "fintech-secretKey-springBoot")
        .compact();

    AccountUser accountUser = AccountUser.builder()
        .id(1L)
        .name("qwe")
        .email("abc@naver.com")
        .accountPassword("abc123")
        .verify(true)
        .build();

    //given

    given(accountUserRepository.findByEmail(anyString()).stream()
        .filter(user -> user.getAccountPassword().equals(anyString()) && user.isVerify())
        .findFirst())
        .willReturn(Optional.of(accountUser));

    given(provider.createToken(anyString(), anyLong(), any(), anyString()))
        .willReturn(token);

    given(provider.getUserVo(anyString()))
        .willReturn(new UserVo(1L, "qwe", "abc@naver.com", "USER"));
    //when

    String signInToken = signInService.accountSignIn(form);

    //then
    assertEquals(token, signInToken);
    assertEquals(provider.getUserVo(token).getEmail(), "abc@naver.com");
    assertEquals(provider.getUserVo(token).getName(), "qwe");
    assertEquals(provider.getUserVo(token).getId(), 1L);
    assertEquals(provider.getUserVo(token).getUserType(), "USER");
  }


  @Test
  @DisplayName("사용자 계정 signIn - 실패(비밀번호가 일치하지 않고, 인증되지 않은 경우)")
  void failSignInAccountUser() {

    SignInForm form = SignInForm.builder()
        .email("abc@naver.com")
        .password("abc123")
        .build();

    //given
    given(accountUserRepository.findByEmail(anyString()).stream()
        .filter(user -> user.getAccountPassword().equals(anyString()) && user.isVerify())
        .findFirst())
        .willReturn(Optional.empty());

    //when
    AccountException accountException = assertThrows(AccountException.class,
        () -> signInService.accountSignIn(form));

    //then
    assertEquals(ErrorCode.LOGIN_CHECK_FAIL, accountException.getErrorCode());
  }
}