# 간단한 계좌 시스템 만들기 

  계좌 송금과 계좌 CRUD 등 계좌 API를 구현 
  
# 프로젝트 기능 및 설계

-   계좌 관리 (생성/삭제/금액 인출/금액 입금)

	  - 사용자 가입 기능 and 계좌 생성 기능
     
		- 사용자는 가입과 동시에 계좌 1개가 자동 생성된다 이때, 최대 5개의 계좌 생성이 가능하다 (계좌 DB에 저장된 이메일의 수가 5개라면 더이상 계좌 생성 불가)

		- 사용자 가입 시 email 과 password, name 들을 입력 받는다 
		  이때, email 은 unique 해야한다
	
		- 초기 잔액은 요청을 줄때  0원으로 설정

		- mailgun을 이용해, 인증 이메일 발송
	
		- User 권한을 가진다 

	- 계좌 삭제 기능
		
		- 계좌가 있는 사용자인지 체크 후  계좌상태를 삭제 상태로 변경
	
	- 금액 인출 	
		
		- 로그인 정보와 사용자의 계좌가  일치하는 지 체크
		- 계좌 잔액이 0원이라면 인출 x 
		- 인출할 금액 <= 계좌금액 인 경우만 인출 가능 
		- 인출이 성공한다면 계좌 잔액에서 인출한 금액만큼을 차감한다
		- 인출이 실패한다면 계좌 잔액을 유지한다 	

	- 금액 입금

		- 로그인 정보와 사용자의 계좌가  일치하는 지 체크
		
		
-  로그인/로그아웃에 따른 계좌 접근 허가 기능 구현
	
	- 로그인 시 JWT 토큰 발행 
	
	- 로그인 시에만 계좌에 접근 허가 

	- 로그아웃 요청이 오면 계좌 접근 불가 
	

- 계좌 검색 기능
	
  - 가입된 계좌가 있는 지 체크
	
  - 만약 가입한 계좌가 여러개라면 전부 조회한다


-  송금 기능 및 송금 이력 조회
    - 송금 기능 

       - 로그인 정보와 사용자의 계좌가  일치하는 지 체크
       - 계좌 잔액이 0원이라면 송금 x 
	    - 송금할 금액 <= 계좌금액 인 경우만 송금 가능 
	    - 송금이 성공한다면 계좌 잔액에서 송금한 금액만큼을 차감한다
	    - 송금이 실패한다면 계좌 잔액을 유지한다 	

 -	송금 이력 조회

    - 사용자의 계좌와 송금 이력 조회할 계좌와 일치하는 지 체크 
    - 송금하는 순간 바로 송금된 금액과 송금 전 계좌의 금액과 송금 후 계좌의 금액을 DB에 저장


# ERD
	
![dbdb1](https://github.com/karpei-taemukan/Fintech/assets/91212680/1f51baf1-7893-4235-adea-f0e2737942a0)


# Tech Stack
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-green)
![Gradle](https://img.shields.io/badge/Gradle-8.7-yellow)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Spring Security](https://img.shields.io/badge/Spring%20Security-orange)
![Git](https://img.shields.io/badge/Git-lightgrey)
![Mailgun](https://img.shields.io/badge/Mailgun-red)


