package com.zerobase.service;

import com.zerobase.config.filter.JwtFilter;
import com.zerobase.domain.Transaction;
import com.zerobase.dto.TransactionDto;
import com.zerobase.exception.AccountException;
import com.zerobase.repository.AccountRepository;
import com.zerobase.repository.TransactionRepository;
import com.zerobase.token.config.JwtAuthProvider;
import com.zerobase.type.ErrorCode;
import com.zerobase.type.TransactionType;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionHistoryServiceTest {

  @Mock
  private JwtFilter jwtFilter;

  @Mock
  private JwtAuthProvider provider;

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private TransactionRepository transactionRepository;

  @InjectMocks
  private TransactionHistoryService transactionHistoryService;


  @Test
  @DisplayName("거래 내역 조회 - 성공")
  void successTransactionHistory() {
    Page<Transaction> page = new PageImpl<>(Arrays.asList(
        new Transaction(1L, 1L, "a1", 1000L, 2000L, LocalDate.now(), TransactionType.DEPOSIT),
        new Transaction(1L, 1L, "a1", 2000L, 0L, LocalDate.now(), TransactionType.WITHDRAW)
    ));

    PageRequest pageRequest = PageRequest.of(
        0, 30, Sort.by(Sort.Direction.DESC, "createdAt")
    );

    //given
    given(accountRepository.existsByAccountName(anyString()))
        .willReturn(true);
    given(transactionRepository.findAllByDateBetween(any(), any(), any()))
        .willReturn(page);
    //when
    Page<TransactionDto> pages = transactionHistoryService.searchTransactionHistory("a1",
        LocalDate.now(), LocalDate.now().plusMonths(2), pageRequest);
    //then
    assertEquals("a1", pages.get().toList().getFirst().getAccountName());
    assertEquals(TransactionType.DEPOSIT, pages.get().toList().getFirst().getTransactionType());
    assertEquals(1000L, pages.get().toList().getFirst().getBeforeTransaction());
    assertEquals(2000L, pages.get().toList().getFirst().getAfterTransaction());

    assertEquals("a1", pages.get().toList().get(1).getAccountName());
    assertEquals(TransactionType.WITHDRAW, pages.get().toList().get(1).getTransactionType());
    assertEquals(2000L, pages.get().toList().get(1).getBeforeTransaction());
    assertEquals(0L, pages.get().toList().get(1).getAfterTransaction());
  }


  @Test
  @DisplayName("거래 내역 조회 - 실패(존재하지않는 계좌 이름으로 거래내역을 조회하려는 경우)")
  void failTransactionHistory() {

    PageRequest pageRequest = PageRequest.of(
        0, 30, Sort.by(Sort.Direction.DESC, "createdAt")
    );
    //given
    given(accountRepository.existsByAccountName(anyString()))
        .willReturn(false);
    //when
    AccountException accountException = assertThrows(AccountException.class,
        () -> transactionHistoryService.searchTransactionHistory("a1", LocalDate.now(),
            LocalDate.now().plusMonths(2), pageRequest));
    //then
    assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, accountException.getErrorCode());
  }
}