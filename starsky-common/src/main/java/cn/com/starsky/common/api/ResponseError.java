package cn.com.starsky.common.api;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**   
 * @ClassName: ResponseError
 * @Description: 错误信息返回类
 * @author: Gavin  
 * @date: 2018年1月9日 下午11:16:43  
 * @version 
 */
public class ResponseError {

	public static ResponseEntity<?> create(String msg) {
		ResponseResult error = new ResponseResult();
		error.setCode(400);
		error.setMessage(msg);
		return ResponseEntity.status(HttpStatus.OK).body(error);
	}

	public static ResponseEntity<?> create(int code, String msg) {
		ResponseResult error = new ResponseResult();
		error.setCode(code);
		error.setMessage(msg);
		return ResponseEntity.status(HttpStatus.OK).body(error);
	}

	public static ResponseEntity<?> create(int code, String msg,Object data) {
		ResponseResult error = new ResponseResult();
		error.setCode(code);
		error.setMessage(msg);
		error.put("data",data);
		return ResponseEntity.status(HttpStatus.OK).body(error);
	}

	public static class ResponseResult extends HashMap<String,Object> {

		private static final long serialVersionUID = 1L;

		public String getMessage() {
			return (String)this.get("message");
		}

		public void setMessage(String message) {
			this.put("message",message);
		}

		public int getCode() {
			return (Integer)this.get("code");
		}

		public void setCode(int code) {
			this.put("code",code);
		}

	}
}
