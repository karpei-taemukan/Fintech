package com.zerobase.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.config.SecurityConfig;
import com.zerobase.domain.SignInForm;
import com.zerobase.exception.AccountException;
import com.zerobase.service.SignInService;
import com.zerobase.token.config.JwtAuthProvider;
import com.zerobase.token.util.Aes256Util;
import com.zerobase.type.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(SignInController.class)
@Import({SecurityConfig.class})
class SignInControllerTest {

  @MockBean
  private SignInService signInService;

  @MockBean
  private JwtAuthProvider provider;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;


  @Test
  void successLogin() throws Exception {

    Claims claims = Jwts.claims()
        .setSubject(Aes256Util.encrypt("qwe"))
        .setAudience(Aes256Util.encrypt("l07wkdgustn07l@gmail.com"))
        .setId(Aes256Util.encrypt("1"));

    Date now = new Date();

    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + 1000L * 60 * 60 * 24))
        .signWith(SignatureAlgorithm.HS256, "fintech-secretKey-springBoot")
        .compact();

    //given
    given(signInService.accountSignIn(any()))
        .willReturn(token);
    //when
    //then
    mockMvc.perform(MockMvcRequestBuilders.post("/signIn/accountUser")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new SignInForm("l07wkdgustn07l@gmail.com", "asd")
            )))
        .andExpect(status().isOk())
        .andExpect(content().string(token))
        .andDo(print());
  }


  @Test
  void failLogin() throws Exception {
    //given
    given(signInService.accountSignIn(any()))
        .willThrow(new AccountException(ErrorCode.LOGIN_CHECK_FAIL));
    //when
    //then
    mockMvc.perform(MockMvcRequestBuilders.post("/signIn/accountUser")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new SignInForm("l07wkdgustn07l@gmail.com", "zxc")
            )))
        .andDo(print())
        .andExpect(jsonPath("$.errorCode").value("LOGIN_CHECK_FAIL"))
        .andExpect(jsonPath("$.errorMessage").value("로그인 실패"))
        .andExpect(status().is4xxClientError());
  }
}