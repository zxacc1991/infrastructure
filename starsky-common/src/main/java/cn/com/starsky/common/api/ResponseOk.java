package cn.com.starsky.common.api;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

/**   
 * @ClassName: ResponseOk
 * @Description: 成功信息返回类
 * @author: Gavin  
 * @date: 2018年1月9日 下午11:17:02  
 * @version 
 */
public class ResponseOk {

	public static ResponseEntity<?> create(Object result) {
		ResponseResult okResponse = new ResponseResult();
		okResponse.setCode(200);
		okResponse.setMessage("succeed");
		okResponse.setData(result);
		return ResponseEntity.status(HttpStatus.OK).body(okResponse);
	}

	public static ResponseEntity<?> create(String key, String result) {
		Map<String, Object> map = Maps.newHashMap();
		map.put(key, result);
		return create(map);
	}

	public static ResponseEntity<?> create(String key, Integer result) {
		Map<String, Object> map = Maps.newHashMap();
		map.put(key, result);
		return create(map);
	}

	public static ResponseEntity<?> create(String key, Long result) {
		Map<String, Object> map = Maps.newHashMap();
		map.put(key, result);
		return create(map);
	}

	public static ResponseEntity<?> createCacheTTL(Object result, long ttl) {
		ResponseResult okResponse = new ResponseResult();
		okResponse.setCode(200);
		okResponse.setMessage("succeed");
		okResponse.setData(result);
		return ResponseEntity.status(HttpStatus.OK).header("X-Cache-TTL", String.valueOf(ttl)).body(okResponse);
	}

    public static class ResponseResult {

		@JsonProperty("code")
		private int code;

		@JsonProperty("message")
		private String message;

		@JsonProperty("data")
		private Object data;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}

	}
	public static class ResponseResult2 {

		@JsonProperty("code")
		private String code;

		@JsonProperty("message")
		private String message;

		@JsonProperty("data")
		private Object data;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}
	}
}
