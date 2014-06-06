package com.vaadin.demo.phonegap.push;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.android.gcm.server.InvalidRequestException;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class AndroidPushServer {
	
	static private Logger logger = Logger.getLogger(AndroidPushServer.class.getName());
	
	// The Project Number of Google API Project (Check https://cloud.google.com/console)
	static String senderId = "AIzaSyAdmmOaBnwv35W45PA6Uw-GZhMYATwQl1w";
	static String destinations;

	public static boolean push(String message) {
		if(noDestinations()) {
			return false;
		}
		
		Sender sender = new Sender(senderId);
	    Message msg = new Message.Builder().addData("message", message)
	    								   .addData("soundname", "beep.wav")
	    								   .addData("msgcnt", "3")
	    								   .addData("title", "My title")
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

	private static boolean noDestinations() {
		return destinations == null;
	}

	public static void addDevice(String regId) {
		destinations = regId;
	}

}
