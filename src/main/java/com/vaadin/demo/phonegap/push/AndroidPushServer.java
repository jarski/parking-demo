package com.vaadin.demo.phonegap.push;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.android.gcm.server.InvalidRequestException;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.model.ShiftSuggestion;

public class AndroidPushServer {
	
	static private Logger logger = Logger.getLogger(AndroidPushServer.class.getName());
	
	// The Project Number of Google API Project (Check https://cloud.google.com/console)
	static String senderId = "AIzaSyAdmmOaBnwv35W45PA6Uw-GZhMYATwQl1w";
	static String destinations;

	public static boolean pushNewShift(ShiftSuggestion bean) {
		if(noDestinations()) {
			return false;
		}
		
		Sender sender = new Sender(senderId);
		Message msg = new Message.Builder().addData("message", createMessage(bean))
				.addData("soundname", "beep.wav")
				.addData("title", "New shift available")
				.addData("area", bean.getArea())
				.addData("date", bean.getDate())
				.addData("start", bean.getStart().toString())
				.addData("end", bean.getEnd().toString())
				.build();
		
		try {
			Result result = sender.send(msg, destinations, 1);
			
			if (result.getErrorCodeName() == null) {
				logger.log(Level.INFO,"GCM Notification is sent successfully");
				return true;
			}
			
			logger.log(Level.SEVERE, "Error occurred while sending push notification :" + result.getErrorCodeName());
		} catch (InvalidRequestException e) {
			logger.log(Level.SEVERE, "Invalid Request", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "IO Exception", e);
		}
		return false;
		
	}

	private static String createMessage(ShiftSuggestion bean) {
		return "Date: " +bean.getDate();
	}

	public static void addDevice(String regId) {
		destinations = regId;
	}


	private static boolean noDestinations() {
		return destinations == null;
	}
}
