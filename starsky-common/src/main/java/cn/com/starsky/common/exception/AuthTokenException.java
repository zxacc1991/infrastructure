package cn.com.starsky.common.exception;

public class AuthTokenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public static final int TOKEN_OK = 200;

	/**
	 * @Title: ok 
	 * @Description: Ʊ������ 200
	 * @Author: Gavin
	 * @Create Date: 2018��1��9������11:24:04
	 * @return
	 */
	public static AuthTokenException ok() {
		return new AuthTokenException(TOKEN_OK);
	}

	/**
	 * @Title: nologin 
	 * @Description: �û�δ��¼ 4010
	 * @Author: Gavin
	 * @Create Date: 2018��1��9������11:24:17
	 * @return
	 */
	public static AuthTokenException nologin() {
		return new AuthTokenException(4010, "�û�δ��¼");
	}

	/**
	 * @Title: dataIllegal 
	 * @Description: Ʊ��ֵ������CUST_NOΪ�� 4011
	 * @Author: Gavin
	 * @Create Date: 2018��1��9������11:24:24
	 * @param message
	 * @return
	 */
	public static AuthTokenException dataIllegal(String message) {
		return new AuthTokenException(4011, message);
	}

	/**
	 * @Title: missing 
	 * @Description: Ʊ��Ϊ�� 4012
	 * @Author: Gavin
	 * @Create Date: 2018��1��9������11:24:32
	 * @return
	 */
	public static AuthTokenException missing() {
		return new AuthTokenException(4012, "Ʊ��Ϊ��");
	}

	/**
	 * @Title: invalid 
	 * @Description: Ʊ�ݽ������� 4013
	 * @Author: Gavin
	 * @Create Date: 2018��1��9������11:24:41
	 * @param rootCause
	 * @return
	 */
	public static AuthTokenException invalid(Exception rootCause) {
		return new AuthTokenException(4013, "Ʊ�ݽ�������", rootCause);
	}

	/**
	 * @Title: expired 
	 * @Description: Ʊ���ѹ��� 4014
	 * @Author: Gavin
	 * @Create Date: 2018��1��9������11:24:49
	 * @return
	 */
	public static AuthTokenException expired() {
		return new AuthTokenException(4014, "Ʊ���ѹ���");
	}

	public static Exception expired(Exception ex) {
		return new AuthTokenException(4014, "Ʊ���ѹ���", ex);
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
