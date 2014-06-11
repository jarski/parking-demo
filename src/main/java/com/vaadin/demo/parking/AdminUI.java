package com.vaadin.demo.parking;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.demo.parking.model.Shift;
import com.vaadin.demo.parking.model.ShiftSuggestion;
import com.vaadin.demo.parking.ui.HourSelect;
import com.vaadin.demo.parking.ui.ShiftTable;
import com.vaadin.demo.parking.util.DataUtil;
import com.vaadin.demo.parking.util.DataUtil.ShiftListener;
import com.vaadin.demo.phonegap.push.AndroidPushServer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("admin")
@Push
public class AdminUI extends UI implements ShiftListener {

	@WebServlet(value = { "/admin/*", "/VAADIN/*" }, asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = AdminUI.class, widgetset = "com.vaadin.DefaultWidgetSet")
	public static class Servlet extends VaadinServlet {
	}

	private BeanItemContainer<ShiftSuggestion> waitingContainer;
	private DateFormat dateFormat;
	private DateFormat timeFormat;
	private FieldGroup binder  = new FieldGroup();
	private ShiftTable confimedTable;
	private HorizontalLayout controlsLayout;
	private Table waitingTable;

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

		confimedTable = createConfirmedTable();
		waitingTable = createWaitingTable();
		final Component waitingTableControls = createWaitingTableControls(waitingTable);

		layout.addComponent(newShiftForm);
		layout.addComponent(waitingTable);
		layout.addComponent(waitingTableControls);
		layout.addComponent(confimedTable);

		DataUtil.addShiftListener(this);
	}

	private Component createWaitingTableControls(final Table waitingTable) {
		controlsLayout = new HorizontalLayout();
		final Button deleteWaitingShift = new Button("Delete");
		Button remindShift = new Button("Remind");
		
		deleteWaitingShift.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				DataUtil.deleteShiftSuggestion((ShiftSuggestion) waitingTable.getValue());
				updateWaitingTable();
				waitingTable.setValue(null);
			}
		});
		remindShift.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				AndroidPushServer.pushNewShift((ShiftSuggestion) waitingTable.getValue());
			}
		});
		
		controlsLayout.setVisible(false);
		waitingTable.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				updateControlsVisibility();
			}
		});
		
		controlsLayout.setSpacing(true);
		controlsLayout.addComponent(deleteWaitingShift);
		controlsLayout.addComponent(remindShift);
		return controlsLayout;
	}

	private Layout createNewShiftForm() {
		FormLayout shiftForm = new ShiftFormLayout();
		final BeanItem<ShiftSuggestion> item = createDefaultShiftSuggestionAndBindIt();
		binder.bindMemberFields(shiftForm);

		Button button = new Button("Add shift");
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					binder.commit();
				} catch (CommitException e) {
					e.printStackTrace();
				}
				waitingContainer.addBean(getEditedShiftSuggestion());
				DataUtil.addShiftSuggestion(getEditedShiftSuggestion());
				AndroidPushServer.pushNewShift(getEditedShiftSuggestion());
				createDefaultShiftSuggestionAndBindIt();
			}

		});
		shiftForm.addComponent(button);
		return shiftForm;
	}
	
	@Override
	public void detach() {
		super.detach();
		DataUtil.removeShiftListener(this);
	}
	
	@SuppressWarnings("unchecked")
	private ShiftSuggestion getEditedShiftSuggestion() {
		return ((BeanItem<ShiftSuggestion>) binder.getItemDataSource()).getBean();
	}

	private BeanItem<ShiftSuggestion> createDefaultShiftSuggestionAndBindIt() {
		ShiftSuggestion shiftSuggestion = createDefaultShiftSuggestion();
		final BeanItem<ShiftSuggestion> item = new BeanItem<ShiftSuggestion>(
				shiftSuggestion);
		binder.setItemDataSource(item);
		return item;
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
		waitingContainer = buildShiftSuggestionContainer();
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
		waitingTable.setImmediate(true);
		waitingTable.setSelectable(true);
		return waitingTable;
	}

	private ShiftTable createConfirmedTable() {
		BeanItemContainer<Shift> confirmedContainer = buildShiftContainer();
		ShiftTable confimedTable = new ShiftTable("Confirmed shifts",
				confirmedContainer);
		confimedTable.setWidth("350px");
		return confimedTable;
	}

	private BeanItemContainer<Shift> buildShiftContainer() {
		return new BeanItemContainer<Shift>(Shift.class,
				DataUtil.getShifts());
	}
	
	private BeanItemContainer<ShiftSuggestion> buildShiftSuggestionContainer() {
		return new BeanItemContainer<ShiftSuggestion>(ShiftSuggestion.class,
				DataUtil.getShiftSuggestions());
	}

	@Override
	public void newShiftAdded(Shift shift) {
		access(new Runnable() {
			@Override
			public void run() {
				Notification.show("Shift accepted", Notification.Type.TRAY_NOTIFICATION);
				updateWaitingTable();
				confimedTable.updateContainerDataSource(buildShiftContainer());
			}

		});
	}
	
	private void updateWaitingTable() {
		waitingContainer.removeAllItems();
		waitingContainer.addAll(DataUtil.getShiftSuggestions());
		updateControlsVisibility();
	}
	
	private void updateControlsVisibility() {
		controlsLayout.setVisible(waitingTable.getValue() != null);
	}
}