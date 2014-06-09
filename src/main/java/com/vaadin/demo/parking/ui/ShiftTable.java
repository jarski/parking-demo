package com.vaadin.demo.parking.ui;

import java.text.DateFormat;
import java.util.Calendar;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.model.Shift;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

public class ShiftTable extends Table {
	private final DateFormat dateFormat = DateFormat.getDateInstance(
			DateFormat.SHORT, UI.getCurrent().getLocale());
	private final DateFormat timeFormat = DateFormat.getTimeInstance(
			DateFormat.SHORT, UI.getCurrent().getLocale());

	
	public ShiftTable(String caption,
			BeanItemContainer<Shift> shiftContainer) {
		super(caption, shiftContainer);
		initPropertiesAndHeaders();
	}

	private void initPropertiesAndHeaders() {
		setVisibleColumns(new Object[] { "name", "area", "date", "start",
		"end" });
		for (Object propertyId : getVisibleColumns()) {
			setColumnHeader(propertyId, toFirstUpper((String) propertyId));
		}
		setSortContainerPropertyId("date");
	}
	
	public void updateContainerDataSource(Container dataSource) {
		setContainerDataSource(dataSource);
		initPropertiesAndHeaders();
	}

	@Override
	protected String formatPropertyValue(Object rowId, Object colId,
			Property<?> property) {
		String result = super.formatPropertyValue(rowId, colId, property);
		if ("date".equals(colId)) {
			result = dateFormat.format(property.getValue());
		} else if ("start".equals(colId) || "end".equals(colId)) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, (Integer) property.getValue());
			cal.set(Calendar.MINUTE, 0);
			result = timeFormat.format(cal.getTime());
		}
		return result;
	}
	
	private String toFirstUpper(final String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}
}