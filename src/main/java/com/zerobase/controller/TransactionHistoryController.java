package com.zerobase.controller;

import com.zerobase.dto.TransactionDto;
import com.zerobase.service.TransactionHistoryService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class TransactionHistoryController {

  private final TransactionHistoryService transactionHistoryService;

  @GetMapping("/date")
  private ResponseEntity<Page<TransactionDto>> searchTransactionHistory(
      @RequestParam("accountName") String accountName,
      @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    return ResponseEntity.ok(
        transactionHistoryService.searchTransactionHistory(accountName, startDate, endDate,
            pageable));
  }
}
