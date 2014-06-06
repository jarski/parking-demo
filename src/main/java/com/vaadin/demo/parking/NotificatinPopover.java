package com.vaadin.demo.parking;

import com.vaadin.addon.touchkit.ui.HorizontalButtonGroup;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

class NotificatinPopover extends Popover implements ClickListener {
	private Button decline;
	private Button accept;

	public NotificatinPopover(String message) {
		setWidth("300px");
		setHeight("200px");
		createButtons();
		NavigationView content = createContent(message);
		setContent(content);
	}

	private NavigationView createContent(String message) {
		VerticalLayout layout = createLayout(message);
		NavigationView content = new NavigationView(layout);
		content.setCaption("New shift");
		return content;
	}

	private VerticalLayout createLayout(String message) {
		Label label = new Label(message);
		label.setSizeUndefined();
		HorizontalButtonGroup buttonGroup = new HorizontalButtonGroup();
		buttonGroup.setWidth("100%");
		buttonGroup.addComponent(decline);
		buttonGroup.addComponent(accept);
		VerticalLayout layout = new VerticalLayout(label, buttonGroup);
		layout.setSizeFull();
		layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		layout.setExpandRatio(label, 1);
		return layout;
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
			// Do something
		}
		close();
	}
}