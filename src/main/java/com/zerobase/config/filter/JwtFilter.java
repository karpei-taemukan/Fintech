package com.zerobase.config.filter;

import com.zerobase.token.config.JwtAuthProvider;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@NonNullApi
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

  public static final String TOKEN_HEADER = "Authorization";

  public static final String TOKEN_PREFIX = "Bearer ";

  private final JwtAuthProvider provider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    String token = getTokenFromRequest(request);

    if (StringUtils.hasText(token) && provider.validateToken(token) && provider.getUserVo(token)
        .getUserType().equals("USER")) {
      Authentication authentication = provider.getUserAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      log.info(String.format("[%s] -> %s", provider.getUsername(token), request.getRequestURI()));
    }

    filterChain.doFilter(request, response);
  }

  private String getTokenFromRequest(HttpServletRequest request) {
    String token = request.getHeader(TOKEN_HEADER);

    if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
      log.info(String.format("[%s] -> %s", "BEFORE TOKEN", token));
      log.info(String.format("[%s] -> %s", "AFTER TOKEN", token.substring(TOKEN_PREFIX.length())));
      return token.substring(TOKEN_PREFIX.length());
    }
    return null;
  }
}