package com.vaadin.demo.parking;

import java.util.Collection;

import javax.servlet.annotation.WebServlet;

import com.vaadin.addon.touchkit.ui.Switch;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.demo.parking.model.Shift;
import com.vaadin.demo.parking.ui.HourSelect;
import com.vaadin.demo.parking.ui.ShiftTable;
import com.vaadin.demo.parking.util.DataUtil;
import com.vaadin.demo.phonegap.push.AndroidPushServer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
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
		FormLayout newShiftForm = new ShiftForm();
		Button button = new Button("Push");
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				AndroidPushServer.push("new shift here, please");
			}
		});
		newShiftForm.addComponent(button);
		return newShiftForm;
	}
	
	public class ShiftForm extends FormLayout {
		private TextField area;
		private DateField date;
		private HourSelect start;
		private HourSelect end;

		public ShiftForm() {
			area = new TextField("Area");
			date = new DateField("Date");
			start = new HourSelect("Start");
			end = new HourSelect("End");
			
			addComponent(area);
			addComponent(date);
			addComponent(start);
			addComponent(end);
		}
	}

	private Table createWaitingTable() {
		BeanItemContainer<Shift> waitingContainer = new BeanItemContainer<Shift>(Shift.class);
		Table waitingTable = new ShiftTable("Shifts waiting for confirmation", waitingContainer);
		waitingTable.setPageLength(5);
		waitingTable.setWidth("350px");
		return waitingTable;
	}

	private Table createConfirmedTable() {
		BeanItemContainer<Shift> confirmedContainer = buildShiftContainer();
		Table confimedTable = new ShiftTable("Confirmed shifts", confirmedContainer);
		confimedTable.setWidth("350px");
		return confimedTable;
	}
	
	 private BeanItemContainer<Shift> buildShiftContainer() {
	        return new BeanItemContainer<Shift>(Shift.class,
	                DataUtil.generateRandomShifts());
	    }

}