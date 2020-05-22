package com.scene.serviceapigateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.scene.serviceapigateway.output.TokenErrorOutputDTO;
import com.scene.serviceapigateway.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends ZuulFilter {

    private static final String BEARER = "Bearer ";
    private final RedisTemplate<String, String> redisTemplate;
    private static final String LOGIN_URL = "/api/v1/account/login";
    @Value("${jwt.token.prefix}")
    private String TOKEN_PREFIX = "default";

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        if (LOGIN_URL.equals(request.getRequestURI())) {
            return false;
        }
        return true;
    }

    @Override
    public Object run() {
        final RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        try {
            final Long userId = JwtUtils.validateJwtToken(request.getHeader(HttpHeaders.AUTHORIZATION));
            String token = StringUtils.substringAfter(request.getHeader(HttpHeaders.AUTHORIZATION), BEARER);
            String key = TOKEN_PREFIX + userId;
            if (!redisTemplate.hasKey(key)) {
                throw new ExpiredJwtException(JwtUtils.getJwtTokenHeader(token), JwtUtils.getJwtTokenClaims(token), "token is not exist in redis");
            }
            if (!redisTemplate.opsForValue().get(key).equals(token)) {
                throw new ZuulException("JwtToken is illegal", HttpServletResponse.SC_UNAUTHORIZED, "JwtToken is illegal");
            }
        } catch (ExpiredJwtException e) {
            log.error("[JwtToken expired], error info={}", e.getMessage(), e);
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseStatusCode(HttpServletResponse.SC_FORBIDDEN);
            final TokenErrorOutputDTO tokenIsExpired = TokenErrorOutputDTO.builder().errorCode(HttpServletResponse.SC_FORBIDDEN).errorMsg("JwtToken is expired").build();
            currentContext.setResponseBody(JSONObject.toJSONString(tokenIsExpired));
        } catch (Exception e) {
            log.error("[JwtToken illegal], error info={}", e.getMessage(), e);
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
            final TokenErrorOutputDTO tokenIsInvalid = TokenErrorOutputDTO.builder().errorCode(HttpServletResponse.SC_UNAUTHORIZED).errorMsg("JwtToken is illegal").build();
            currentContext.setResponseBody(JSONObject.toJSONString(tokenIsInvalid));
        }
        return null;
    }
}
