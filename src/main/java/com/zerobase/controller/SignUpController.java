package com.zerobase.controller;

import com.zerobase.domain.SignUpAccountForm;
import com.zerobase.domain.SignUpUserForm;
import com.zerobase.dto.AccountDto;
import com.zerobase.dto.AccountUserDto;
import com.zerobase.service.SignupService;
import com.zerobase.token.config.JwtAuthProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signUp")
@RequiredArgsConstructor
public class SignUpController {

  public static final String TOKEN_PREFIX = "Bearer ";
  private final SignupService signupService;

  private final JwtAuthProvider provider;

  @PostMapping("/accountUser")
  private ResponseEntity<AccountUserDto> createUser(
      @RequestBody @Valid SignUpUserForm form
  ) {
    return ResponseEntity.ok(signupService.userSignUp(form));
  }

  // 인증메일의 url
  @GetMapping("/verify/user")
  private ResponseEntity<String> verifyUser(
      @RequestParam(name = "email") String email,
      @RequestParam(name = "code") String code
  ) {
    signupService.userVerify(email, code);
    return ResponseEntity.ok("인증 완료");
  }


  @PostMapping("/account")
  private ResponseEntity<AccountDto> createAccount(
      @RequestHeader(name = "Authorization") String token,
      @RequestBody @Valid SignUpAccountForm form
  ) {

    token = token.substring(TOKEN_PREFIX.length());

    String email = provider.getUserVo(token).getEmail();

    return ResponseEntity.ok(signupService.accountSignUp(email, form)
    );
  }

}