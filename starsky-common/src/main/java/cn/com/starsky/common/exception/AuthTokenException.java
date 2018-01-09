package cn.com.starsky.common.exception;

public class AuthTokenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public static final int TOKEN_OK = 200;

	/**
	 * @Title: ok 
	 * @Description: 票据正常 200
	 * @Author: Gavin
	 * @Create Date: 2018年1月9日下午11:24:04
	 * @return
	 */
	public static AuthTokenException ok() {
		return new AuthTokenException(TOKEN_OK);
	}

	/**
	 * @Title: nologin 
	 * @Description: 用户未登录 4010
	 * @Author: Gavin
	 * @Create Date: 2018年1月9日下午11:24:17
	 * @return
	 */
	public static AuthTokenException nologin() {
		return new AuthTokenException(4010, "用户未登录");
	}

	/**
	 * @Title: dataIllegal 
	 * @Description: 票据值错误，如CUST_NO为空 4011
	 * @Author: Gavin
	 * @Create Date: 2018年1月9日下午11:24:24
	 * @param message
	 * @return
	 */
	public static AuthTokenException dataIllegal(String message) {
		return new AuthTokenException(4011, message);
	}

	/**
	 * @Title: missing 
	 * @Description: 票据为空 4012
	 * @Author: Gavin
	 * @Create Date: 2018年1月9日下午11:24:32
	 * @return
	 */
	public static AuthTokenException missing() {
		return new AuthTokenException(4012, "票据为空");
	}

	/**
	 * @Title: invalid 
	 * @Description: 票据解析错误 4013
	 * @Author: Gavin
	 * @Create Date: 2018年1月9日下午11:24:41
	 * @param rootCause
	 * @return
	 */
	public static AuthTokenException invalid(Exception rootCause) {
		return new AuthTokenException(4013, "票据解析错误", rootCause);
	}

	/**
	 * @Title: expired 
	 * @Description: 票据已过期 4014
	 * @Author: Gavin
	 * @Create Date: 2018年1月9日下午11:24:49
	 * @return
	 */
	public static AuthTokenException expired() {
		return new AuthTokenException(4014, "票据已过期");
	}

	public static Exception expired(Exception ex) {
		return new AuthTokenException(4014, "票据已过期", ex);
	}

	private final int code;

	public AuthTokenException(int code) {
		this.code = code;
	}

	public AuthTokenException(int code, String message) {
		super(message);
		this.code = code;
	}

	public AuthTokenException(int code, String message, Exception rootCause) {
		super(message, rootCause);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
