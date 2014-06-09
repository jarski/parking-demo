package com.vaadin.demo.parking;

import java.text.DateFormat;
import java.util.Calendar;

import com.vaadin.addon.touchkit.ui.HorizontalButtonGroup;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.demo.parking.model.ShiftSuggestion;
import com.vaadin.demo.parking.util.DataUtil;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

class NotificatinPopover extends Popover implements ClickListener {
	private Button decline;
	private Button accept;
	private ShiftSuggestion shiftSuggestion;

	public NotificatinPopover() {
		setWidth("300px");
		setHeight("200px");
		createButtons();
	}
	
	public void setShiftSuggestion(ShiftSuggestion shiftSuggestion) {
		this.shiftSuggestion = shiftSuggestion;
		NavigationView content = createContent();
		setContent(content);
	}
	
	private NavigationView createContent() {
		VerticalLayout layout = createLayout();
		NavigationView content = new NavigationView(layout);
		content.setCaption("New shift");
		return content;
	}

	private VerticalLayout createLayout() {
		Component info = createShiftInfo();
		info.setSizeUndefined();
		HorizontalButtonGroup buttonGroup = new HorizontalButtonGroup();
		buttonGroup.setWidth("100%");
		buttonGroup.addComponent(decline);
		buttonGroup.addComponent(accept);
		VerticalLayout layout = new VerticalLayout(info, buttonGroup);
		layout.setSizeFull();
		layout.setComponentAlignment(info, Alignment.MIDDLE_CENTER);
		layout.setExpandRatio(info, 1);
		return layout;
	}

	private Component createShiftInfo() {
		String message = createPreformattedMessage();
		Label label = new Label(message);
		label.setContentMode(ContentMode.PREFORMATTED);
		label.setSizeUndefined();
		return label;
	}

	private String createPreformattedMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append("Area ");
		builder.append(shiftSuggestion.getArea());
		builder.append("\n");
		builder.append("Date ");
		builder.append(shiftSuggestion.getDate());
		builder.append("\n");
		builder.append("Time ");
		DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, UI.getCurrent().getLocale());
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR, shiftSuggestion.getStart());
		builder.append(timeFormat.format(calendar.getTime()));
		builder.append(" - ");
		calendar.set(Calendar.HOUR, shiftSuggestion.getEnd());
		builder.append(timeFormat.format(calendar.getTime()));
		String message = builder.toString();
		return message;
	}


	private void createButtons() {
		decline = new Button("Decline", this);
		accept = new Button("Accept", this);
		decline.setWidth("50%");
		accept.setWidth("50%");
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if(accept.equals(event.getButton())) {
			DataUtil.confirmed(shiftSuggestion);
			Notification.show("Shift confirmed", Notification.Type.TRAY_NOTIFICATION);
		}
		close();
	}
}