package de.hsos.geois.ws2021.views.deviceOrder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import de.hsos.geois.ws2021.data.entity.Customer;
import de.hsos.geois.ws2021.data.entity.Device;
import de.hsos.geois.ws2021.data.entity.DeviceOrder;
import de.hsos.geois.ws2021.data.service.CustomerDataService;
import de.hsos.geois.ws2021.data.service.DeviceOrderDataService;
import de.hsos.geois.ws2021.views.MainView;

@Route(value = "device-order", layout = MainView.class)
@PageTitle("MyDeviceManager")
@CssImport("./styles/views/mydevicemanager/my-device-manager-view.css")
@RouteAlias(value = "deviceOrder", layout = MainView.class)
public class DeviceOrderView extends Div {

	private static final long serialVersionUID = 4939100739729795870L;

//	private Grid<Device> grid;
	
	private ComboBox<String> producer = new ComboBox<>();
	private ComboBox<String> deviceModel = new ComboBox<>();
	private IntegerField quantity = new IntegerField();
	private DatePicker deliveryDate = new DatePicker();

	// TODO: Refactore these buttons in a separate (abstract) form class
	private Button cancel = new Button("Cancel");
	private Button createMail = new Button("Create Mail");

	private Binder<DeviceOrder> binder;

	private DeviceOrder currentDeviceOrder = new DeviceOrder();

	private DeviceOrderDataService deviceOrderService;
	
	public DeviceOrderView() {
		setId("my-device-manager-view");
		this.deviceOrderService = DeviceOrderDataService.getInstance();

		// Configure Form
		binder = new Binder<>(DeviceOrder.class);

		// Bind fields. This where you'd define e.g. validation rules
		binder.bindInstanceFields(this);

		cancel.addClickListener(e -> {															
			clearForm();
//			refreshGrid();
		});

		createMail.addClickListener(e -> {
//			try {
//				this.currentDeviceOrder = new DeviceOrder();
//				
//				binder.writeBean(this.currentDeviceOrder);										//Werte aus FormularFeldern werden in Objekt geschrieben
//				
//				this.currentDeviceOrder = deviceOrderService.update(this.currentDeviceOrder);	//update(...) : Klasse wird in Datenbank neu erstellt oder aktualisiert
//				clearForm();																	//Formular leeren
//				
				openMailControlDialog();
//				
//				Notification.show("New Device Order created.");
//			} catch (ValidationException validationException) {
//				Notification.show("An exception happened while trying to create a new Device Order.");
//			}
		});
		
		// add users to combobox user
		producer.setItems("Apple", "Samsung");													//ComboBox Inhalt für Producer
		deviceModel.setItems("One-way device", "multi-use device");								//ComboBox Inhalt für Device Model, muss sich automatisch aktualisieren bei Auswahl des Producers
		
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
		
		addFormItem(editorDiv, formLayout, producer, "Producer");
		addFormItem(editorDiv, formLayout, deviceModel, "Device Model");
		addFormItem(editorDiv, formLayout, quantity, "Quantity");
		addFormItem(editorDiv, formLayout, deliveryDate, "Delivery Date");
		
		createButtonLayout(editorLayoutDiv, cancel, createMail);
		
		add(editorLayoutDiv);
	}

	private void createButtonLayout(Div editorLayoutDiv, Button cancel, Button accept) {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setId("button-layout");
		buttonLayout.setWidthFull();
		buttonLayout.setSpacing(true);
		cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		accept.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		buttonLayout.add(accept, cancel);
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

//	private void refreshGrid() {
//		grid.select(null);
//		grid.getDataProvider().refreshAll();
//	}

	private void clearForm() {
		populateForm(null);
	}

	private void populateForm(DeviceOrder value) {
		this.currentDeviceOrder = value;
		binder.readBean(this.currentDeviceOrder);
	}
	
	private void openMailControlDialog() {
		Dialog mailControlDialog = new Dialog();
		mailControlDialog.setWidth("800px");
//		controllDialog.setHeight("500px");;
		
		Div mailDiv = new Div();
		mailDiv.setSizeFull();
		mailControlDialog.add(mailDiv);
		
		createMailLayout(mailDiv);
		
		Button sendMail = new Button("Send Mail");
		Button cancelMail = new Button("Cancel");
		
		sendMail.addClickListener(e -> {
			try {
				this.currentDeviceOrder = new DeviceOrder();
				
				binder.writeBean(this.currentDeviceOrder);										//Werte aus FormularFeldern werden in Objekt geschrieben
				
				this.currentDeviceOrder = deviceOrderService.update(this.currentDeviceOrder);	//update(...) : Klasse wird in Datenbank neu erstellt oder aktualisiert
				clearForm();																	//Formular leeren
				
				mailControlDialog.close();
				
				Notification.show("New Device Order created.");
			} catch (ValidationException validationException) {
				Notification.show("An exception happened while trying to create a new Device Order.");
			}
		});
		
		cancelMail.addClickListener(e -> {															
			mailControlDialog.close();
		});
		
		createButtonLayout(mailDiv, cancelMail, sendMail);
		
		mailControlDialog.open();
	}
	
	public void createMailLayout(Div wrapper) {
		
		FormLayout mailItemsLayout = new FormLayout();
		mailItemsLayout.setWidthFull();
		
		TextField from = new TextField();
		TextField to = new TextField();
		TextField subject = new TextField();
		TextArea mailText = new TextArea();
		
		addMailItem(mailItemsLayout, from,  "From:", "devicemanagement@hellmann-logistics.com");
		addMailItem(mailItemsLayout, to,  "To:", "");
		addMailItem(mailItemsLayout, subject, "Subject:", "Device Order");
		addMailItem(mailItemsLayout, mailText, "", "");
		
		mailItemsLayout.setResponsiveSteps(
		        new ResponsiveStep("50em", 1));
		wrapper.add(mailItemsLayout);
	}
	
	public void addMailItem(FormLayout mailItemsLayout, AbstractField field, String fieldName, String fieldValue) {
		field.setValue(fieldValue);
		field.setReadOnly(true);
		
		mailItemsLayout.addFormItem(field, fieldName);
		field.getElement().getClassList().add("full-width");
	}
}
