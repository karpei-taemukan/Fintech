package com.zerobase.controller;

import com.zerobase.domain.SignInForm;
import com.zerobase.service.SignInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signIn")
@RequiredArgsConstructor
public class SignInController {

  private final SignInService signInService;
  // 사용자 계정 로그인
  // 토큰 발행
  @PostMapping("/accountUser")
  private ResponseEntity<String> signInUser(
      @RequestBody @Valid SignInForm form
  ){
    return ResponseEntity.ok(signInService.accountSignIn(form));
  }


}
