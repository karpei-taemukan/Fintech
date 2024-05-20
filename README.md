# 프로젝트 주제 :heavy_check_mark:

  - 간단한 계좌 시스템 만들기 --> 계좌 송금과 계좌 CRUD 등 계좌 API를 구현 

# 프로젝트 구조 :heavy_check_mark:
	
  ![qwe](https://github.com/karpei-taemukan/Fintech/assets/91212680/453c468c-27f7-4ffa-87c4-e4e80f11c672)

# 프로젝트 기능 및 설계 :heavy_check_mark:

-   계좌 관리 (생성/삭제/금액 인출/금액 입금)

	  - 사용자 가입 기능 and 계좌 생성 기능
     
		- 사용자는 가입과 동시에 계좌 1개가 자동 생성된다 이때, 최대 5개의 계좌 생성이 가능하다 (계좌 DB에 저장된 이메일의 수가 5개라면 더이상 계좌 생성 불가)

		- 사용자 가입 시 email 과 password, name 들을 입력 받는다 
		  이때, email 은 unique 해야한다
	
		- 초기 잔액은 가입 요청을 줄때 0원으로 설정

		- mailgun을 이용해, 인증 이메일 발송
	
		- User 권한을 가진다 

	- 계좌 삭제 기능
		
		- 계좌가 있는 사용자인지 체크 후 계좌상태를 삭제 상태로 변경
	
	- 금액 인출 	
		
		- 토큰의 이메일 정보와 사용자의 계좌DB의 이메일이 일치하는 지 체크
		- 계좌 잔액이 0원이라면 인출 x 
		- 인출할 금액 <= 계좌금액 인 경우만 인출 가능 
		- 인출이 성공한다면 계좌 잔액에서 인출한 금액만큼을 차감한다
		- 인출이 실패한다면 계좌 잔액을 유지한다 	

	- 금액 입금

		- 로그인 정보와 사용자의 계좌가  일치하는 지 체크
		
	- 로그인
	
		- 로그인 시 JWT 토큰 발행(토큰 페이로드(subject)에 이메일 정보 세팅) 


- 계좌 검색 기능
	
  - 가입된 계좌가 있는 지 체크
	
  - 만약 가입한 계좌가 여러개라면 전부 조회한다


- 계좌 Lock 기능
  	- Spring 의 AOP 를 이용하여, 계좌에서 출금과 입금이 발생할때 Lock 을 걸어, 멀티 스레드 환경에서 데이터 일관성이 유지되도록 한다 


-  송금 기능 및 송금 이력 조회
    - 송금 기능 

       - 토큰의 이메일 정보와 사용자의 계좌DB의 이메일이 일치하는 지 체크
       - 계좌 잔액이 0원이라면 송금 x 
	    - 송금할 금액 <= 계좌금액 인 경우만 송금 가능 
	    - 송금이 성공한다면 계좌 잔액에서 송금한 금액만큼을 차감한다
	    - 송금이 실패한다면 계좌 잔액을 유지한다 	
	   - 송금하는 순간 바로 송금 전 계좌의 금액과 송금 후 계좌의 금액을 거래내역DB에 저장
       
- 송금 이력 조회

    - 한 이메일로 최대 5개의 계좌가 가입할 수 있다 --> 각각의 계좌에 대해 송금이력을 구분하는 게 불가 --> 거래내역DB에 계좌 별칭 추가 --> 계좌 별칭으로 각각의 계좌에 대한 송금이력 조회 가능  
      
# Trouble Shooting    
   - 헤더에 토큰을 넣고 요청을 했을 때 발생한 에러
     
      ``` com.fasterxml.jackson.core.jsonparseexception: illegal character ((ctrl-char, code 5)): only regular white space (\r, \n, \t) is allowed between tokens ```
	
 - 에러가 발생한 이유:
	- 토큰를 파싱할때 Bearer 를 substring 을 안해서 발생했던 에러
	- 토큰을 파싱할때 토큰 정보를 저장할 클래스의 필드와 토큰 정보를 서로 매핑이 안된 경우에 발생
	<hr>
   
  - POSTMAN 401Unauthorized 에러
    - 에러가 발생한 이유:
		- 요청하는 URI가 컨트롤러에 있는 URI 와 일치하지 않아 발생한 에러
   		-  security Configuration 관련 파일의 requestMatchers에 요청 URI가 없어서 발생한 에러 
<hr>

 - @MockBean 으로 등록한 메소드를 사용하려면 반드 메소드에 맞는 리턴값을 설정한다  

``` java.lang.NullPointerException: Cannot invoke "com.zerobase.token.domain.UserVo.getEmail()" because the return value of "com.zerobase.token.config.JwtAuthProvider.getUserVo(String)" is null ```

	잘못된 사례

	given(provider.getUserVo(anyString()).getEmail())  
       		 .willReturn("abc@gmail.com");   

	모범 사례

 	given(provider.getUserVo(anyString()))
        	.willReturn(new UserVo(1L, "abc", "abc@gmail.com", "USER"));	
<hr>


- @MockBean 으로 등록한 컴포넌트안 메소드를 사용하려면 given() 안에 들어가는 메소드의 매개변수를 any(), anyString(), anyLong()과 같은 매개변수로 전달한다 

``` jakarta.servlet.ServletException: Request processing failed: java.lang.NullPointerException: Cannot invoke "com.zerobase.token.domain.UserVo.getEmail()" because the return value of "com.zerobase.token.config.JwtAuthProvider.getUserVo(String)" is null ```

	잘못된 사례

	given(provider.getUserVo(token))
     	   .willReturn(new UserVo(1L, "abc", "abc@gmail.com", "USER"));

	모범 사례

	 given(provider.getUserVo(anyString()))
     	   .willReturn(new UserVo(1L, "abc", "abc@gmail.com", "USER"));
	
 
# ERD :heavy_check_mark:


![fdb](https://github.com/karpei-taemukan/Fintech/assets/91212680/9b67c8c5-e274-4e2d-9fd9-0b3ef99fcfaa)




# Tech Stack :heavy_check_mark:
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-green)
![Gradle](https://img.shields.io/badge/Gradle-8.7-yellow)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Spring Security](https://img.shields.io/badge/Spring%20Security-orange)
![Git](https://img.shields.io/badge/Git-lightgrey)
![Mailgun](https://img.shields.io/badge/Mailgun-red)


