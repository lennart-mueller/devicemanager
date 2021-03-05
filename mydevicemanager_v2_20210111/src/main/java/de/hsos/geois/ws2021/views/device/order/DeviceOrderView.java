package de.hsos.geois.ws2021.views.device.order;

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
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import de.hsos.geois.ws2021.data.entity.Customer;
import de.hsos.geois.ws2021.data.entity.Device;
import de.hsos.geois.ws2021.data.entity.DeviceModel;
import de.hsos.geois.ws2021.data.entity.DeviceOrder;
import de.hsos.geois.ws2021.data.entity.Producer;
import de.hsos.geois.ws2021.data.service.CustomerDataService;
import de.hsos.geois.ws2021.data.service.DeviceModelDataService;
import de.hsos.geois.ws2021.data.service.DeviceOrderDataService;
import de.hsos.geois.ws2021.data.service.ProducerDataService;
import de.hsos.geois.ws2021.views.MainView;
import de.hsos.geois.ws2021.views.device.model.DeviceModelDataProvider;

@Route(value = "device-order", layout = MainView.class)
@PageTitle("MyDeviceManager")
@CssImport("./styles/views/mydevicemanager/my-device-manager-view.css")
@RouteAlias(value = "deviceOrder", layout = MainView.class)
public class DeviceOrderView extends Div {

	private static final long serialVersionUID = 4939100739729795870L;

	private Grid<DeviceOrder> grid;
	
	private ComboBox<Producer> producer = new ComboBox<Producer>();
	private ComboBox<DeviceModel> deviceModel = new ComboBox<DeviceModel>();
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
		
		
		
		 // Configure Grid
        grid = new Grid<>(DeviceOrder.class);
        grid.setColumns("deviceModel","quantity", "orderDate", "deliveryDate");
        grid.setDataProvider(new DeviceOrderDataProvider());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();
        
        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
          producer.setEnabled(false);
          deviceModel.setEnabled(false);
          quantity.setEnabled(false);
          deliveryDate.setEnabled(false);
        });

        
		// Configure Form
		binder = new Binder<>(DeviceOrder.class);

		// Bind fields. This where you'd define e.g. validation rules
		binder.bindInstanceFields(this);

		cancel.addClickListener(e -> {															
			clearForm();
			deviceModel.setEnabled(false);
//			refreshGrid();
		});

		createMail.addClickListener(e -> {
			try {
				this.currentDeviceOrder = new DeviceOrder();
				binder.writeBean(this.currentDeviceOrder);
			} catch (ValidationException e1) {
				Notification.show("An exception happened while trying to create a new Device Order.");
			}
			
			openMailControlDialog();
		});
		
		// add producers to combobox producer
		producer.setItems(ProducerDataService.getInstance().getAll());
		deviceModel.setEnabled(false);
		
		producer.addValueChangeListener(event -> {
			deviceModel.setItems(DeviceModelDataService.getInstance().getDeviceModelsOfProducer(producer.getValue()));								//ComboBox Inhalt für Device Model, muss sich automatisch aktualisieren bei Auswahl des Producers
			deviceModel.setEnabled(true);
		});
		
		
//		deviceModel.addValueChangeListener(event -> {
//			if (event.isFromClient() && event.getValue()!=null) {
//	       		event.getValue().addDeviceOrder(this.currentDeviceOrder);
//	        	DeviceModelDataService.getInstance().save(event.getValue());
//	        	this.currentDeviceOrder.setDeviceModel(event.getValue());
//	        	try {
//					binder.writeBean(this.currentDeviceOrder);
//				} catch (ValidationException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//	            	this.currentDeviceOrder = deviceOrderService.update(this.currentDeviceOrder);
//	        }
//		});
		
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
	}

	private void createEditorLayout(SplitLayout splitLayout) {
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
		
		splitLayout.addToSecondary(editorLayoutDiv);
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

	private void createGridLayout(SplitLayout splitLayout) {
		Div wrapper = new Div();
		wrapper.setId("grid-wrapper");
		wrapper.setWidthFull();
		splitLayout.addToPrimary(wrapper);
		wrapper.add(grid);
	}

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
		
		Button sendMail = new Button("Send Mail");
		Button cancelMail = new Button("Cancel");
		
		createMailLayout(mailDiv);
		createButtonLayout(mailDiv, cancelMail, sendMail);
		mailControlDialog.open();
		
		sendMail.addClickListener(e -> {
//			try {
//				this.currentDeviceOrder = new DeviceOrder();
								
//				binder.writeBean(this.currentDeviceOrder);										//Werte aus FormularFeldern werden in Objekt geschrieben
				
				this.currentDeviceOrder = deviceOrderService.update(this.currentDeviceOrder);	//update(...) : Klasse wird in Datenbank neu erstellt oder aktualisiert
				clearForm();																	//Formular leeren
				
				mailControlDialog.close();
				
				Notification.show("New Device Order created.");
//			} catch (ValidationException validationException) {
//				Notification.show("An exception happened while trying to create a new Device Order.");
//			}
		});
		
		cancelMail.addClickListener(e -> {
			mailControlDialog.close();
		});
	}
	
	public void createMailLayout(Div wrapper) {
		
		FormLayout mailItemsLayout = new FormLayout();
		mailItemsLayout.setWidthFull();
		
		TextField from = new TextField();
		TextField to = new TextField();
		TextField subject = new TextField();
		TextArea mailText = new TextArea();
		
		addMailItem(mailItemsLayout, from,  "From:", "devicemanagement@hellmann-logistics.com");
//		addMailItem(mailItemsLayout, to,  "To:", currentDeviceOrder.getProducer().getEmail());
		addMailItem(mailItemsLayout, to,  "To:", currentDeviceOrder.getDeviceModel().getProducer().getEmail());
		addMailItem(mailItemsLayout, subject, "Subject:", "Device Order: " + currentDeviceOrder.getDeviceModel().getName());
		addMailItem(mailItemsLayout, mailText, null , createMailText());
		
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
	
	private String createMailText() {
		return "Dear " + currentDeviceOrder.getDeviceModel().getProducer().getSalutation() + " " + currentDeviceOrder.getDeviceModel().getProducer().getLastName() + ",\n"
				+ "I am writing to purchase devices from the device model " + currentDeviceOrder.getDeviceModel().getName() + ".\n"
				+ "We would like to order " + currentDeviceOrder.getQuantity() + " devices each " + currentDeviceOrder.getDeviceModel().getPurchasePrice() + "  euros as purchase price. \n"
				+ "It will be grateful if you accept our prefering delivery date of the " + currentDeviceOrder.getDeliveryDate() + ". \n" 
				+ "Should you need any further information, please do not hesitate to contact us.\n"
				+ "I look forward to hearing from you.\n"
				+ "Yours sincerely,\n"
				+ "Devicemanagement - Hellmann Logistics";
	}
}