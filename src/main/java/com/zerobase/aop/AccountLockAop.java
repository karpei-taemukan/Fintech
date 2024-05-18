package com.zerobase.aop;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class AccountLockAop {
  // 안전한 거래를 위해 계좌에 lock 걸기

  // 동시에 여러 사용자가 계좌 잔액을 입금 출금이 발생할 수 있음 --> 멀티 쓰레드 환경
  // HashMap 보단 ConcurrentHashMap 이 적합
  private final ConcurrentHashMap<String, Lock> lockMap = new ConcurrentHashMap<>();

  // TransactionService
  @Around("execution(* com.zerobase.service.TransactionService.*(..)) && args(email,..)")
  public Object accountLock(ProceedingJoinPoint joinPoint, String email) throws Throwable {

    //ConcurrentHashMap 에 TransactionService 에서 가져온 첫 인자인 email 에 Lock 이 없다면 새로운 ReentrantLock 생성
    Lock lock = lockMap.computeIfAbsent(email, e -> new ReentrantLock(true));

    lock.lock(); // 락 획득
    Object proceed = null;
    try {
      log.info("##################### AOP START #####################");
      log.info("Current Tread  " + Thread.currentThread().getName() + "  User E-Mail: " + email);
      proceed = joinPoint.proceed(); // 실제 TransactionService 실행
    } catch (InterruptedException e) {
      log.error("fail to use balance ", e);
    } finally {
      lock.unlock(); // 락 해제
      log.info("##################### AOP END #####################");
    }

    return proceed;
  }
}
