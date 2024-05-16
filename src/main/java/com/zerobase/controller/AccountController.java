package com.zerobase.controller;

import com.zerobase.dto.AccountDto;
import com.zerobase.dto.TokenDto;
import com.zerobase.service.AccountService;
import com.zerobase.token.config.JwtAuthProvider;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @GetMapping("/show")
  private ResponseEntity<List<AccountDto>> showAccounts(
      TokenDto tokenDto
  ) {
    return ResponseEntity.ok(accountService.showAllAccounts(tokenDto.getEmail()));
  }


  @DeleteMapping("/delete")
  private ResponseEntity<AccountDto> deleteAccount(
      @RequestParam("accountName") String accountName,
      TokenDto tokenDto
  ) {
    return ResponseEntity.ok(accountService.deleteAccount(tokenDto.getEmail(), accountName));
  }
}