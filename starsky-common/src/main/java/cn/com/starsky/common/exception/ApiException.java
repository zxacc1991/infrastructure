package cn.com.starsky.common.exception;

public class ApiException extends RuntimeException {

	public static ApiException create(int code) {
		return new ApiException(code);
	}

	public static ApiException create(int code, String message) {
		return new ApiException(code, message);
	}

	public static ApiException create(int code, String message, Exception rootCause) {
		return new ApiException(code, message, rootCause);
	}

	private static final long serialVersionUID = 1L;

	private final int code;

	private ApiException(int code) {
		this.code = code;
	}

	private ApiException(int code, String message) {
		super(message);
		this.code = code;
	}

	private ApiException(int code, String message, Exception rootCause) {
		super(message, rootCause);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
