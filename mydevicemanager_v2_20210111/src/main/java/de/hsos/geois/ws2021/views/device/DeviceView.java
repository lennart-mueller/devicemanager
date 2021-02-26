package de.hsos.geois.ws2021.views.device;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import de.hsos.geois.ws2021.data.entity.Customer;
import de.hsos.geois.ws2021.data.entity.Device;
import de.hsos.geois.ws2021.data.service.CustomerDataService;
import de.hsos.geois.ws2021.data.service.DeviceDataService;
import de.hsos.geois.ws2021.views.MainView;

@Route(value = "device", layout = MainView.class)
@PageTitle("MyDeviceManager")
@CssImport("./styles/views/mydevicemanager/my-device-manager-view.css")
@RouteAlias(value = "device", layout = MainView.class)
public class DeviceView extends Div {

	private static final long serialVersionUID = 4939100739729795870L;

	private Grid<Device> grid;

    private TextField name = new TextField();
    private TextField artNr = new TextField();
    private TextField serialNr = new TextField();
    private BigDecimalField purchasePrice = new BigDecimalField();
    private BigDecimalField salesPrice = new BigDecimalField();
    
    private ComboBox<Customer> customer = new ComboBox<Customer>();


    // TODO: Refactore these buttons in a separate (abstract) form class
    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<Device> binder;

    private Device currentDevice = new Device();

    private DeviceDataService deviceService;

    public DeviceView() {
        setId("my-device-manager-view");
        this.deviceService = DeviceDataService.getInstance();
        // Configure Grid
        grid = new Grid<>(Device.class);
        grid.setColumns("name", "artNr", "serialNr", "purchasePrice", "salesPrice");
        grid.setDataProvider(new DeviceDataProvider());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
//              Device deviceFromBackend = deviceService.getById(event.getValue().getId());
            	Device deviceFromBackend = event.getValue();
                // when a row is selected but the data is no longer available, refresh grid
                if (deviceFromBackend != null) {
                    populateForm(deviceFromBackend	);
                } else {
                    refreshGrid();
                }
            } else {
                clearForm();
            }
        });

        // Configure Form
        binder = new Binder<>(Device.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.currentDevice == null) {
                    this.currentDevice = new Device();
                }
                binder.writeBean(this.currentDevice);
                this.currentDevice = deviceService.update(this.currentDevice);
                clearForm();
                refreshGrid();
                Notification.show("Device details stored.");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the device details.");
            }
        });
        
        // add users to combobox user
        customer.setItems(CustomerDataService.getInstance().getAll());
        
        customer.addValueChangeListener(event -> {
        	if (event.isFromClient() && event.getValue()!=null) {
        		event.getValue().addDevice(this.currentDevice);
        		CustomerDataService.getInstance().save(event.getValue());
        		this.currentDevice.setCustomer(event.getValue());
        		try {
					binder.writeBean(this.currentDevice);
				} catch (ValidationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                this.currentDevice = deviceService.update(this.currentDevice);
        	}
        });
        

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

        FormLayout formLayout = new FormLayout();
        addFormItem(editorDiv, formLayout, name, "Device name");
        addFormItem(editorDiv, formLayout, artNr, "Article number");
        addFormItem(editorDiv, formLayout, serialNr, "Serial number");
        addFormItem(editorDiv, formLayout, purchasePrice, "Purchase price");
        addFormItem(editorDiv, formLayout, salesPrice, "Sales price");
        addFormItem(editorDiv, formLayout, customer, "Customer");
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
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

    private void populateForm(Device value) {
        this.currentDevice = value;
        binder.readBean(this.currentDevice);
    }
}
