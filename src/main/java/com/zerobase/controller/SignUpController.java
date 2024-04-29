package com.zerobase.controller;

import com.zerobase.domain.SignUpAccountForm;
import com.zerobase.domain.SignUpUserForm;
import com.zerobase.dto.AccountDto;
import com.zerobase.service.SignupService;
import jakarta.validation.Valid;
import java.util.List;
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

  private final SignupService signupService;
  public static final String TOKEN_PREFIX = "Bearer ";

  @PostMapping("/accountUser")
  private ResponseEntity<String> createUser(
      @RequestBody @Valid SignUpUserForm form
  ){
    return ResponseEntity.ok(signupService.userSignUp(form));
  }

// 인증메일의 url
  @GetMapping("/verify/user")
  private ResponseEntity<String> verifyUser(
      @RequestParam(name = "email") String email,
      @RequestParam(name = "code") String code
  ){
    signupService.userVerify(email, code);
    return ResponseEntity.ok("인증 완료");
  }


  @PostMapping("/account")
  private ResponseEntity<String> createAccount(
      @RequestHeader(name = "Authorization") String token,
      @RequestBody @Valid SignUpAccountForm form
  ){

    token = token.substring(TOKEN_PREFIX.length());

    AccountDto accountDto = signupService.accountSignUp(token, form);
    return ResponseEntity.ok(accountDto.getName()+"의 계정 생성 완료"+"\n"+
        "계좌 소유주 이름: "+ accountDto.getName() + "\n"+
        "계좌 이름: "+accountDto.getAccountName() + "\n"+
        "계좌 번호: "+accountDto.getAccountNumber()
    );
  }

}