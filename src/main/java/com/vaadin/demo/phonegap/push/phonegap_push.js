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

		$("#app-status-ul").append('<li>deviceready event received</li>');

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
				$("#app-status-ul").append('<li>registering android</li>');
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

	// handle APNS notifications for iOS
	/*
	 * function onNotificationAPN(e) { if (e.alert) {
	 * $("#app-status-ul").append('<li>push-notification: ' + e.alert + '</li>');
	 * navigator.notification.alert(e.alert); }
	 * 
	 * if (e.sound) { var snd = new Media(e.sound); snd.play(); }
	 * 
	 * if (e.badge) {
	 * pushNotification.setApplicationIconBadgeNumber(successHandler, e.badge); } },
	 */

	// handle GCM notifications for Android
	onNotificationGCM : function(e) {
		$("#app-status-ul")
				.append('<li>EVENT -> RECEIVED:' + e.event + '</li>');

		switch (e.event) {
		case 'registered':
			if (e.regid.length > 0) {
				$("#app-status-ul").append(
						'<li>REGISTERED -> REGID:' + e.regid + "</li>");
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
				$("#app-status-ul").append(
						'<li>--INLINE NOTIFICATION--' + '</li>');

				// if the notification contains a soundname, play it.
				var my_media = new Media("/android_asset/www/"
						+ e.payload.soundname);
				my_media.play();
			} else { // otherwise we were launched because the user touched a
						// notification in the notification tray.
				if (e.coldstart) {
					$("#app-status-ul").append(
							'<li>--COLDSTART NOTIFICATION--' + '</li>');
				} else {
					$("#app-status-ul").append(
							'<li>--BACKGROUND NOTIFICATION--' + '</li>');
				}
			}

			$("#app-status-ul").append(
					'<li>MESSAGE -> MSG: ' + e.payload.message + '</li>');
			$("#app-status-ul").append(
					'<li>MESSAGE -> MSGCNT: ' + e.payload.msgcnt + '</li>');
			break;

		case 'error':
			$("#app-status-ul").append('<li>ERROR -> MSG:' + e.msg + '</li>');
			break;

		default:
			$("#app-status-ul")
					.append(
							'<li>EVENT -> Unknown, an event was received and we do not know what it is</li>');
			break;
		}
	},

	tokenHandler : function(result) {
		$("#app-status-ul").append('<li>token: ' + result + '</li>');
		// Your iOS push server needs to know the token before it can push to
		// this device
		// here is where you might want to send it the token for later use.
	},

	successHandler : function(result) {
		$("#app-status-ul").append('<li>success:' + result + '</li>');
	},

	errorHandler : function(error) {
		$("#app-status-ul").append('<li>error:' + error + '</li>');
	}

};
