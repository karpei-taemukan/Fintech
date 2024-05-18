package com.zerobase.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.config.SecurityConfig;
import com.zerobase.config.WebConfig;
import com.zerobase.dto.TransactionDto;
import com.zerobase.exception.AccountException;
import com.zerobase.repository.AccountRepository;
import com.zerobase.service.TransactionHistoryService;
import com.zerobase.token.config.JwtAuthProvider;
import com.zerobase.type.ErrorCode;
import com.zerobase.type.TransactionType;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.*;

@WebMvcTest(TransactionHistoryController.class)
@Import({SecurityConfig.class, WebConfig.class})
class TransactionHistoryControllerTest {

  @MockBean
  private TransactionHistoryService transactionHistoryService;

  @MockBean
  private JwtAuthProvider provider;

  @MockBean
  private AccountRepository accountRepository;
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;


  @Test
  @WithMockUser
  @DisplayName("거래 내역 조회 - 성공")
  void successTransactionHistory() throws Exception {

    Page<TransactionDto> page = new PageImpl<>(Arrays.asList(
        new TransactionDto("a1", 1000L, 2000L, TransactionType.DEPOSIT),
        new TransactionDto("a1", 2000L, 0L, TransactionType.WITHDRAW)
    ));
    //given
    given(transactionHistoryService.searchTransactionHistory(anyString(), any(), any(), any()))
        .willReturn(page);
    //when

    //then
    mockMvc.perform(MockMvcRequestBuilders.get("/search/date")
            .param("accountName", "a1")
            .param("startDate", "2024-05-05")
            .param("endDate", "2024-12-24")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].accountName").value("a1"))
        .andExpect(jsonPath("$.content[0].beforeTransaction").value(1000L))
        .andExpect(jsonPath("$.content[0].afterTransaction").value(2000L))
        .andExpect(jsonPath("$.content[0].transactionType").value("DEPOSIT"))
        .andExpect(jsonPath("$.content[1].accountName").value("a1"))
        .andExpect(jsonPath("$.content[1].beforeTransaction").value(2000L))
        .andExpect(jsonPath("$.content[1].afterTransaction").value(0L))
        .andExpect(jsonPath("$.content[1].transactionType").value("WITHDRAW"))
        .andDo(print());

  }

  @Test
  @WithMockUser
  @DisplayName("거래 내역 조회 - 실패(존재하지않는 계좌 이름으로 거래내역을 조회하려는 경우)")
  void failTransactionHistory() throws Exception {
    //given

    given(transactionHistoryService.searchTransactionHistory(anyString(), any(), any(), any()))
        .willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
    //when

    //then
    mockMvc.perform(MockMvcRequestBuilders.get("/search/date")
            .param("accountName", "aaa")
            .param("startDate", "2024-05-05")
            .param("endDate", "2024-12-24"))
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"))
        .andExpect(jsonPath("$.errorMessage").value("계좌가 없습니다"))
        .andDo(print());
  }
}