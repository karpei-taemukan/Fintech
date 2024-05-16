package com.zerobase.controller.util;

import com.zerobase.dto.TokenDto;
import com.zerobase.token.util.Aes256Util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class TokenArgumentResolver implements HandlerMethodArgumentResolver {

  public static final String TOKEN_PREFIX = "Bearer ";
  @Value("${spring.jwt.secret}")
  private String secretKey;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(TokenDto.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();

    String token = httpServletRequest.getHeader("Authorization");

    token = token.substring(TOKEN_PREFIX.length());

    Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

    String email = Aes256Util.decrypt(claims.getSubject());
    String name = Aes256Util.decrypt(claims.getAudience());

    return new TokenDto(name, email);
  }
}
