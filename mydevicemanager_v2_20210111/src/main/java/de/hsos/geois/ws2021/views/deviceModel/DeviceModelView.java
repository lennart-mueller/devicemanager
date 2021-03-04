package de.hsos.geois.ws2021.views.deviceModel;

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

import de.hsos.geois.ws2021.data.entity.DeviceModel;
import de.hsos.geois.ws2021.data.entity.Producer;
import de.hsos.geois.ws2021.data.service.DeviceModelDataService;
import de.hsos.geois.ws2021.data.service.ProducerDataService;
import de.hsos.geois.ws2021.views.MainView;

@Route(value = "deviceModel", layout = MainView.class)
@PageTitle("MyDeviceManager")
@CssImport("./styles/views/mydevicemanager/my-device-manager-view.css")
@RouteAlias(value = "deviceModel", layout = MainView.class)
public class DeviceModelView extends Div {
	
	private static final long serialVersionUID = -1759199493485879653L;

	private Grid<DeviceModel> grid;

    private TextField name = new TextField();
    private TextField artNr = new TextField();
    private BigDecimalField purchasePrice = new BigDecimalField();
    private BigDecimalField salesPrice = new BigDecimalField();
    
    private ComboBox<Producer> producer = new ComboBox<Producer>();


    // TODO: Refactore these buttons in a separate (abstract) form class
    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<DeviceModel> binder;

    private DeviceModel currentDeviceModel = new DeviceModel();

    private DeviceModelDataService deviceModelService;

    public DeviceModelView() {
        setId("my-device-manager-view");
        this.deviceModelService = DeviceModelDataService.getInstance();
        // Configure Grid
        grid = new Grid<>(DeviceModel.class);
        grid.setColumns("name", "artNr", "purchasePrice", "salesPrice");
        grid.setDataProvider(new DeviceModelDataProvider());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
//              Device deviceFromBackend = deviceService.getById(event.getValue().getId());
            	DeviceModel deviceModelFromBackend = event.getValue();
                // when a row is selected but the data is no longer available, refresh grid
                if (deviceModelFromBackend != null) {
                    populateForm(deviceModelFromBackend	);
                } else {
                    refreshGrid();
                }
            } else {
                clearForm();
            }
        });

        // Configure Form
        binder = new Binder<>(DeviceModel.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.currentDeviceModel == null) {
                    this.currentDeviceModel = new DeviceModel();
                }
                binder.writeBean(this.currentDeviceModel);
                this.currentDeviceModel = deviceModelService.update(this.currentDeviceModel);
                clearForm();
                refreshGrid();
                Notification.show("Device model details stored.");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the device model details.");
            }
        });
        
        // add users to combobox user
        producer.setItems(ProducerDataService.getInstance().getAll());
        
        producer.addValueChangeListener(event -> {
        	if (event.isFromClient() && event.getValue()!=null) {
        		event.getValue().addDeviceModel(this.currentDeviceModel);
        		ProducerDataService.getInstance().save(event.getValue());
        		this.currentDeviceModel.setProducer(event.getValue());
        		try {
					binder.writeBean(this.currentDeviceModel);
				} catch (ValidationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                this.currentDeviceModel = deviceModelService.update(this.currentDeviceModel);
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
        addFormItem(editorDiv, formLayout, producer, "Producer");
        addFormItem(editorDiv, formLayout, name, "Device Model name");
        addFormItem(editorDiv, formLayout, artNr, "Article number");
        addFormItem(editorDiv, formLayout, purchasePrice, "Purchase price");
        addFormItem(editorDiv, formLayout, salesPrice, "Sales price");
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

    private void populateForm(DeviceModel value) {
        this.currentDeviceModel = value;
        binder.readBean(this.currentDeviceModel);
    }
}
