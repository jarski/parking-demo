/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var phoneGapPushExtension;

window.com_vaadin_demo_phonegap_push_PhoneGapPushExtension = function() {
	this.initialize = function(command) {
		if (localStorage.getItem("isWrappedInPhoneGap")) {
			app.initialize();
			phoneGapPushExtension = this;
		}
	};
};

var app = {
	// Application Constructor
	initialize : function() {
		this.bindEvents();
	},
	// Bind Event Listeners
	//
	// Bind any events that are required on startup. Common events are:
	// 'load', 'deviceready', 'offline', and 'online'.
	bindEvents : function() {
		document.addEventListener('deviceready', this.onDeviceReady, false);
	},
	// deviceready Event Handler
	//
	// The scope of 'this' is the event. In order to call the 'receivedEvent'
	// function, we must explicity call 'app.receivedEvent(...);'
	onDeviceReady : function() {

		document.addEventListener("backbutton", function(e) {
			// call this to get a new token each time. don't call it to
			// reuse existing token.
			console.log("unregister..");
			pushNotification.unregister(app.successHandler, app.errorHandler);
			e.preventDefault();
			navigator.app.exitApp();
		}, false);

		try {
			var pushNotification = window.plugins.pushNotification;
			if (device.platform == 'android' || device.platform == 'Android') {
				pushNotification.register(app.successHandler, app.errorHandler,
						{
							"senderID" : "280534261435",
							"ecb" : "app.onNotificationGCM"
						}); // required!
			} 
		} catch (err) {
			txt = "There was an error on this page.\n\n";
			txt += "Error description: " + err.message + "\n\n";
			alert(txt);
		}
	},

	// handle GCM notifications for Android
	onNotificationGCM : function(e) {
		switch (e.event) {
		case 'registered':
			if (e.regid.length > 0) {
				// Your GCM push server needs to know the regID before it can
				// push to this device
				// here is where you might want to send it the regID for later
				// use.
				console.log("regID = " + e.regid);
				phoneGapPushExtension.registered(e.regid);
			}
			break;

		case 'message':
			phoneGapPushExtension.receivedNotification(e.payload);
			
			// if this flag is set, this notification happened while we were in
			// the foreground.
			// you might want to play a sound to get the user's attention, throw
			// up a dialog, etc.
			if (e.foreground) {
				// if the notification contains a soundname, play it.
				var my_media = new Media("/android_asset/www/"
						+ e.payload.soundname);
				my_media.play();
			} else { // otherwise we were launched because the user touched a
						// notification in the notification tray.
				if (e.coldstart) {
				} else {
				}
			}

			break;

		case 'error':
			break;

		default:
			break;
		}
	},

	successHandler : function(result) {
	},

	errorHandler : function(error) {
	}

};
