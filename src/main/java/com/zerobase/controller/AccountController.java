package com.zerobase.controller;

import com.zerobase.dto.AccountDto;
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

  public static final String TOKEN_PREFIX = "Bearer ";
  private final AccountService accountService;
  private final JwtAuthProvider provider;


  private String getEmailFromToken(String token) {
    token = token.substring(TOKEN_PREFIX.length());
    return provider.getUserVo(token).getEmail();
  }

  // 유저 이름으로 등록된 모든 계좌 찾기
  @GetMapping("/show")
  private ResponseEntity<List<AccountDto>> showAccounts(
      @RequestHeader(name = "Authorization") String token
  ) {
    String email = getEmailFromToken(token);

    return ResponseEntity.ok(accountService.showAllAccounts(email));
  }


  @DeleteMapping("/delete")
  private ResponseEntity<AccountDto> deleteAccount(
      @RequestHeader(name = "Authorization") String token,
      @RequestParam("accountName") String accountName
  ){
    String email = getEmailFromToken(token);

    return ResponseEntity.ok(accountService.deleteAccount(email, accountName));
  }
}