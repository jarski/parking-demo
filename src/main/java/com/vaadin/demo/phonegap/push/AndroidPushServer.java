package com.vaadin.demo.phonegap.push;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.InvalidRequestException;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.vaadin.demo.parking.model.ShiftSuggestion;

public class AndroidPushServer {
	
	static private Logger logger = Logger.getLogger(AndroidPushServer.class.getName());
	
	// The Project Number of Google API Project (Check https://cloud.google.com/console)
	static String senderId = null;
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
		senderId = properties.getProperty("android.senderId");
	}
	

	public static void pushNewShift(ShiftSuggestion bean) {
		if(noDestinations()) {
			return;
		}
		
		Sender sender = new Sender(senderId);
		Message msg = new Message.Builder()
				.addData("message", createMessage(bean))
				.addData("soundname", "beep.wav")
				.addData("title", "New shift available")
				.addData("id", String.valueOf(bean.getId()))
				.addData("area", bean.getArea())
				.addData("date", bean.getDate())
				.addData("start", bean.getStart().toString())
				.addData("end", bean.getEnd().toString())
				.delayWhileIdle(true) // 
				.collapseKey("newshift") // Only the last message with key is sent
				.timeToLive(2 * 60 * 60 ) // two hours
				.build();
		
		try {
			// Note there cannot be more than 1000 devices (GCM constraint)
			MulticastResult multicast = sender.send(msg, devices, 1);
			
			List<Result> results = multicast.getResults();
	        // check the results
	        for (int i = 0; i < devices.size(); i++) {
	          String regId = devices.get(i);
	          Result result = results.get(i);
	          String messageId = result.getMessageId();
	          if (messageId != null) {
	            logger.fine("Message send to device: " + regId +
	                "; messageId = " + messageId);
	            checkCanonicalReqId(regId, result);
	          } else {
	            String error = result.getErrorCodeName();
	            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
	              // application has been removed from device - unregister it
	              logger.info("Remove device: " + regId);
	              removeDevice(regId);
	            } else {
	              logger.severe("Error " + regId + ": " + error);
	            }
	          }
	        }
		} catch (InvalidRequestException e) {
			logger.log(Level.SEVERE, "Invalid Request", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "IO Exception", e);
		}
	}


	private static void checkCanonicalReqId(String regId, Result result) {
		String canonicalRegId = result.getCanonicalRegistrationId();
		if (canonicalRegId != null) {
		  // same device has new registration id: update it
		  // 	- Canonical id: the id used in last registration
		  logger.info("canonicalRegId " + canonicalRegId);
		  updateDevice(regId, canonicalRegId);
		}
	}

	private static String createMessage(ShiftSuggestion bean) {
		return "Date: " +bean.getDate();
	}
	
	//  TODO: extract devices and related methods to it's own class
	static List<String> devices = new LinkedList<String>();
	
	private static synchronized void updateDevice(String regId, String canonicalRegId) {
		devices.remove(regId);
		devices.add(canonicalRegId);
	}

	public static synchronized void addDevice(String regId) {
		devices.add(regId);
	}

	private static synchronized void removeDevice(String regId) {
		devices.remove(regId);
	}

	private static synchronized boolean noDestinations() {
		return devices.size() == 0;
	}
}
