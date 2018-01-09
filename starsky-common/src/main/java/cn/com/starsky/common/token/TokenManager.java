package cn.com.starsky.common.token;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import cn.com.starsky.common.exception.AuthTokenException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

/**
 * 放值：到token中，必须严格检查tokne的合法性。
 * 取值：接口可以允许游客访问，所以允许token为空，如果为空，返回空值。但接口的token必须合法，否则抛出异常。
 * 
 * @author Administrator
 *
 */
@Component
public class TokenManager implements InitializingBean {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String ISSUER = "ocj-starsky";

	private String SECRET = "6IkpXUyJ9.eyJpc3M";

	// 31天
	private long REDIS_TTL_DAYS = 31;

	// 90天
	private long TOKEN_TTL_MILLIS = TimeUnit.DAYS.toMillis(90);

	@Autowired
	private RedisTemplate<String, ?> redisTemplate;

	private static final String VISITOR = "!visitor!";

	public void setRedisTemplate(RedisTemplate<String, ?> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void afterPropertiesSet() throws Exception {
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
		redisTemplate.afterPropertiesSet();
	}

	/**
	 * creat a JWT token
	 *
	 * @param custNo
	 * @param deviceId
	 * @return
	 */
	public String generateAccessToken(TokenClaim tokenClaim) {
		// String custNo, String deviceId, long logId
		try {
			long nowMillis = System.currentTimeMillis();
			Date now = new Date(nowMillis);
			Date exp = new Date(nowMillis + TOKEN_TTL_MILLIS);
			Algorithm algorithm = Algorithm.HMAC256(SECRET);
			return JWT.create().withIssuer(ISSUER).withIssuedAt(now).withExpiresAt(exp)
					.withSubject(tokenClaim.getCustNo()).withClaim(TokenClaim.DEVICE_ID, tokenClaim.getDeviceId())
					.withClaim(TokenClaim.LOG_ID, String.valueOf(tokenClaim.getLogId())).sign(algorithm);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public String generateVistorToken() {
		String subject = VISITOR + "-" + UUID.randomUUID().toString();
		try {
			long nowMillis = System.currentTimeMillis();
			Date now = new Date(nowMillis);
			Date exp = new Date(nowMillis + TOKEN_TTL_MILLIS);
			Algorithm algorithm = Algorithm.HMAC256(SECRET);
			return JWT.create().withIssuer(ISSUER).withIssuedAt(now).withExpiresAt(exp).withSubject(subject)
					.sign(algorithm);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 更新KEY 原来的key
	 * 
	 * @param oldTokenKey
	 * @param newTokenKey
	 * @return
	 */
	public boolean refreshTonkey(String oldTokenKey, String newTokenKey) {
		if (redisTemplate.hasKey(oldTokenKey)) {
			redisTemplate.rename(oldTokenKey, newTokenKey);
			redisTemplate.expire(newTokenKey, REDIS_TTL_DAYS, TimeUnit.DAYS);
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 是否登录
	 * 
	 * @param request
	 * @return
	 */
	public void checkLogin(HttpServletRequest request) {
		String token = getToken(request);
		verifyTokenException(token);
		if (!isLogin(request)) {
			throw AuthTokenException.nologin();
		}
	}

	public boolean isLogin(HttpServletRequest request) {
		String token = getToken(request);
		if (!isVisitor(token) && StringUtils.isNotEmpty(getString(request, TokenKey.CUST_NO))) {
			return true;
		}
		return false;
	}

	public boolean isVisitor(HttpServletRequest request) {
		String token = getToken(request);
		return isVisitor(token);
	}

	private boolean isVisitor(String token) {
		if (StringUtils.isEmpty(token)) {
			return true;
		}
		DecodedJWT jwt = verifyTokenException(token);
		if (StringUtils.isEmpty(getString(token, TokenKey.CUST_NO))) {
			return true;
		}
		String subject = jwt.getSubject();
		return subject.startsWith(VISITOR);
	}

	public boolean isVisitorToken(String token) {
		if (StringUtils.isEmpty(token)) {
			return true;
		}
		DecodedJWT jwt = verifyTokenException(token);
		String subject = jwt.getSubject();
		return subject.startsWith(VISITOR);
	}

	/**
	 * 获取过期时间
	 * 
	 * @param token
	 *            <BR>
	 * @return expiresTime 过期时间 <BR>
	 *         isVisitor true login <BR>
	 *         isVisitor true login <BR>
	 */
	public Map<String, Object> getTokenInfo(String token) {
		Map<String, Object> tokenResult = Maps.newHashMap();
		DecodedJWT jwt = null;
		try {
			jwt = verifyTokenException(token);
			Date expiresAt = jwt.getExpiresAt();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			tokenResult.put("expiresAt", dateFormat.format(expiresAt));
			tokenResult.put("expiresTime",
					TimeUnit.MILLISECONDS.toMillis(expiresAt.getTime() - System.currentTimeMillis()));
			String subject = jwt.getSubject();
			boolean isVisitor = subject.startsWith(VISITOR);
			tokenResult.put("isVisitor", isVisitor);
			tokenResult.put("isLogin", !isVisitor && !StringUtils.isEmpty(getString(token, TokenKey.CUST_NO)));
			tokenResult.put("cust_no", getString(token, TokenKey.CUST_NO));
		} catch (Exception ex) {
			throw AuthTokenException.invalid(ex);
		}
		return tokenResult;
	}

	/**
	 * 获取ID
	 * 
	 * @param token
	 * @return
	 */
	public TokenClaim getTokenClaim(HttpServletRequest request) {
		TokenClaim tokenClaim = new TokenClaim();
		String token = getToken(request);
		if (StringUtils.isEmpty(token)) {
			return tokenClaim;
		}
		tokenClaim.setAccessToken(token);
		try {
			DecodedJWT jwt = verifyTokenException(token);
			tokenClaim.setCustNo(jwt.getSubject());
			Claim logId = jwt.getClaim(TokenClaim.LOG_ID);
			if (!logId.isNull()) {
				tokenClaim.setLogId(NumberUtils.toLong(logId.asString()));
			}
			Claim deviceId = jwt.getClaim(TokenClaim.DEVICE_ID);
			if (!deviceId.isNull()) {
				tokenClaim.setDeviceId(deviceId.asString());
			}
		} catch (Exception ex) {
			logger.warn("JWTVerifier: ", ex);
		}
		return tokenClaim;
	}

	/**
	 * 放值到token中，必须严格检查tokne的合法性。
	 * 
	 * @param request
	 * @param tokenKey
	 * @param tokenValue
	 */
	public void putObject(HttpServletRequest request, TokenKey tokenKey, Object tokenValue) {
		if (tokenKey == null) {
			throw new RuntimeException();
		}
		if (tokenValue == null) {
			throw new RuntimeException();
		}
		String token = getToken(request);
		putObject(token, tokenKey, tokenValue);
	}

	public void putObject(String token, TokenKey tokenKey, Object tokenValue) {
		if (tokenKey == null) {
			throw new RuntimeException();
		}
		if (tokenValue == null) {
			throw new RuntimeException();
		}
		verifyTokenException(token);
		redisTemplate.opsForHash().put(token, tokenKey.toString(), tokenValue);
		redisTemplate.expire(token, REDIS_TTL_DAYS, TimeUnit.DAYS);
	}

	public void putMap(HttpServletRequest request, Map<String, Object> map) {
		String token = getToken(request);
		putMap(token, map);
	}

	public void putMap(String token, Map<String, Object> map) {
		verifyTokenException(token);
		Map<String, Object> newMap = transformMap(map);
		redisTemplate.opsForHash().putAll(token, newMap);
		redisTemplate.expire(token, REDIS_TTL_DAYS, TimeUnit.DAYS);

	}

	public Object getObject(HttpServletRequest request, TokenKey tokenKey) {
		String token = getToken(request);
		return getObject(token, tokenKey);
	}

	/**
	 * 接口可以允许游客访问，所以允许token为空，如果为空，返回空值。但接口中的token必须合法，否则抛出异常。
	 * 
	 * @param token
	 * @param tokenKey
	 * @return
	 */
	public Object getObject(String token, TokenKey tokenKey) {
		if (tokenKey == null) {
			throw new RuntimeException();
		}
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		verifyTokenException(token);
		return redisTemplate.opsForHash().get(token, tokenKey.toString());
	}

	public String getString(HttpServletRequest request, TokenKey tokenKey) {
		Optional<String> op = getStrings(request, tokenKey);
		if (op.isPresent()) {
			return op.get();
		} else {
			return (String) tokenKey.getDefaultValue();
		}
	}

	public String getString(String token, TokenKey tokenKey) {
		String v = StringUtils.trimToNull((String) getObject(token, tokenKey));
		if (StringUtils.isEmpty(v)) {
			return (String) tokenKey.getDefaultValue();
		} else {
			return v;
		}
	}

	public Optional<String> getStrings(HttpServletRequest request, TokenKey tokenKey) {
		String o = (String) getObject(request, tokenKey);
		if (StringUtils.isEmpty(o) && tokenKey.getScope() == 2) {
			return tokenKey.getStringFromRequest(request);
		} else {
			return Optional.ofNullable(StringUtils.trimToNull(o));
		}
	}

	public Optional<Integer> getInt(HttpServletRequest request, TokenKey tokenKey) {
		Object o = getObject(request, tokenKey);
		return Optional.ofNullable((Integer) o);
	}

	public Optional<Long> getLong(HttpServletRequest request, TokenKey tokenKey) {
		Object o = getObject(request, tokenKey);
		return Optional.ofNullable((Long) o);
	}

	public Optional<Date> getDate(HttpServletRequest request, TokenKey tokenKey) {
		Object o = getObject(request, tokenKey);
		return Optional.ofNullable((Date) o);
	}

	public Optional<Date> getDate(String token, TokenKey tokenKey) {
		Object o = getObject(token, tokenKey);
		return Optional.ofNullable((Date) o);
	}

	/**
	 * 可以用lambda，暂时用这个方式
	 * 
	 * @param map
	 * @return
	 */
	private Map<String, Object> transformMap(Map<String, Object> map) {
		return Maps.filterEntries(map, new Predicate<Entry<String, Object>>() {
			@Override
			public boolean apply(Entry<String, Object> input) {
				if (input.getValue() == null) {
					return false;
				} else {
					return true;
				}
			}
		});
	}

	public void remove(HttpServletRequest request, TokenKey key) {
		String token = getToken(request);
		if (StringUtils.isNotEmpty(token)) {
			redisTemplate.opsForHash().delete(token, key.toString());
		}
	}

	/**
	 * 删除redis KEY
	 * 
	 * @param key
	 */
	public void removeTokenKey(String key) {
		if (StringUtils.isNotEmpty(key) && redisTemplate.hasKey(key)) {
			redisTemplate.delete(key);
		}
	}

	/**
	 * 这个方法会返回一个JWT。除非发来的额token是一个乱七八糟的的值，如abc123。
	 * 
	 * @param token
	 * @return
	 */
	protected JWT verifyTokenException(String token) {
		if (StringUtils.isEmpty(token)) {
			throw AuthTokenException.missing();
		}
		try {
			Algorithm algorithm = Algorithm.HMAC256(SECRET);
			JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
			verifier.verify(token);
		} catch (Exception ex) {
			if (StringUtils.contains(ex.getMessage(), "expired")) {
				// throw AuthTokenException.expired(ex);
			} else {
				throw AuthTokenException.invalid(ex);
			}
		}
		// Date expiresAt = jwt.getExpiresAt();
		// if (expiresAt.getTime() < System.currentTimeMillis()) {
		// throw AuthTokenException.expired();
		// }
		return JWT.decode(token);
	}

	/**
	 * 从request中获取token
	 *
	 * @param request
	 * @return
	 */
	public String getToken(HttpServletRequest request) {
		String token = getTokenFromHeader(request);
		if (token == null) {
			token = getTokenFromParameter(request);
		}
		return StringUtils.trimToEmpty(token);
	}

	/**
	 * 从requestParma 中获取token
	 *
	 * @param request
	 * @return
	 */
	private String getTokenFromParameter(HttpServletRequest request) {
		String token = request.getParameter("access_token");
		return StringUtils.trimToNull(token);
	}

	private String getTokenFromHeader(HttpServletRequest request) {
		String token = request.getHeader("X-access-token");
		return StringUtils.trimToNull(token);
	}

}
