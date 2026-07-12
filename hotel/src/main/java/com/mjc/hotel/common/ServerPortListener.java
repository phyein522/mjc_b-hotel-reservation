package com.mjc.hotel.common;

import org.springframework.boot.web.server.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ServerPortListener implements ApplicationListener<WebServerInitializedEvent> {
	public static int PORT;

	@Override
	public void onApplicationEvent(WebServerInitializedEvent event) {
		PORT = event.getWebServer().getPort();
	}
}
