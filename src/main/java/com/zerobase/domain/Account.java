package com.zerobase.domain;

import com.zerobase.dto.AccountDto;
import com.zerobase.exception.AccountException;
import com.zerobase.type.AccountStatus;
import com.zerobase.type.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditOverride(forClass = BaseEntity.class)
public class Account extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;

  private String name;

  @Enumerated(EnumType.STRING)
  private AccountStatus accountStatus;

  @Column(unique = true)
  private String accountName;

  private String accountNumber;

  private Long balance;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private AccountUser accountUser;



  public static AccountDto from(Account account) {
    return AccountDto.builder()
        .email(account.getAccountUser().getEmail())
        .name(account.getAccountUser().getName())
        .accountName(account.getAccountName())
        .accountStatus(account.getAccountStatus())
        .accountNumber(account.getAccountNumber())
        .balance(account.getBalance())
        .build();
  }


  public void deposit(long balance){
    // 음수일 경우
    if(balance < 0){
      throw new AccountException(ErrorCode.INVALID_REQUEST_BALANCE);
    }
    this.balance += balance;
  }


  public void withdraw(long balance){
    // 출금금액 <= 계좌 금액 이 조건 만족 해야함
    if (balance > this.balance) {
      throw new AccountException(ErrorCode.ACCOUNT_BALANCE_NOT_ENOUGH);
    }

    // 음수일 경우
    if(balance < 0){
      throw new AccountException(ErrorCode.INVALID_REQUEST_BALANCE);
    }

    this.balance -= balance;
  }
}
