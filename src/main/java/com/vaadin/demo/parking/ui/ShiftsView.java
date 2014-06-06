package com.vaadin.demo.parking.ui;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;

import com.google.gwt.thirdparty.guava.common.base.Objects;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.addon.touchkit.ui.DatePicker;
import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.NavigationManager.NavigationEvent.Direction;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.model.Shift;
import com.vaadin.demo.parking.util.DataUtil;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class ShiftsView extends NavigationManager {

	private static final String STYLE_NAME = "shifts";
	private static final String STYLE_NAME_TABLE = "shiftstable";
	private static final String STYLE_NAME_FILTER = "shiftsfilter";

	private BeanItemContainer<Shift> shiftContainer;

	private final Collection<Field<?>> filterFields = Lists.newArrayList();

	private final DateFormat dateFormat = DateFormat.getDateInstance(
			DateFormat.SHORT, ParkingUI.getApp().getLocale());
	private final DateFormat timeFormat = DateFormat.getTimeInstance(
			DateFormat.SHORT, ParkingUI.getApp().getLocale());

	@Override
	public void attach() {
		if (shiftContainer == null) {
			buildUi();
		}
		super.attach();
	};

	private void buildUi() {
		setStyleName(STYLE_NAME);
		setCaption("Shifts");
		setSizeFull();

		shiftContainer = buildShiftContainer();

		CssLayout contentLayout = new CssLayout();
		contentLayout.setCaption("Shifts");
		contentLayout.setSizeFull();

		NavigationView filteringLayout = buildFilteringLayout();
		contentLayout.addComponent(filteringLayout);
		contentLayout.addComponent(buildEditFiltersButton(filteringLayout));
		contentLayout.addComponent(buildShiftTable());

		setCurrentComponent(contentLayout);

	}

	private Component buildEditFiltersButton(
			final NavigationView filteringLayout) {
		final VerticalComponentGroup editFiltersGroup = new VerticalComponentGroup();
		editFiltersGroup.addStyleName("editfiltersbutton");

		final Component filtersContent = filteringLayout.getContent();

		final NavigationView navigationView = new NavigationView("Filters");
		navigationView.addStyleName("filtersview");
		navigationView.setSizeFull();
		navigationView.setRightComponent(buildClearButton());
		final NavigationButton editButton = new NavigationButton(
				"Edit filters...", navigationView);

		ValueChangeListener vcl = new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent event) {
				if (shiftContainer.hasContainerFilters()) {
					StringBuilder sb = new StringBuilder();
					for (Field<?> field : filterFields) {
						Object value = field.getValue();
						if (value != null
								&& !String.valueOf(value).trim().isEmpty()) {
							if (!sb.toString().isEmpty()) {
								sb.append(", ");
							}

							if (value instanceof Date) {
								sb.append(dateFormat.format(value));
							} else if (field instanceof AbstractSelect) {
								sb.append(((AbstractSelect) field)
										.getItemCaption(value));
							} else {
								sb.append(String.valueOf(value));
							}
						}
					}
					editButton.setCaption(sb.toString());
				} else {
					editButton.setCaption("Edit filters...");
				}
			}
		};

		for (Field<?> field : filterFields) {
			field.addValueChangeListener(vcl);
		}

		addNavigationListener(new NavigationListener() {
			@Override
			public void navigate(final NavigationEvent event) {
				if (event.getDirection() == Direction.FORWARD) {
					navigationView.setContent(filtersContent);
				} else {
					filteringLayout.setContent(filtersContent);
				}
			}
		});
		editFiltersGroup.addComponent(editButton);
		return editFiltersGroup;
	}

	private Component buildClearButton() {
		return new Button("Clear", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				for (Field<?> field : filterFields) {
					field.setValue(null);
				}
			}
		});
	}

	private static String toFirstUpper(final String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	private BeanItemContainer<Shift> buildShiftContainer() {
		return new BeanItemContainer<Shift>(Shift.class,
				DataUtil.generateRandomShifts());
	}

	private Component buildShiftTable() {
        final Table shiftTable = new ShiftTable(null, shiftContainer);
        shiftTable.addStyleName(STYLE_NAME_TABLE);
        shiftTable.setSizeFull();
        return shiftTable;
    }

	private NavigationView buildFilteringLayout() {
		final NavigationView filteringLayout = new NavigationView();
		filteringLayout.addStyleName(STYLE_NAME_FILTER);
		filteringLayout.setCaption("Filters");

		filteringLayout.setContent(buildFiltersLayout());
		filteringLayout.setRightComponent(buildClearButton());

		return filteringLayout;
	}

	private Component buildFiltersLayout() {
		filterFields.add(new FilteringTextField("name"));
		filterFields.add(new FilteringTextField("area"));
		filterFields.add(new FilteringDatePicker("date"));
		filterFields.add(createHoursSelect("start"));
		filterFields.add(createHoursSelect("end"));

		final VerticalComponentGroup filtersGroup = new VerticalComponentGroup();
		for (Field<?> field : filterFields) {
			filtersGroup.addComponent(field);
		}
		return filtersGroup;
	}

	

	private class FilteringTextField extends TextField {
		private Filter filter;
		private final String propertyId;

		public FilteringTextField(final String propertyId) {
			setWidth(100.0f, Unit.PERCENTAGE);
			setCaption(toFirstUpper(propertyId));
			setNullRepresentation("");
			this.propertyId = propertyId;

			addTextChangeListener(new TextChangeListener() {
				@Override
				public void textChange(final TextChangeEvent event) {
					textChanged(event.getText());
				}
			});

			addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(
						final com.vaadin.data.Property.ValueChangeEvent event) {
					textChanged((String) event.getProperty().getValue());
				}
			});
		}

		private void textChanged(String text) {
			shiftContainer.removeContainerFilter(filter);
			if (text != null && !text.isEmpty()) {
				filter = new SimpleStringFilter(propertyId, text, true, false);
				shiftContainer.addContainerFilter(filter);
			}
		}
	}

	private class FilteringDatePicker extends DatePicker {
		private Filter filter;

		public FilteringDatePicker(final String propertyId) {
			setWidth(100.0f, Unit.PERCENTAGE);
			setCaption(toFirstUpper(propertyId));

			addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(
						final com.vaadin.data.Property.ValueChangeEvent event) {
					shiftContainer.removeContainerFilter(filter);
					final Date filterValue = (Date) event.getProperty()
							.getValue();
					if (filterValue != null) {
						filter = new Filter() {

							@Override
							public boolean passesFilter(final Object itemId,
									final Item item)
									throws UnsupportedOperationException {
								Date propertyValue = (Date) item
										.getItemProperty(propertyId).getValue();
								return propertyValue.after(filterValue);
							}

							@Override
							public boolean appliesToProperty(final Object pid) {
								return Objects.equal(pid, propertyId);
							}
						};
						shiftContainer.addContainerFilter(filter);
					}
				}
			});

		}
	}
	
	private HourSelect createHoursSelect(final String propertyId) {
		HourSelect hourSelect = new HourSelect(toFirstUpper(propertyId));
		hourSelect.setWidth(100.0f, Unit.PERCENTAGE);
		hourSelect.addValueChangeListener(new ValueChangeListener() {
			private Filter filter;
			
			@Override
			public void valueChange(
					final com.vaadin.data.Property.ValueChangeEvent event) {
				shiftContainer.removeContainerFilter(filter);
				final Integer filterValue = (Integer) event.getProperty()
						.getValue();
				if (filterValue != null) {
					filter = new Filter() {

						@Override
						public boolean passesFilter(final Object itemId,
								final Item item)
								throws UnsupportedOperationException {
							return filterValue.equals(item.getItemProperty(
									propertyId).getValue());
						}

						@Override
						public boolean appliesToProperty(final Object pid) {
							return Objects.equal(pid, propertyId);
						}
					};
					shiftContainer.addContainerFilter(filter);
				}
			}
		});
		return hourSelect;
	}
}
