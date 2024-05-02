package com.zerobase.service;

import com.zerobase.client.MailgunClient;
import com.zerobase.domain.Account;
import com.zerobase.domain.AccountUser;
import com.zerobase.domain.SendMailForm;
import com.zerobase.domain.SignUpAccountForm;
import com.zerobase.dto.AccountDto;
import com.zerobase.exception.AccountException;
import com.zerobase.exception.CertificationException;
import com.zerobase.repository.AccountRepository;
import com.zerobase.repository.AccountUserRepository;
import com.zerobase.domain.SignUpUserForm;
import com.zerobase.token.config.JwtAuthProvider;
import com.zerobase.type.AccountStatus;
import com.zerobase.type.ErrorCode;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {

  private final AccountUserRepository accountUserRepository;

  private final AccountRepository accountRepository;

  private final MailgunClient mailgunClient;

  private final VerifyService verifyService;

  private final JwtAuthProvider provider;

  // 계좌 번호 생성기
  public String generateAccountNumber() {
    Random random = new Random();

    // 첫 번째 6자리 숫자 생성 (100000 이상 999999 이하)
    int firstPart = 100000 + random.nextInt(900000);

    // 두 번째 2자리 숫자 생성 (00 이상 99 이하)
    int secondPart = random.nextInt(100);

    // 세 번째 6자리 숫자 생성 (100000 이상 999999 이하)
    int thirdPart = 100000 + random.nextInt(900000);

    String accountNumber = String.format("%d-%02d-%d", firstPart, secondPart, thirdPart);


    // 생성한 계좌번호가 이미 있는 경우 재귀호출로 계좌 번호 다시 생성
    if(accountRepository.existsByAccountNumber(accountNumber)){
       accountNumber = generateAccountNumber();
    }

    // 계좌 번호를 문자열로 조합하여 반환
    return accountNumber;
  }

  public AccountUser signup(SignUpUserForm form) {
    return accountUserRepository.save(AccountUser.from(form));
  }

  private String getRandomCode() {
    return RandomStringUtils.random(10, true, true);
  }

  private String getVerificationEmailBody(String email, String name, String code) {
    StringBuilder sb = new StringBuilder();
    return sb.append("Hello ").append(name)
        .append("! Please Click Link for verification. \n")
        .append("http://localhost:8080/signUp")
        .append("/verify/")
        .append("user")
        .append("?email=")
        .append(email)
        .append("&code=")
        .append(code)
        .toString();
  }

  @Transactional
  public String userSignUp(SignUpUserForm form) {
    // 이미 가입된 이메일이 있는 지 체크
    if (accountUserRepository.existsByEmail(form.getEmail())) {
      throw new AccountException(ErrorCode.ACCOUNT_ALREADY_EXIST);
    } else {
      AccountUser accountUser = signup(form);

      String code = getRandomCode();

      SendMailForm sendMailForm = SendMailForm.builder()
          .from("zerobase-Auth@email.com")
          .to(form.getEmail())
          .subject("Verification Email")
          .text(getVerificationEmailBody(form.getEmail(), form.getName(), code))
          .build();

      log.info(String.format("[%s] -> %s", "EMAIL FORM", sendMailForm));

      mailgunClient.sendEmail(sendMailForm);

      verifyService.ChangeUserVerification(accountUser.getId(), code);

      return "회원 가입 성공";
    }
  }

  @Transactional
  public void userVerify(String email, String code) {
    AccountUser accountUser = accountUserRepository.findByEmail(email)
        .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

    if (!accountUser.getVerificationCode().equals(code)) {
      throw new CertificationException(ErrorCode.WRONG_VERIFICATION);
    }

    if (accountUser.isVerify()) {
      throw new AccountException(ErrorCode.ACCOUNT_ALREADY_EXIST);
    }

    if (accountUser.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
      throw new CertificationException(ErrorCode.EXPIRE_CODE);
    }

    accountUser.setVerify(true);

    accountUserRepository.save(accountUser);
  }

  public AccountDto accountSignUp(String token, SignUpAccountForm form) {
    // email 은 unique 하기 때문에 email 로
    // 토큰에 있는 이메일하고 form 의 이메일하고 비교

    if (!provider.getUserVo(token).getEmail().equals(form.getEmail())) {
      throw new AccountException(ErrorCode.EMAIL_NOT_MATCH);
    }

    /* Account 가 5개 라면
     * AccountUser 의 이메일로 가입된 Account 가 5개 이다
     */

    AccountUser accountUser = getAccountUser(form.getEmail());

    // 계좌의 갯수가 5개인지 체크
    validateCreateAccount(accountUser);

    String accountNumber = generateAccountNumber();

    Integer accountCount = accountRepository.countByAccountNumber(accountNumber);


    return AccountDto.from(
        accountRepository.save(
            Account.builder()
                .email(form.getEmail())
                .name(form.getName())
                .accountStatus(AccountStatus.IN_USE)
                .accountName(form.getAccountName())
                .accountNumber(accountNumber)
                .balance(0L)
                .accountUser(accountUser)
                .build()
        )
    );

  }


  private AccountUser getAccountUser(String email) {
    return accountUserRepository.findByEmail(email).get();
  }

  private void validateCreateAccount(AccountUser accountUser) {
    if (accountRepository.countByEmail(accountUser.getEmail()) == 5) {
      throw new AccountException(ErrorCode.ACCOUNT_MAX);
    }
  }


}
