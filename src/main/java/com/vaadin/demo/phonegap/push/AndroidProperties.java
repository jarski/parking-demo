package com.vaadin.demo.phonegap.push;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AndroidProperties {
	// The Project Number of Google API Project (Check https://cloud.google.com/console)
		private static String APIKey = null;
		private static String senderId = null;
		// Static block to load push properties
		static {
			Properties properties = new Properties();
			try {
				InputStream stream = AndroidPushServer.class.getResourceAsStream("push.properties");
				if(stream == null) {
					throw new RuntimeException("The properties file for push is missing");
				}
				properties.load(stream);
			} catch (IOException e) {
				throw new RuntimeException("Wasn't able to read the properties file for push", e);
			}
			APIKey = properties.getProperty("android.APIKey");
			senderId = properties.getProperty("android.senderId");
		}
		
		public static String getAPIKey() {
			return APIKey;
		}
		
		public static String getSenderId() {
			return senderId;
		}
}
