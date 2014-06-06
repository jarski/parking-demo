package com.vaadin.demo.parking.ui;

import java.text.DateFormat;
import java.util.Calendar;

import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.UI;

public class HourSelect extends NativeSelect {

	private final DateFormat timeFormat = DateFormat.getTimeInstance(
			DateFormat.SHORT, UI.getCurrent().getLocale());
	
	public HourSelect(String caption) {
		setCaption(caption);
		setImmediate(true);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 0);
		addItem(-1);
		setItemCaption(-1, "Choose...");
		setNullSelectionItemId(-1);
		setNullSelectionAllowed(false);
		for (int i = 0; i < 24; i++) {
			cal.set(Calendar.HOUR_OF_DAY, i);
			addItem(i);
			setItemCaption(i, timeFormat.format(cal.getTime()));
		}
	}
}