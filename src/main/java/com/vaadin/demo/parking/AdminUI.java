package com.vaadin.demo.parking;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.demo.parking.model.Shift;
import com.vaadin.demo.parking.model.ShiftSuggestion;
import com.vaadin.demo.parking.ui.HourSelect;
import com.vaadin.demo.parking.ui.ShiftTable;
import com.vaadin.demo.parking.util.DataUtil;
import com.vaadin.demo.phonegap.push.AndroidPushServer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("admin")
public class AdminUI extends UI {

	@WebServlet(value = { "/admin/*", "/VAADIN/*" }, asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = AdminUI.class, widgetset = "com.vaadin.DefaultWidgetSet")
	public static class Servlet extends VaadinServlet {
	}

	private BeanItemContainer<ShiftSuggestion> waitingContainer;
	private DateFormat dateFormat;
	private DateFormat timeFormat;

	@Override
	protected void init(VaadinRequest request) {
		dateFormat = DateFormat.getDateInstance(
				DateFormat.SHORT, getLocale());
		timeFormat = DateFormat.getTimeInstance(
				DateFormat.SHORT, getLocale());
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		Layout newShiftForm = createNewShiftForm();

		Table confimedTable = createConfirmedTable();
		Table waitingTable = createWaitingTable();

		layout.addComponent(newShiftForm);
		layout.addComponent(waitingTable);
		layout.addComponent(confimedTable);
	}

	private Layout createNewShiftForm() {
		FormLayout shiftForm = new ShiftFormLayout();
		ShiftSuggestion shiftSuggestion = createDefaultShiftSuggestion();
		final BeanItem<ShiftSuggestion> item = new BeanItem<ShiftSuggestion>(
				shiftSuggestion);
		FieldGroup binder = new FieldGroup(item);
		binder.bindMemberFields(shiftForm);

		Button button = new Button("Add shift");
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				AndroidPushServer.push("new shift info here, please");
				waitingContainer.addBean(item.getBean());
			}
		});
		shiftForm.addComponent(button);
		return shiftForm;
	}

	private ShiftSuggestion createDefaultShiftSuggestion() {
		ShiftSuggestion shiftSuggestion = new ShiftSuggestion();
		shiftSuggestion.setArea("A1");
		shiftSuggestion.setDate(dateFormat.format(new Date()));
		shiftSuggestion.setStart(8);
		shiftSuggestion.setEnd(16);
		return shiftSuggestion;
	}

	public class ShiftFormLayout extends FormLayout {
		private TextField area;
		private DateField date;
		private HourSelect start;
		private HourSelect end;

		public ShiftFormLayout() {
			area = new TextField("Area");
			date = new DateField("Date");
			date.setConverter(new Converter<Date, String>() {
				@Override
				public String convertToModel(Date value,
						Class<? extends String> targetType, Locale locale)
						throws com.vaadin.data.util.converter.Converter.ConversionException {
					return dateFormat.format(value);
				}

				@Override
				public Date convertToPresentation(String value,
						Class<? extends Date> targetType, Locale locale)
						throws com.vaadin.data.util.converter.Converter.ConversionException {
					try {
						return dateFormat.parse(value);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						throw new ConversionException();
					}
				}

				@Override
				public Class<String> getModelType() {
					return String.class;
				}

				@Override
				public Class<Date> getPresentationType() {
					return Date.class;
				}
				
			});
			start = new HourSelect("Start");
			end = new HourSelect("End");

			addComponent(area);
			addComponent(date);
			addComponent(start);
			addComponent(end);
		}
	}

	private Table createWaitingTable() {
		waitingContainer = new BeanItemContainer<ShiftSuggestion>(
				ShiftSuggestion.class);
		Table waitingTable = new Table("Shifts waiting for confirmation",
				waitingContainer) {
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				String result = super.formatPropertyValue(rowId, colId, property);
				if ("start".equals(colId) || "end".equals(colId)) {
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.HOUR_OF_DAY, (Integer) property.getValue());
					cal.set(Calendar.MINUTE, 0);
					result = timeFormat.format(cal.getTime());
				}
				return result;
			}
		};
		waitingTable.setVisibleColumns("area", "date", "start", "end");
		waitingTable.setPageLength(5);
		waitingTable.setWidth("350px");
		return waitingTable;
	}

	private Table createConfirmedTable() {
		BeanItemContainer<Shift> confirmedContainer = buildShiftContainer();
		Table confimedTable = new ShiftTable("Confirmed shifts",
				confirmedContainer);
		confimedTable.setWidth("350px");
		return confimedTable;
	}

	private BeanItemContainer<Shift> buildShiftContainer() {
		return new BeanItemContainer<Shift>(Shift.class,
				DataUtil.generateRandomShifts());
	}

}