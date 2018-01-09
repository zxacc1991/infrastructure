package cn.com.starsky.common.token;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import nl.basjes.parse.useragent.UserAgentAnalyzer;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Administrator
 *
 */
public enum TokenKey {
	/**
	 * 出生年月
	 */
	BIRTHDAY,
	CUST_GRADE,
	/**
	 * 用户等级编号
	 */
	CUST_LEV,
	/**
	 * 用户等级编号
	 */
	CUST_LEVEL_ID,
	/**
	 * 用户等级名称
	 */
	CUST_LEVEL_NAME,
	/**
	 * 客户姓名
	 */
	CUST_NAME,
	/**
	 * 客户编号
	 */
	CUST_NO,
	/**
	 * 促销登记编号
	 */
	/**
	 * 设备号
	 */
	DEVICE_ID,
	/**
	 * 访客号
	 */
	VISITOR_ID,
	
	/**
	 * 邮箱 Tcustomer
	 */
	EMAIL_ADDR,
	/**
	 * MAC地址
	 */
	MAC,
	/**
	 * 与msale_code一样 媒体渠道,TWCART中
	 */
	MEDIA_CHANNEL,
	/**
	 * 手机（第三方登录表TCUSTWEIBO） 只有一处用到可以替换成mobile
	 */
	PHONE,
	/**
	 * 身份证
	 */
	RESIDENT_NO,

	/**
	 *
	 * 用于极光推送的registrationId
	 *
	 */
	JIGUANG_ID;

	private Object defaultValue = null;

	/**
	 * <br>
	 * 1 只从token中获取，默认值 <br>
	 * 2 从token和request中获取<br>
	 * <br>
	 */
	private int scope = 1;

	private static UserAgentAnalyzer userAgentAnalyzer = new UserAgentAnalyzer();

	public static String init() {
		return userAgentAnalyzer.toString();
	}

	private TokenKey() {
	}

	private TokenKey(int scope) {
		this.scope = scope;
	}

	private TokenKey(int scope, Object defaultValue) {
		this.scope = scope;
		this.defaultValue = defaultValue;
	}

	public int getScope() {
		return scope;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	@Override
	public java.lang.String toString() {
		return super.toString().toLowerCase();
	}

	/*private UserAgent getUserAgent(HttpServletRequest request) {
		return userAgentAnalyzer.parse(request.getHeader("User-Agent"));
	}*/

	/*private String getMsaleCode(UserAgent userAgent) {
		String osc = userAgent.getValue(UserAgent.OPERATING_SYSTEM_CLASS);
		if (DeviceClass.Desktop.getValue().equals(osc)) {
			return "51";
		} else if (DeviceClass.Mobile.getValue().equals(osc)) {
			return "TM";
		} else if (DeviceClass.Tablet.getValue().equals(osc)) {
			return "PA";
		}
		// TODO 暂时，不够精确
		return "TM";
	}*/

	/*private String getMsaleWay(UserAgent userAgent) {
		String osn = userAgent.getValue(UserAgent.OPERATING_SYSTEM_NAME);
		if (StringUtils.equalsIgnoreCase(osn, "Android")) {
			return "ADR";
		} else if (StringUtils.equalsIgnoreCase(osn, "iOS") || StringUtils.equalsIgnoreCase(osn, "iPhone")
				|| StringUtils.equalsIgnoreCase(osn, "iPad")) {
			return "IOS";
		}
		// TODO 暂时，不够精确
		return "ADR";
	}*/

	private String stringFromRequest(TokenKey tokenKey, HttpServletRequest request) {
		String value = trimToNull(request.getHeader("X" + "-" + tokenKey.toString().replace("_", "-")));
		if (isNotEmpty(value)) {
			return value;
		}
		value = trimToNull(request.getHeader("x" + "-" + tokenKey.toString().replace("_", "-")));
		if (isNotEmpty(value)) {
			return value;
		}
		value = trimToNull(request.getParameter(this.toString()));
		if (isNotEmpty(value)) {
			return value;
		}
		//可以替换为其他的key值判断，changed by Gavin 2018-1-9
		/*if (tokenKey == MSALE_WAY) {
			UserAgent userAgent = getUserAgent(request);
			return getMsaleWay(userAgent);
		} else if (tokenKey == MSALE_CODE) {
			UserAgent userAgent = getUserAgent(request);
			return getMsaleCode(userAgent);
		} else {
			return value;
		}*/
		return value;
	}

	private String stringFromRequest(TokenKey tokenKey, HttpServletRequest request, Map<String, Object> params) {
		String value = stringFromRequest(tokenKey, request);
		if (isNotEmpty(value)) {
			return value;
		}
		Object oValue = params.get(tokenKey.toString());
		if (oValue == null) {
			return null;
		} else {
			return StringUtils.trimToNull(oValue.toString());
		}
	}

	public Optional<String> getStringFromRequest(HttpServletRequest request) {
		return Optional.ofNullable(stringFromRequest(this, request));
	}

	public Optional<String> getStringFromRequest(HttpServletRequest request, Map<String, Object> params) {
		return Optional.ofNullable(stringFromRequest(this, request, params));
	}
	
	/*public Optional<String> getStringFromRequest(HttpServletRequest request) {
		if (this == C_CODE || this == REGION_CD) {
			String value = stringFromRequest(C_CODE, request);
			if (value == null) {
				value = stringFromRequest(REGION_CD, request);
			}
			return Optional.ofNullable(value);
		} else if (this == SUB_CD || this == SUB_CODE || this == SUBSTATION_CODE) {
			String value = stringFromRequest(SUB_CD, request);
			if (value == null) {
				value = stringFromRequest(SUB_CODE, request);
			}
			if (value == null) {
				value = stringFromRequest(SUBSTATION_CODE, request);
			}
			return Optional.ofNullable(value);
		} else {
			return Optional.ofNullable(stringFromRequest(this, request));
		}

	}*/
	
	/*public Optional<String> getStringFromRequest(HttpServletRequest request, Map<String, Object> params) {
		if (this == C_CODE || this == REGION_CD) {
			String value = stringFromRequest(C_CODE, request, params);
			if (value == null) {
				value = stringFromRequest(REGION_CD, request, params);
			}
			return Optional.ofNullable(value);
		} else if (this == SUB_CD || this == SUB_CODE || this == SUBSTATION_CODE) {
			String value = stringFromRequest(SUB_CD, request, params);
			if (value == null) {
				value = stringFromRequest(SUB_CODE, request, params);
			}
			if (value == null) {
				value = stringFromRequest(SUBSTATION_CODE, request, params);
			}
			return Optional.ofNullable(value);
		} else {
			return Optional.ofNullable(stringFromRequest(this, request, params));
		}
	}*/

}