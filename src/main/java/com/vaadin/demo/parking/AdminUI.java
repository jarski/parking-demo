package com.vaadin.demo.parking;

import javax.servlet.annotation.WebServlet;

import com.vaadin.addon.touchkit.ui.Switch;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.demo.phonegap.push.AndroidPushServer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("admin")
public class AdminUI extends UI {

	@WebServlet(value = {"/admin/*", "/VAADIN/*"}, asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = AdminUI.class, widgetset = "com.vaadin.DefaultWidgetSet")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

		Button button = new Button("Push");
		final TextField textField = new TextField();
		
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				AndroidPushServer.push(textField.getValue());
			}
		});
		layout.addComponent(button);
		layout.addComponent(textField);
	}

}