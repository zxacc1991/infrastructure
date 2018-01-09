package cn.com.starsky.common.log;

import java.util.Arrays;

import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class RedisAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

	String redisKey = null;

	int timeout = 500;

	String password = null;

	String redisUrl;

	StringRedisTemplate redisTemplate;

	JedisConnectionFactory factory;

	public RedisAppender() {
	}

	@Override
	protected void append(ILoggingEvent event) {
		try {
			String json = event.getMessage();
			redisTemplate.opsForList().leftPush(redisKey, json);
			redisTemplate.opsForList().trim(redisKey, 0, 9999);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setRedisUrl(String redisUrl) {
		this.redisUrl = redisUrl;
	}

	public void setRedisKey(String redisKey) {
		this.redisKey = redisKey;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void start() {
		if (redisKey == null || redisKey.isEmpty()) {
			throw new RuntimeException("redisKey is null");
		}
		if (redisUrl == null || redisUrl.isEmpty()) {
			throw new RuntimeException("redisUrl is null");
		}
		super.start();
		String[] hostPorts = redisUrl.split(",");
		factory = new JedisConnectionFactory(new RedisClusterConfiguration(Arrays.asList(hostPorts)));
		if (timeout > 0) {
			factory.setTimeout(timeout);
		}
		if (password != null && !password.isEmpty()) {
			factory.setPassword(password);
		}
		factory.afterPropertiesSet();
		redisTemplate = new StringRedisTemplate(factory);
	}

	@Override
	public void stop() {
		super.stop();
		factory.destroy();
	}
}
