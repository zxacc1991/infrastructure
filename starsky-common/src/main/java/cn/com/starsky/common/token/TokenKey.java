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
	 * ��������
	 */
	BIRTHDAY,
	CUST_GRADE,
	/**
	 * �û��ȼ����
	 */
	CUST_LEV,
	/**
	 * �û��ȼ����
	 */
	CUST_LEVEL_ID,
	/**
	 * �û��ȼ�����
	 */
	CUST_LEVEL_NAME,
	/**
	 * �ͻ�����
	 */
	CUST_NAME,
	/**
	 * �ͻ����
	 */
	CUST_NO,
	/**
	 * �����ǼǱ��
	 */
	/**
	 * �豸��
	 */
	DEVICE_ID,
	/**
	 * �ÿͺ�
	 */
	VISITOR_ID,
	
	/**
	 * ���� Tcustomer
	 */
	EMAIL_ADDR,
	/**
	 * MAC��ַ
	 */
	MAC,
	/**
	 * ��msale_codeһ�� ý������,TWCART��
	 */
	MEDIA_CHANNEL,
	/**
	 * �ֻ�����������¼��TCUSTWEIBO�� ֻ��һ���õ������滻��mobile
	 */
	PHONE,
	/**
	 * ���֤
	 */
	RESIDENT_NO,

	/**
	 *
	 * ���ڼ������͵�registrationId
	 *
	 */
	JIGUANG_ID;

	private Object defaultValue = null;

	/**
	 * <br>
	 * 1 ֻ��token�л�ȡ��Ĭ��ֵ <br>
	 * 2 ��token��request�л�ȡ<br>
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
		// TODO ��ʱ��������ȷ
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
		// TODO ��ʱ��������ȷ
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
		//�����滻Ϊ������keyֵ�жϣ�changed by Gavin 2018-1-9
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