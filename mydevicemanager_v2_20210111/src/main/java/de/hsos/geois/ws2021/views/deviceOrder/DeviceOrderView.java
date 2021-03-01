package de.hsos.geois.ws2021.views.deviceOrder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import de.hsos.geois.ws2021.data.entity.Customer;
import de.hsos.geois.ws2021.data.entity.Device;
import de.hsos.geois.ws2021.data.service.CustomerDataService;
import de.hsos.geois.ws2021.data.service.DeviceOrderDataService;
import de.hsos.geois.ws2021.views.MainView;

@Route(value = "device-order", layout = MainView.class)
@PageTitle("MyDeviceManager")
@CssImport("./styles/views/mydevicemanager/my-device-manager-view.css")
@RouteAlias(value = "deviceOrder", layout = MainView.class)
public class DeviceOrderView extends Div {

	private static final long serialVersionUID = 4939100739729795870L;

	private Grid<Device> grid;
	
	private ComboBox<Customer> customer = new ComboBox<Customer>();
	private ComboBox<Customer> customer2 = new ComboBox<Customer>();
	private IntegerField quantity = new IntegerField();
	private DatePicker deliveryDate = new DatePicker();

	// TODO: Refactore these buttons in a separate (abstract) form class
	private Button cancel = new Button("Cancel");
	private Button createMail = new Button("Create Mail");

	private Binder<Device> binder;															// in DeviceOrder ändern? Binder überhaupt relevant?

	private Device currentDevice = new Device();											// in DeviceOrder ändern?

	private DeviceOrderDataService deviceOrderService;

	public DeviceOrderView() {
		setId("my-device-manager-view");
		this.deviceOrderService = DeviceOrderDataService.getInstance();

		// Configure Form
		binder = new Binder<>(Device.class);													//Klasse ändern

		// Bind fields. This where you'd define e.g. validation rules
		binder.bindInstanceFields(this);														//relevant?

		cancel.addClickListener(e -> {															
			clearForm();
			refreshGrid();
		});

		createMail.addClickListener(e -> {														//Save-Button zu CreateMail-Button ändern
			try {
				if (this.currentDevice == null) {
					this.currentDevice = new Device();
				}
				binder.writeBean(this.currentDevice);											//Werte in FormularFeldern werden in Objekt geschrieben
				this.currentDevice = deviceOrderService.update(this.currentDevice);				//update(...) : Klasse wird in Datenbank neu erstellt oder aktualisiert
				clearForm();																	//Formular leeren
//				refreshGrid();																	//Grid aktualisieren
				Notification.show("Device details stored.");
			} catch (ValidationException validationException) {
				Notification.show("An exception happened while trying to store the device details.");
			}
		});

		// add users to combobox user
		customer.setItems(CustomerDataService.getInstance().getAll());							//ComboBox Inhalt für Producer
		customer2.setItems(CustomerDataService.getInstance().getAll());							//ComboBox Inhalt für Device Model, muss sich automatisch aktualisieren bei Auswahl des Producers

		customer.addValueChangeListener(event -> {												// ???		
			if (event.isFromClient() && event.getValue() != null) {
				event.getValue().addDevice(this.currentDevice);
				CustomerDataService.getInstance().save(event.getValue());
				this.currentDevice.setCustomer(event.getValue());
				try {
					binder.writeBean(this.currentDevice);
				} catch (ValidationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				this.currentDevice = deviceOrderService.update(this.currentDevice);
			}
		});
		
		createEditorLayout();
	}

	private void createEditorLayout() {
		Div editorLayoutDiv = new Div();
		editorLayoutDiv.setId("editor-layout");

		Div editorDiv = new Div();
		editorDiv.setId("editor");
		editorLayoutDiv.add(editorDiv);
//		editorLayoutDiv.setSizeFull();

		FormLayout formLayout = new FormLayout();
		
		quantity.setMin(1);
		quantity.setHasControls(true);
		
		LocalDate dayAfterTomorrow = LocalDate.now().plus(2, ChronoUnit.DAYS);
//		deliveryDate.setValue(dayAfterTomorrow);
		deliveryDate.setMin(dayAfterTomorrow);
		
		addFormItem(editorDiv, formLayout, customer, "Producer");							//zu Producer ändern	
		addFormItem(editorDiv, formLayout, customer2, "Device Model");						//zu Device Model ändern
		addFormItem(editorDiv, formLayout, quantity, "Quantity");
		addFormItem(editorDiv, formLayout, deliveryDate, "Delivery Date");
		
		createButtonLayout(editorLayoutDiv);
		
		add(editorLayoutDiv);
	}

	private void createButtonLayout(Div editorLayoutDiv) {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setId("button-layout");
		buttonLayout.setWidthFull();
		buttonLayout.setSpacing(true);
		cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		createMail.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		buttonLayout.add(createMail, cancel);
		editorLayoutDiv.add(buttonLayout);
	}

//	private void createGridLayout(SplitLayout splitLayout) {
//		Div wrapper = new Div();
//		wrapper.setId("grid-wrapper");
//		wrapper.setWidthFull();
//		splitLayout.addToPrimary(wrapper);
//		wrapper.add(grid);
//	}

	private void addFormItem(Div wrapper, FormLayout formLayout, AbstractField field, String fieldName) {
		formLayout.addFormItem(field, fieldName);
		wrapper.add(formLayout);
		field.getElement().getClassList().add("full-width");
	}

	private void refreshGrid() {
		grid.select(null);
		grid.getDataProvider().refreshAll();
	}

	private void clearForm() {
		populateForm(null);
	}

	private void populateForm(Device value) {
		this.currentDevice = value;
		binder.readBean(this.currentDevice);
	}
}
