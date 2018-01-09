package cn.com.starsky.common.token;

public class TokenClaim {

	public static final String LOG_ID = "logid";

	public static final String DEVICE_ID = "deviceid";

	private String accessToken;
	
	private String custNo;

	private String deviceId;

	private long logId;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public long getLogId() {
		return logId;
	}

	public void setLogId(long logId) {
		this.logId = logId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TokenClaim [custNo=");
		builder.append(custNo);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", logId=");
		builder.append(logId);
		builder.append("]");
		return builder.toString();
	}

}
