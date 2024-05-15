package com.zerobase.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.config.SecurityConfig;
import com.zerobase.domain.TransactionForm;
import com.zerobase.dto.AccountDto;
import com.zerobase.exception.AccountException;
import com.zerobase.service.AccountService;
import com.zerobase.token.config.JwtAuthProvider;
import com.zerobase.token.domain.UserVo;
import com.zerobase.token.util.Aes256Util;
import com.zerobase.type.AccountStatus;
import com.zerobase.type.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(AccountController.class)
@Import({SecurityConfig.class})
class AccountControllerTest {

  @MockBean
  private AccountService accountService;

  @MockBean
  private JwtAuthProvider provider;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;


  @Test
  @WithMockUser
  @DisplayName("계좌 조회 - 성공")
  void successShowAccount() throws Exception {
    Claims claims = Jwts.claims()
        .setSubject(Aes256Util.encrypt("abc"))
        .setAudience(Aes256Util.encrypt("abc@gmail.com"))
        .setId(Aes256Util.encrypt("1"));

    Date now = new Date();

    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + 1000L * 60 * 60 * 24))
        .signWith(SignatureAlgorithm.HS256, "fintech-secretKey-springBoot")
        .compact();

    List<AccountDto> accounts = Arrays.asList(
        new AccountDto("abc@gmail.com", "abc", "a1", "111111-11-111111",
            AccountStatus.IN_USE, 10000L),
        new AccountDto("abc@gmail.com", "abc", "a2", "222222-22-222222",
            AccountStatus.IN_USE, 20000L)
    );

    //given
    given(provider.getUserVo(anyString()))
        .willReturn(new UserVo(1L, "abc", "abc@gmail.com", "USER"));
    given(accountService.showAllAccounts(anyString()))
        .willReturn(accounts);
    //when

    //then

    mockMvc.perform(MockMvcRequestBuilders.get("/account/show")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].email").value("abc@gmail.com"))
        .andExpect(jsonPath("$[0].name").value("abc"))
        .andExpect(jsonPath("$[0].accountName").value("a1"))
        .andExpect(jsonPath("$[0].accountNumber").value("111111-11-111111"))
        .andExpect(jsonPath("$[0].accountStatus").value("IN_USE"))
        .andExpect(jsonPath("$[0].balance").value(10000L))
        .andExpect(jsonPath("$[1].email").value("abc@gmail.com"))
        .andExpect(jsonPath("$[1].name").value("abc"))
        .andExpect(jsonPath("$[1].accountName").value("a2"))
        .andExpect(jsonPath("$[1].accountNumber").value("222222-22-222222"))
        .andExpect(jsonPath("$[1].accountStatus").value("IN_USE"))
        .andExpect(jsonPath("$[1].balance").value(20000L))
        .andDo(print());

  }


  @Test
  @WithMockUser
  @DisplayName("계좌 조회 - 실패(등록되지 않은 이메일로 계좌를 조회한 경우)")
  void failShowAccount() throws Exception {
    Claims claims = Jwts.claims()
        .setSubject(Aes256Util.encrypt("abc"))
        .setAudience(Aes256Util.encrypt("abc@gmail.com"))
        .setId(Aes256Util.encrypt("1"));

    Date now = new Date();

    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + 1000L * 60 * 60 * 24))
        .signWith(SignatureAlgorithm.HS256, "fintech-secretKey-springBoot")
        .compact();

    //given
    given(provider.getUserVo(anyString()))
        .willReturn(new UserVo(1L, "abc", "abc@gmail.com", "USER"));
    given(accountService.showAllAccounts(anyString()))
        .willThrow(new AccountException(ErrorCode.USER_NOT_FOUND));
    //when
    //then

    mockMvc.perform(MockMvcRequestBuilders.get("/account/show")
            .header("Authorization", token))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"))
        .andExpect(jsonPath("$.errorMessage").value("사용자가 없습니다"))
        .andDo(print());
  }


  @Test
  @WithMockUser
  @DisplayName("계좌 삭제 - 성공")
  void successDeleteAccount() throws Exception {
    Claims claims = Jwts.claims()
        .setSubject(Aes256Util.encrypt("abc"))
        .setAudience(Aes256Util.encrypt("abc@gmail.com"))
        .setId(Aes256Util.encrypt("1"));

    Date now = new Date();

    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + 1000L * 60 * 60 * 24))
        .signWith(SignatureAlgorithm.HS256, "fintech-secretKey-springBoot")
        .compact();

    //given
    given(provider.getUserVo(anyString()))
        .willReturn(new UserVo(1L, "abc", "abc@gmail.com", "USER"));
    given(accountService.deleteAccount(anyString(), anyString()))
        .willReturn(AccountDto.builder()
            .email("abc@gmail.com")
            .name("abc")
            .accountName("a1")
            .accountNumber("111111-11-111111")
            .accountStatus(AccountStatus.NOT_USE)
            .balance(0L)
            .build());
    //when
    //then
    mockMvc.perform(MockMvcRequestBuilders.delete("/account/delete")
            .param("accountName", "a1")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("abc@gmail.com"))
        .andExpect(jsonPath("$.name").value("abc"))
        .andExpect(jsonPath("$.accountName").value("a1"))
        .andExpect(jsonPath("$.accountNumber").value("111111-11-111111"))
        .andExpect(jsonPath("$.accountStatus").value("NOT_USE"))
        .andExpect(jsonPath("$.balance").value(0L))
        .andDo(print());
  }


  @Test
  @WithMockUser
  @DisplayName("계좌 삭제 - 실패(등록되지 않은 이메일로 계좌를 삭제하려는 경우)")
  void failDeleteAccount() throws Exception {
    Claims claims = Jwts.claims()
        .setSubject(Aes256Util.encrypt("abc"))
        .setAudience(Aes256Util.encrypt("abc@gmail.com"))
        .setId(Aes256Util.encrypt("1"));

    Date now = new Date();

    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + 1000L * 60 * 60 * 24))
        .signWith(SignatureAlgorithm.HS256, "fintech-secretKey-springBoot")
        .compact();

    //given
    given(provider.getUserVo(anyString()))
        .willReturn(new UserVo(1L, "abc", "abc@gmail.com", "USER"));
    given(accountService.deleteAccount(anyString(), anyString()))
        .willThrow(new AccountException(ErrorCode.USER_NOT_FOUND));
    //when
    //then
    mockMvc.perform(MockMvcRequestBuilders.delete("/account/delete")
            .param("accountName", "a1")
            .header("Authorization", token))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"))
        .andExpect(jsonPath("$.errorMessage").value("사용자가 없습니다"))
        .andDo(print());
  }
}