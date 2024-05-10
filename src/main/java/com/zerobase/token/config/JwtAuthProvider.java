package com.zerobase.token.config;

import com.zerobase.service.VerifyService;
import com.zerobase.token.domain.UserVo;
import com.zerobase.token.util.Aes256Util;
import com.zerobase.token.domain.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthProvider {

  private static final Long TOKEN_VALID_TIME = 1000L * 60 * 60 * 24;
  private final VerifyService verifyService;
  @Value("${spring.jwt.secret}")
  private String secretKey;

  public String createToken(String name, Long id, UserType userType, String email) {
    Claims claims = Jwts.claims()
        .setSubject(Aes256Util.encrypt(name))
        .setAudience(Aes256Util.encrypt(email))
        .setId(Aes256Util.encrypt(id.toString()));

    claims.put("role", userType);

    System.out.println("ROLE   " + claims.get("role"));
    System.out.println("NAME " + Aes256Util.decrypt(claims.getSubject()));
    System.out.println("EMAIL " + Aes256Util.decrypt(claims.getAudience()));
    System.out.println("ID " + Aes256Util.decrypt(claims.getId()));
    Date now = new Date();

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + TOKEN_VALID_TIME))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }


  // JWS 를 이용해 JWT 토큰의 유효성 확인
  public boolean validateToken(String jwtToken) {
    try {
      System.out.println("SECRET " + secretKey);
      Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
      return !claimsJws.getBody().getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  // 토큰에 설정해둔 정보를 꺼낼 수 있도록한 메소드
  public UserVo getUserVo(String token) {
    Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

    System.out.println("getUserVo Claims: " + claims.get("role"));

    return new UserVo(
        Long.valueOf(Objects.requireNonNull(Aes256Util.decrypt(claims.getId()))),
        Aes256Util.decrypt(claims.getSubject()),
        Aes256Util.decrypt(claims.getAudience()),
        String.valueOf(claims.get("role"))
    );
  }


  // 토큰 파싱
  private Claims parseClaims(String token) {
    try {
      return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }


  // 토큰을 파싱해서 토큰안 이름 정보 가져오기
  public String getUsername(String token) {
    return this.parseClaims(token).getSubject();
  }

  public Authentication getUserAuthentication(String jwt) {
    UserDetails userDetails = verifyService.loadUserByUsername(getUsername(jwt));

    return new UsernamePasswordAuthenticationToken(
        userDetails,
        "",
        userDetails.getAuthorities()
    );
  }
}
