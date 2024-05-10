package com.zerobase.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.config.SecurityConfig;
import com.zerobase.domain.TransactionForm;
import com.zerobase.dto.AccountDto;
import com.zerobase.exception.AccountException;
import com.zerobase.service.TransactionService;
import com.zerobase.token.config.JwtAuthProvider;
import com.zerobase.token.domain.UserVo;
import com.zerobase.token.util.Aes256Util;
import com.zerobase.type.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import({SecurityConfig.class})
class TransactionControllerTest {

  @MockBean
  private TransactionService transactionService;
  @MockBean
  private JwtAuthProvider provider;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;


  @Test
  @WithMockUser
  @DisplayName("계정에 입금하기 - 성공")
  void successDepositAccount() throws Exception {
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

    given(transactionService.depositAccount(anyString(), any()))
        .willReturn(AccountDto.builder()
            .email("abc@gmail.com")
            .name("abc")
            .accountName("aaa")
            .accountNumber("123456-78-910119")
            .balance(10000L)
            .build());

    //when

    //then
    mockMvc.perform(MockMvcRequestBuilders.post("/transaction/deposit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new TransactionForm("aaa", 10000L)
            ))
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountName").value("aaa"))
        .andExpect(jsonPath("$.balance").value(10000L))
        .andDo(print());
  }

  @Test
  @WithMockUser
  @DisplayName("존재하지않는 사용자 계정에 돈을 입금하는 경우")
  void failDepositAccount() throws Exception {
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
        .willReturn(new UserVo(1L, "abc", "abcabc@gmail.com", "USER"));

    given(transactionService.depositAccount(anyString(), any()))
        .willThrow(new AccountException(ErrorCode.USER_NOT_FOUND));

    //when
    //then
    mockMvc.perform(MockMvcRequestBuilders.post("/transaction/deposit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new TransactionForm("abc", 10000L)
            ))
            .header("Authorization", token))
        .andDo(print())
        .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"))
        .andExpect(jsonPath("$.errorMessage").value("사용자가 없습니다"))
        .andExpect(status().is4xxClientError());

  }


  @Test
  @WithMockUser
  @DisplayName("계정에 출금하기 - 성공")
  void successWithdrawAccount() throws Exception {
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

    given(transactionService.withdrawAccount(anyString(), any()))
        .willReturn(AccountDto.builder()
            .email("abc@gmail.com")
            .name("abc")
            .accountName("aaa")
            .accountNumber("123456-78-910119")
            .balance(9000L)
            .build());

    //when
    //then
    mockMvc.perform(MockMvcRequestBuilders.post("/transaction/withdraw")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new TransactionForm("aaa", 1000L)
            ))
            .header("Authorization", token))
        .andExpect(jsonPath("$.accountName").value("aaa"))
        .andExpect(jsonPath("$.balance").value(9000L))
        .andDo(print());

  }


  @Test
  @WithMockUser
  @DisplayName("존재하지않는 사용자 계정에서 돈을 출금하려는 경우")
  void failWithdrawAccount() throws Exception {
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
        .willReturn(new UserVo(1L, "abc", "abcabc@gmail.com", "USER"));

    given(transactionService.withdrawAccount(anyString(), any()))
        .willThrow(new AccountException(ErrorCode.USER_NOT_FOUND));

    //when
    //then
    mockMvc.perform(MockMvcRequestBuilders.post("/transaction/withdraw")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new TransactionForm("abc", 1000L)
            ))
            .header("Authorization", token))
        .andDo(print())
        .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"))
        .andExpect(jsonPath("$.errorMessage").value("사용자가 없습니다"))
        .andExpect(status().is4xxClientError());
  }
}