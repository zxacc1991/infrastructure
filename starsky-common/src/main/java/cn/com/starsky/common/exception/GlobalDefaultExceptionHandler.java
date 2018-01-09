package cn.com.starsky.common.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import cn.com.starsky.common.api.ResponseError;


/**   
 * @ClassName: GlobalDefaultExceptionHandler
 * @Description: 全局异常捕捉类
 * @author: Gavin  
 * @date: 2018年1月9日 下午11:25:51  
 * @version 
 */
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

	protected Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

	@ExceptionHandler(AuthTokenException.class)
	public ResponseEntity<?> handleAuthTokenException(HttpServletRequest req, Exception ex) {
		logger.warn("Request: " + req.getRequestURL() + " Error! " + "\n" + requestToString(req) + ex);
		AuthTokenException authTokenException = (AuthTokenException) ex;
		return ResponseError.create(authTokenException.getCode(), authTokenException.getMessage());
	}

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<?> apiException(HttpServletRequest req, Exception ex) {
		logger.error("Request: " + req.getRequestURL() + " Error! " + "\n" + requestToString(req), ex);
		ApiException apiException = (ApiException) ex;
		return ResponseError.create(apiException.getCode(), apiException.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleAllException(HttpServletRequest req, Exception ex) {
		String debug = req.getHeader("X-api-debug");
		String rStr = requestToString(req);
		logger.error("Request: " + req.getRequestURL() + " Error! " + "\n" + rStr, ex);
		if (StringUtils.isEmpty(debug)) {
			return ResponseError.create(400, "服务异常");
		} else {
			String exStr = exceptionToString(ex);
			return ResponseError.create(400, rStr + "\n" + exStr);
		}
	}

	private String requestToString(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		sb.append(">>> URL: ").append(req.getRequestURL()).append("\n");
		sb.append(">>> URI: ").append(req.getRequestURI()).append("\n");
		sb.append(">>> Method: ").append(req.getMethod()).append("\n");
		sb.append(">>> Header: ").append("\n");
		Enumeration<String> names = req.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String value = req.getHeader(name);
			sb.append(">>> ").append(name).append(": ").append(value).append("\n");
		}
		return sb.toString();
	}

	private String exceptionToString(Exception ex) {
		StringWriter sWriter = new StringWriter();
		ex.printStackTrace(new PrintWriter(sWriter));
		return sWriter.toString();
	}

}
