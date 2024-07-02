/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.security.jwt;

import com.nx.auth.conf.AuthConfig;
import com.nx.auth.context.AuthCache;
import com.nx.auth.context.UserFacade;
import com.nx.cache.annotation.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
@ConditionalOnMissingBean(JwtTokenHandler.class)
public class JwtTokenHandler implements Serializable {

	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_CREATED = "iat";
	private static final long serialVersionUID = -3301605591108950415L;
	private Clock clock = DefaultClock.INSTANCE;

	// 加密key
	private final String secret;

	// 过期时间，默认为7天 604800秒
	private final Long expiration;


	public JwtTokenHandler(@Autowired AuthConfig authConfig){
		this.secret = authConfig.getJwt().getSecret();
		this.expiration = authConfig.getJwt().getExpiration();
	}

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getIssuedAtDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token)
				.getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(clock.now());
	}

	private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
		return (lastPasswordReset != null && created.before(lastPasswordReset));
	}

	private Boolean ignoreTokenExpiration(String token) {
		// here you specify tokens, for that the expiration is ignored
		return false;
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		String tenantId = "";
		if(userDetails instanceof UserFacade) {
			UserFacade iUser = (UserFacade) userDetails;
			tenantId = iUser.getTenantId();
		}
		claims.put("tenantId", tenantId);
		return doGenerateToken(claims, userDetails.getUsername());
	}

	private String doGenerateToken(Map<String, Object> claims, String subject) {
		final Date createdDate = clock.now();
		final Date expirationDate = calculateExpirationDate(createdDate);

		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(createdDate)
				.setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
		final Date created = getIssuedAtDateFromToken(token);
		return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
				&& (!isTokenExpired(token) || ignoreTokenExpiration(token));
	}

	public String refreshToken(String token) {
		final Date createdDate = clock.now();
		final Date expirationDate = calculateExpirationDate(createdDate);

		final Claims claims = getAllClaimsFromToken(token);
		claims.setIssuedAt(createdDate);
		claims.setExpiration(expirationDate);

		return Jwts.builder()
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	@SuppressWarnings("unused")
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		final Date created = getIssuedAtDateFromToken(token);
		return (username.equals(userDetails.getUsername())
				&& !isTokenExpired(token));
	}

	private Date calculateExpirationDate(Date createdDate) {
		return new Date(createdDate.getTime() + expiration * 1000);
	}

	public String getTenantIdFromToken(String authToken) {
		Claims allClaimsFromToken = getAllClaimsFromToken(authToken);
		String tenantId = allClaimsFromToken.get("tenantId", String.class);
		return tenantId;
	}



	/**
	 * 获取缓存中的token
	 * @param userAgent
	 * @param tenantId
	 * @param account
	 * @param expireTime
	 * @return
	 */
	@Cacheable(cacheName= AuthCache.AUTH_CACHE_NAME, keyPrefix = "singlelogin", key = "#userAgent+'_'+#tenantId+'_'+#account", ignoreException = false,
			firstCache = @FirstCache(expireTime = 1800, expireTimeExp = "#expireTime", timeUnit = TimeUnit.SECONDS),
			secondaryCache = @SecondaryCache(expireTime = 1800, expireTimeExp = "#expireTime", timeUnit = TimeUnit.SECONDS))
	public String getTokenFromCache(String userAgent, String tenantId, String account, int expireTime) {
		return null;
	}

	/**
	 * 将token缓存起来
	 * @param userAgent
	 * @param tenantId
	 * @param account
	 * @param expireTime
	 * @param token
	 * @return
	 */
	@CachePut(cacheName= AuthCache.AUTH_CACHE_NAME, keyPrefix = "singlelogin", key = "#userAgent+'_'+#tenantId+'_'+#account", ignoreException = false,
			firstCache = @FirstCache(expireTime = 1800, expireTimeExp = "#expireTime", timeUnit = TimeUnit.SECONDS),
			secondaryCache = @SecondaryCache(expireTime = 1800, expireTimeExp = "#expireTime",  timeUnit = TimeUnit.SECONDS))
	public String putTokenInCache(String userAgent, String tenantId, String account, long expireTime, String token) {
		return token;
	}

	/**
	 * 删除token
	 * <p>可实现服务端踢用户下线</p>
	 * @param userAgent
	 * @param tenantId
	 * @param account
	 */
	@CacheEvict(cacheName= AuthCache.AUTH_CACHE_NAME, keyPrefix = "singlelogin", key = "#userAgent+'_'+#tenantId+'_'+#account", ignoreException = false)
	public void removeFromCache(String userAgent, String tenantId, String account) {
	}
}
