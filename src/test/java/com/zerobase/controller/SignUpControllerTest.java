package com.zerobase.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.config.SecurityConfig;
import com.zerobase.domain.SignUpAccountForm;
import com.zerobase.domain.SignUpUserForm;
import com.zerobase.dto.AccountDto;
import com.zerobase.dto.AccountUserDto;
import com.zerobase.exception.AccountException;
import com.zerobase.service.SignupService;
import com.zerobase.token.config.JwtAuthProvider;
import com.zerobase.token.domain.UserType;
import com.zerobase.token.domain.UserVo;
import com.zerobase.token.util.Aes256Util;
import com.zerobase.type.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.LocalDateTime;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@WebMvcTest(SignUpController.class)
@Import({SecurityConfig.class})
class SignUpControllerTest {

  @MockBean
  private SignupService signupService;
  @MockBean
  private JwtAuthProvider provider;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;


  @Test
  @DisplayName("사용자 계정 만들기 컨트롤러 요청 - 성공")
  void successCreateAccountUser() throws Exception {
    //given
    given(signupService.userSignUp(any()))
        .willReturn(AccountUserDto.builder()
            .email("l07wkdgustn07l@gmail.com")
            .name("qwe")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build());
    //when
    //then

    mockMvc.perform(MockMvcRequestBuilders.post("/signUp/accountUser")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new SignUpUserForm("l07wkdgustn07l@gmail.com", "qwe", "asd", "ROLE_USER")
            )))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("l07wkdgustn07l@gmail.com"))
        .andExpect(jsonPath("$.name").value("qwe"))
        .andDo(print());


  }


  @Test
  @DisplayName("사용자 계정 만들기 컨트롤러 요청 - 실패")
  void failCreateAccountUser() throws Exception {
    //given
    given(signupService.userSignUp(any()))
        .willThrow(new AccountException(
            ErrorCode.ACCOUNT_ALREADY_EXIST
        ));
    //when
    //then
    mockMvc.perform(MockMvcRequestBuilders.post("/signUp/accountUser")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new SignUpUserForm("l07wkdgustn07l@gmail.com", "qwe", "asd", "ROLE_USER")
            )))
        .andDo(print())
        .andExpect(jsonPath("$.errorCode").value("ACCOUNT_ALREADY_EXIST"))
        .andExpect(jsonPath("$.errorMessage").value("이미 있는 계좌입니다"))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @DisplayName("계좌 만들기 컨트롤러 요청 - 성공")
  void successCreateAccount() throws Exception {
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
    given(provider.getUserVo(anyString()))
        .willReturn(new UserVo(1L, "qwe", "l07wkdgustn07l@gmail.com", "USER"));

    given(signupService.accountSignUp(anyString(), any()))
        .willReturn(AccountDto.builder()
            .email("l07wkdgustn07l@gmail.com")
            .name("qwe")
            .accountName("aaa")
            .accountNumber("123456-78-910119")
            .balance(0L)
            .build());

    //when

    //then
    mockMvc.perform(MockMvcRequestBuilders.post("/signUp/account")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new SignUpAccountForm("l07wkdgustn07l@gmail.com", "asd", "aaa")
            ))
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("l07wkdgustn07l@gmail.com"))
        .andExpect(jsonPath("$.name").value("qwe"))
        .andExpect(jsonPath("$.accountName").value("aaa"))
        .andExpect(jsonPath("$.accountNumber").value("123456-78-910119"))
        .andExpect(jsonPath("$.balance").value(0L))
        .andDo(print());
  }


  @Test
  @DisplayName("계좌 만들기 컨트롤러 요청 - 실패")
  void failCreateAccount() throws Exception {

    Claims claims = Jwts.claims()
        .setSubject(Aes256Util.encrypt("qwe"))
        .setAudience(Aes256Util.encrypt("l07wkdgustn07l@gmail.com"))
        .setId(Aes256Util.encrypt("1"));
    claims.put("role", UserType.USER);
    Date now = new Date();
    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + 1000L * 60 * 60 * 24))
        .signWith(SignatureAlgorithm.HS256, "fintech-secretKey-springBoot")
        .compact();

    //given

    given(provider.getUserVo(anyString()))
        .willReturn(new UserVo(1L, "qwe", "l07wkdgustn07l@gmail.com", "USER"));

    given(signupService.accountSignUp(anyString(), any()))
        .willThrow(new AccountException(ErrorCode.EMAIL_NOT_MATCH));
    //when
    //then
    mockMvc.perform(MockMvcRequestBuilders.post("/signUp/account")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new SignUpAccountForm("l07wkdgustn07l@naver.com", "asd", "aaa")
            )).header("Authorization", token))
        .andDo(print())
        .andExpect(jsonPath("$.errorCode").value("EMAIL_NOT_MATCH"))
        .andExpect(jsonPath("$.errorMessage").value("사용자의 이메일이 다릅니다"))
        .andExpect(status().is4xxClientError());
  }
}