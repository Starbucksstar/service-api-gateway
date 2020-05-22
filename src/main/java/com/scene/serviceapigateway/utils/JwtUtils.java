package com.scene.serviceapigateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Slf4j
@Component
public class JwtUtils {
    private static final String ISSUER = "VR";
    private static final String SUBJECT = "ACCOUNT_JWT";
    private static final String BEARER = "Bearer ";
    public static String SECRET;

    @Value("${jwt.token.secret:default}")
    public void setSecret(String secret) {
        SECRET = secret;
    }

    public static Long validateJwtToken(String jwtToken) throws UnsupportedEncodingException {
        if (StringUtils.isNotBlank(jwtToken)) {
            String token = StringUtils.substringAfter(jwtToken, BEARER);
            final Claims body = Jwts.parser()
                                    .requireIssuer(ISSUER)
                                    .requireSubject(SUBJECT)
                                    .setSigningKey(SECRET.getBytes("utf-8"))
                                    .parseClaimsJws(token)
                                    .getBody();
            return body.get("id", Long.class);
        }
        throw new JwtException("Missing JwtToken");
    }

    public static Header getJwtTokenHeader(String jwtToken) throws UnsupportedEncodingException {
        if (StringUtils.isNotBlank(jwtToken)) {
            return Jwts.parser()
                       .requireIssuer(ISSUER)
                       .requireSubject(SUBJECT)
                       .setSigningKey(SECRET.getBytes("utf-8"))
                       .parseClaimsJws(jwtToken)
                       .getHeader();
        }
        throw new JwtException("Missing JwtToken");
    }

    public static Claims getJwtTokenClaims(String jwtToken) throws UnsupportedEncodingException {
        if (StringUtils.isNotBlank(jwtToken)) {
            return Jwts.parser()
                       .requireIssuer(ISSUER)
                       .requireSubject(SUBJECT)
                       .setSigningKey(SECRET.getBytes("utf-8"))
                       .parseClaimsJws(jwtToken)
                       .getBody();
        }
        throw new JwtException("Missing JwtToken");
    }

}
