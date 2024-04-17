package com.zerobase.token.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthProvider {
    private String secretKey;

    private static final Long TOKEN_VALID_TIME = 1000L * 60 * 60 * 24;


}
