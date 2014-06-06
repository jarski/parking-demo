package com.vaadin.demo.phonegap.push;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

@JavaScript({"phonegap_push.js", "jquery_1.5.2.min.js"})
public class PhoneGapPushExtension extends AbstractJavaScriptExtension {

	public interface NotificationListener {
		public void notificationReceived(JSONObject notification);
	}
	
	private NotificationListener notificationListener;
	
	public PhoneGapPushExtension(UI ui, final NotificationListener notificationListener) {
		this.notificationListener = notificationListener;
		extend(ui);
		addFunction("registered", new JavaScriptFunction() {
			@Override
			public void call(JSONArray arguments) throws JSONException {
				String regId = arguments.getString(0);
				AndroidPushServer.addDevice(regId);
				Notification.show("Registered with id: "+regId, Type.ERROR_MESSAGE);
			}
		});
		addFunction("receivedNotification", new JavaScriptFunction() {
			@Override
			public void call(JSONArray arguments) throws JSONException {
				JSONObject notification = (JSONObject) arguments.get(0);
				notificationListener.notificationReceived(notification);
			}
		});
	}
	
	public void initialize() {
		callFunction("initialize");
	}
	
}
