package cn.com.starsky.common.log;

import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class RedisLogFilter extends Filter<ILoggingEvent> {

	private String markerName = "redis";
	
	@Override
	public FilterReply decide(ILoggingEvent event) {
		Marker marker = event.getMarker();
		if (marker != null && markerName.equals(marker.getName())) {
			return FilterReply.ACCEPT;
		} else {
			return FilterReply.DENY;
		}
	}

}
