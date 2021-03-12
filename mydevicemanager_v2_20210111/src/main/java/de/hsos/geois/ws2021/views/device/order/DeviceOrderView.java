package de.hsos.geois.ws2021.views.device.order;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.flow.component.AbstractField;
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import de.hsos.geois.ws2021.data.entity.DeviceModel;
import de.hsos.geois.ws2021.data.entity.DeviceOrder;
import de.hsos.geois.ws2021.data.entity.OrderPosition;
import de.hsos.geois.ws2021.data.entity.Producer;
import de.hsos.geois.ws2021.data.service.DeviceModelDataService;
import de.hsos.geois.ws2021.data.service.DeviceOrderDataService;
import de.hsos.geois.ws2021.data.service.OrderPositionDataService;
import de.hsos.geois.ws2021.data.service.ProducerDataService;
import de.hsos.geois.ws2021.views.MainView;

@Route(value = "device-order", layout = MainView.class)
@PageTitle("MyDeviceManager")
@CssImport("./styles/views/mydevicemanager/my-device-manager-view.css")
@RouteAlias(value = "deviceOrder", layout = MainView.class)
public class DeviceOrderView extends Div {

	private static final long serialVersionUID = 4939100739729795870L;

	private Grid<DeviceOrder> deviceOrderGrid;
	private Grid<OrderPosition> orderPositionGrid;
	private Collection<OrderPosition> orderPositionList = new ArrayList<>();
	
	private ComboBox<Producer> producer = new ComboBox<Producer>();
	private DatePicker deliveryDate = new DatePicker();
	private ComboBox<DeviceModel> deviceModel = new ComboBox<DeviceModel>();
	private IntegerField quantity = new IntegerField();
	
	private ComboBox<String> newStatus = new ComboBox<String>();

	private Button newOrder = new Button("New Order");
	private Button orderDetails = new Button("Order Details");
	private Button editStatus = new Button("Edit Status");
	
	private Dialog newOrderDialog;
	private Dialog mailControlDialog;
	private Dialog orderDetailsDialog;
	private Dialog statusDialog;
	
	Button sendMail = new Button("Send Mail");
	Button cancelMail = new Button("Cancel");

	private Binder<DeviceOrder> binderForDeviceOrder;
	private DeviceOrder currentDeviceOrder = new DeviceOrder();
	private DeviceOrderDataService deviceOrderService;
	
	private Binder<OrderPosition> binderForOrderPosition;
	private OrderPosition currentPosition = new OrderPosition();
	int posNo;
	
	public DeviceOrderView() {
		
		setId("my-device-manager-view");
		this.deviceOrderService = DeviceOrderDataService.getInstance();
		
		 // Configure Grid
        deviceOrderGrid = new Grid<>(DeviceOrder.class);
        deviceOrderGrid.setColumns("id", "producer", "orderDate", "deliveryDate", "amountOfPositions", "amountOfDevices", "status");
        deviceOrderGrid.setDataProvider(new DeviceOrderDataProvider());
        deviceOrderGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        deviceOrderGrid.setHeight("700px");
        
    	orderDetails.setEnabled(false);
    	editStatus.setEnabled(false);
        
        // when a row is selected or deselected, populate form
        deviceOrderGrid.asSingleSelect().addValueChangeListener(event -> {
        	if (event.getValue() != null) {
            	DeviceOrder deviceOrderFromBackend = event.getValue();
            	this.currentDeviceOrder = deviceOrderFromBackend;
            	
            	orderDetails.setEnabled(true);
            	editStatus.setEnabled(true);
            	
                // when a row is selected but the data is no longer available, refresh grid
                if (deviceOrderFromBackend != null) {
//                    populateForm(deviceOrderFromBackend);
                } else {
                    refreshGrid();
                }
            } else {
                orderDetails.setEnabled(false);
            	editStatus.setEnabled(false);
            }
        	
        });

        
		// Configure Form
		binderForDeviceOrder = new Binder<>(DeviceOrder.class);																							

		binderForOrderPosition = new Binder<>(OrderPosition.class);
		
		// Bind fields. This where you'd define e.g. validation rules
		binderForDeviceOrder.bindInstanceFields(this);																									
		
		binderForOrderPosition.bindInstanceFields(this);
		
		newOrder.addClickListener(e -> {
			producer.setEnabled(true);
			deliveryDate.setEnabled(true);
			deviceModel.setEnabled(false);
			quantity.setEnabled(false);
			
			orderPositionList = new ArrayList<>();
			openNewOrderDialog();
		});
		
		orderDetails.addClickListener(e -> {
			orderPositionList = new ArrayList<>();
			openOrderDetailsDialog();
		});
		
		editStatus.addClickListener(e -> {
			openEditStatusDialog();
		});
		
		
		// add producers to combobox producer
		producer.setItems(ProducerDataService.getInstance().getAll());
		
		producer.addValueChangeListener(event -> {
			deviceModel.setItems(DeviceModelDataService.getInstance().getDeviceModelsOfProducer(producer.getValue()));						
		});
		
		
        createGridLayout();
	}

	/**
	 * Opens a new dialog to change the status of an order
	 */
	private void openEditStatusDialog() {
		statusDialog = new Dialog();
		
		Div statusDiv = new Div();
		statusDialog.add(statusDiv);
		
		FormLayout formLayout = new FormLayout();
		
		newStatus.setItems("Request sent", "Request confirmed", "Order shipped", "Order delivered", "Request refused");
		newStatus.setValue(this.currentDeviceOrder.getStatus());
		
		addFormItem(statusDiv, formLayout, newStatus, "Status");
		Button accept = new Button("Accept");
		Button cancel = new Button("Cancel");
		
		createButtonLayout(statusDiv, cancel, accept);
				
		accept.addClickListener(e -> {
			this.currentDeviceOrder.setStatus(newStatus.getValue());
			this.currentDeviceOrder = deviceOrderService.update(this.currentDeviceOrder);
			refreshGrid();
			statusDialog.close();
		});
		
		cancel.addClickListener(e -> {
			statusDialog.close();
		});
		
		statusDialog.open();
	}

	/**
	 * Creates the grid
	 */
	private void createGridLayout() {
		VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.setId("grid-layout");
		gridLayout.setSizeFull();
		
		Div gridDiv = new Div();
		gridDiv.setId("grid");
		gridDiv.setSizeFull();
		gridLayout.add(gridDiv);
		gridDiv.add(deviceOrderGrid);
		
		createGridButtonsLayout(gridDiv, orderDetails, newOrder, editStatus);
		add(gridLayout);
	}
	
	/**
	 * Opens a dialog to add an order
	 */
	private void openNewOrderDialog() {
		
		newOrderDialog = new Dialog();
		Div newOrderLayoutDiv = new Div();
		newOrderLayoutDiv.setId("newOrder-layout");
		newOrderDialog.add(newOrderLayoutDiv);
		
		Button cancel = new Button("Cancel");
		Button createMail = new Button("Create Mail");
		
		Button selectProducer = new Button("Select Producer");
		selectProducer.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		Button addPosition = new Button("Add Position");
		addPosition.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		// disable buttons until producer is chosen
		addPosition.setEnabled(false);
		createMail.setEnabled(false);

		Div newOrderDiv = new Div();
		newOrderDiv.setId("newOrder");
		newOrderLayoutDiv.add(newOrderDiv);

		quantity.setMin(1);
		quantity.setHasControls(true);
		
		LocalDate dayAfterTomorrow = LocalDate.now().plus(2, ChronoUnit.DAYS);
		deliveryDate.setMin(dayAfterTomorrow);
		
		FormLayout formLayout = new FormLayout();
		formLayout.setWidthFull();
		
		formLayout.setResponsiveSteps(
		           new ResponsiveStep("80px", 1),
		           new ResponsiveStep("30px", 2));
		
		producer.setPlaceholder("Producer");
		deliveryDate.setPlaceholder("Delivery Date");
		deviceModel.setPlaceholder("Device Model");
		quantity.setPlaceholder("Quantity");
		
		formLayout.add(producer, deliveryDate);
		
		selectProducer.addClickListener(e -> {																								
			if(producer.getValue()!=null && deliveryDate.getValue() != null) {
					
				try {
					// disable producer choice and enable addition of order positions
					producer.setEnabled(false);
					deliveryDate.setEnabled(false);
					deviceModel.setEnabled(true);
					quantity.setEnabled(true);
					addPosition.setEnabled(true);
					this.currentDeviceOrder = new DeviceOrder();
					binderForDeviceOrder.writeBean(this.currentDeviceOrder);
				} catch (ValidationException e1) {
					Notification.show("An exception happened while trying to select the producer.");
				}
			} else {
				Notification.show("Producer and Delivery Date are not allowed to be empty.");
			}
		});
		formLayout.add(selectProducer, 2);
		
		formLayout.add(deviceModel, quantity);
		
		newOrderDiv.add(formLayout);
		
		posNo = 1;
		// add position to device order
		addPosition.addClickListener(e -> {																								
			if(deviceModel.getValue()!=null && quantity.getValue() != null) {
				try {
					this.currentPosition = new OrderPosition();
					this.currentPosition.setPosNo(posNo);
					posNo = posNo + 1;
					this.currentDeviceOrder.addOrderPosition(this.currentPosition);
					this.currentPosition.setDeviceOrder(this.currentDeviceOrder);
					binderForOrderPosition.writeBean(this.currentPosition);
					orderPositionList.add(this.currentPosition);
					orderPositionGrid.setItems(orderPositionList);
					
					deviceModel.clear();
					quantity.clear();
					createMail.setEnabled(true);
				} catch (ValidationException e1) {
					Notification.show("An exception happened while trying to create a new Order Position.");
				}
			} else {
				Notification.show("Device Model and Quantity are not allowed to be empty.");
			}
		});
		newOrderDiv.add(addPosition);
		
		createOrderPositionsGrid(newOrderDiv);
		
		cancel.addClickListener(e -> {															
			deviceModel.setEnabled(false);
			newOrderDialog.close();
			clearForm();
			orderPositionList.clear();
		});

		createMail.addClickListener(e -> {
			openMailControlDialog();
		});
		
		createButtonLayout(newOrderLayoutDiv, cancel, createMail);
		
		newOrderDialog.open();
	}

	private void createOrderPositionsGrid(Div wrapper) {
		orderPositionGrid = new Grid<>(OrderPosition.class);
		orderPositionGrid.setColumns("posNo", "deviceModel", "quantity");
		orderPositionGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		orderPositionGrid.setWidthFull();
		wrapper.add(orderPositionGrid);
		
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
	
	private void createGridButtonsLayout(Div editorLayoutDiv, Button cancel, Button accept, Button edit) {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setId("button-layout");
		buttonLayout.setWidthFull();
		buttonLayout.setSpacing(true);
		edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		accept.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		buttonLayout.add(accept, cancel, edit);
		editorLayoutDiv.add(buttonLayout);
	}
	
	/**
	 * display of information of a created order
	 */
	public void openOrderDetailsDialog() {
		orderDetailsDialog = new Dialog();
		orderDetailsDialog.setWidth("700px");
		Div detailsLayoutDiv = new Div();
		detailsLayoutDiv.setId("orderDetails-layout");
		orderDetailsDialog.add(detailsLayoutDiv);

		FormLayout formLayout = new FormLayout();
		
		DatePicker orderDate = new DatePicker();
		TextField producer = new TextField();
		DatePicker deliveryDate = new DatePicker();
		TextField status= new TextField();
		TextField amountOfPositions = new TextField();
		TextField amountOfDevices = new TextField();

		addFormItem(detailsLayoutDiv, formLayout, producer, "Producer");
		addFormItem(detailsLayoutDiv, formLayout, orderDate, "Order Date");
		addFormItem(detailsLayoutDiv, formLayout, amountOfPositions, "Amount Of Positions");
		addFormItem(detailsLayoutDiv, formLayout, deliveryDate, "Delivery Date");
		addFormItem(detailsLayoutDiv, formLayout, amountOfDevices, "Amount Of Devices");
		addFormItem(detailsLayoutDiv, formLayout, status, "Status");
		
		orderDate.setValue(this.currentDeviceOrder.getOrderDate());
		producer.setValue(this.currentDeviceOrder.getProducer().getCompanyName());
		deliveryDate.setValue(this.currentDeviceOrder.getDeliveryDate());
		status.setValue(this.currentDeviceOrder.getStatus());
		amountOfPositions.setValue(String.valueOf(this.currentDeviceOrder.getAmountOfPositions()));
		amountOfDevices.setValue(String.valueOf(this.currentDeviceOrder.getAmountOfDevices()));
		
		// information is read-only since the order is already sent
		orderDate.setReadOnly(true);
		producer.setReadOnly(true);
		deliveryDate.setReadOnly(true);
		status.setReadOnly(true);
		amountOfPositions.setReadOnly(true);
		amountOfDevices.setReadOnly(true);
						
		createOrderPositionsGrid(detailsLayoutDiv);
		orderPositionList = OrderPositionDataService.getInstance().getOrderPositionsOfDeviceOrder(this.currentDeviceOrder);
		orderPositionGrid.setItems(orderPositionList);
		
		Button back = new Button("Back");
		detailsLayoutDiv.add(back);
		
		back.addClickListener(e -> {
			orderDetailsDialog.close();
		});
		orderDetailsDialog.open();
	}
		
	
	@SuppressWarnings("rawtypes")
	private void addFormItem(Div wrapper, FormLayout formLayout, AbstractField field, String fieldName) {
		formLayout.addFormItem(field, fieldName);
		wrapper.add(formLayout);
		field.getElement().getClassList().add("full-width");
	}
	
	/**
	 * Simulates sending an e-mail with the order information to the producer and saves the order
	 */
	private void openMailControlDialog() {
		mailControlDialog = new Dialog();
		mailControlDialog.setWidth("800px");
		
		Div mailDiv = new Div();
		mailDiv.setSizeFull();
		mailControlDialog.add(mailDiv);
			
		createMailLayout(mailDiv);
		createButtonLayout(mailDiv, cancelMail, sendMail);
		mailControlDialog.open();
		
		sendMail.addClickListener(e -> {
			
			this.currentDeviceOrder.setAmountOfPositions(this.currentDeviceOrder.getOrderPositions());
			this.currentDeviceOrder.setAmountOfDevices(this.currentDeviceOrder.getOrderPositions());
			
			// creates or updates DeviceOrder depending on whether this DeviceOrder already exists
			this.currentDeviceOrder = deviceOrderService.update(this.currentDeviceOrder);
			
			clearForm();
			mailControlDialog.close();
			newOrderDialog.close();
			Notification.show("New Device Order created.");
			refreshGrid();
			orderPositionList.clear();
		});
		
		cancelMail.addClickListener(e -> {
			mailControlDialog.close();
		});
	}
	
	/**
	 * Creates an e-mail layout
	 * @param wrapper
	 */
	public void createMailLayout(Div wrapper) {
		
		FormLayout mailItemsLayout = new FormLayout();
		mailItemsLayout.setWidthFull();
		
		TextField from = new TextField();
		TextField to = new TextField();
		TextField subject = new TextField();
		TextArea mailText = new TextArea();
		
		addMailItem(mailItemsLayout, from,  "From:", "devicemanagement@hellmann-logistics.com");
		addMailItem(mailItemsLayout, to,  "To:", currentDeviceOrder.getProducer().getEmail());
		addMailItem(mailItemsLayout, subject, "Subject:", "Device Order");
		addMailItem(mailItemsLayout, mailText, null , createMailText());
		
		mailItemsLayout.setResponsiveSteps(
		        new ResponsiveStep("50em", 1));
		wrapper.add(mailItemsLayout);
		
		Button editText = new Button("Edit Text");
		Button saveText = new Button("Save Text");
		saveText.setVisible(false);
		
		editText.addClickListener(e -> {
			saveText.setVisible(true);
			editText.setVisible(false);
			mailText.setReadOnly(false);
			sendMail.setEnabled(false);
			cancelMail.setEnabled(false);
		});
		
		saveText.addClickListener(e -> {
			saveText.setVisible(false);
			editText.setVisible(true);
			mailText.setReadOnly(true);
			sendMail.setEnabled(true);
			cancelMail.setEnabled(true);
		});
		
		createButtonLayout(wrapper, saveText, editText);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addMailItem(FormLayout mailItemsLayout, AbstractField field, String fieldName, String fieldValue) {
		field.setValue(fieldValue);
		field.setReadOnly(true);
		
		mailItemsLayout.addFormItem(field, fieldName);
		field.getElement().getClassList().add("full-width");
	}
	
	/**
	 * 
	 * @return initial text for an e-mail to order devices including the producers information
	 */
	public String createMailText() {
		return "Dear " + currentDeviceOrder.getProducer().getSalutation() + " " + currentDeviceOrder.getProducer().getLastName() + ",\n"
				+ "I am writing to order the following device models:\n"
				+ printDeviceModels()
				+ "It will be grateful if you accept our prefering delivery date of the " + currentDeviceOrder.getDeliveryDate() + ". \n" 
				+ "Should you need any further information, please do not hesitate to contact us.\n"
				+ "I look forward to hearing from you.\n"
				+ "Yours sincerely,\n"
				+ "Devicemanagement - Hellmann Logistics";
	}
	
	public String printDeviceModels() {
		String string= "";
		if (orderPositionList != null) {
			for(OrderPosition position : orderPositionList ) {
				String row = "Pos. " + position.getPosNo() + ": " + position.getDeviceModel().getName() + " (" + position.getDeviceModel().getPurchasePrice() + " euros per device), Quantity: " + position.getQuantity() +"\n";
				string = string + row;
			}
		}	
		return string;
	}
	
	private void refreshGrid() {
		deviceOrderGrid.select(null);
		deviceOrderGrid.getDataProvider().refreshAll();
	}

	private void clearForm() {
		populateForm(null);
	}

	private void populateForm(DeviceOrder value) {
		this.currentDeviceOrder = value;
		binderForDeviceOrder.readBean(this.currentDeviceOrder);
	}
}