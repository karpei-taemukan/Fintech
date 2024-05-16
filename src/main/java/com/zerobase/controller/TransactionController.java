package com.zerobase.controller;

import com.zerobase.domain.TransactionForm;
import com.zerobase.dto.AccountDto;
import com.zerobase.dto.TokenDto;
import com.zerobase.service.TransactionService;
import com.zerobase.token.config.JwtAuthProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @PostMapping("/deposit")
  private ResponseEntity<AccountDto> depositAccount(
      @RequestBody @Valid TransactionForm form,
      TokenDto tokenDto
  ) {
    return ResponseEntity.ok(transactionService.depositAccount(tokenDto.getEmail(), form));
  }


  @PostMapping("/withdraw")
  private ResponseEntity<AccountDto> withdrawAccount(
      @RequestBody @Valid TransactionForm form,
      TokenDto tokenDto
  ) {
    return ResponseEntity.ok(transactionService.withdrawAccount(tokenDto.getEmail(), form));
  }
}
