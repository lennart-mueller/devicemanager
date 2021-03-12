package de.hsos.geois.ws2021.views.producer;

import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import de.hsos.geois.ws2021.data.entity.DeviceModel;
import de.hsos.geois.ws2021.data.entity.Producer;
import de.hsos.geois.ws2021.data.service.DeviceModelDataService;
import de.hsos.geois.ws2021.data.service.ProducerDataService;
import de.hsos.geois.ws2021.views.MainView;

@Route(value = "producer", layout = MainView.class)
@PageTitle("MyDeviceManager")
@CssImport("./styles/views/mydevicemanager/my-device-manager-view.css")
@RouteAlias(value = "producer", layout = MainView.class)
public class ProducerView extends Div {
	
	private static final long serialVersionUID = -3436063662854717622L;

	private Grid<Producer> grid;
	// producer information
	private TextField companyName = new TextField();
	private TextField salutation = new TextField();
    private TextField firstName = new TextField();
    private TextField lastName = new TextField();
    private TextField email = new TextField();
    private TextField phone = new TextField();
    
    private TextField addressSecondLine = new TextField();
    private TextField streetAndNr = new TextField();
    private TextField place = new TextField();
    private TextField zipCode = new TextField();
    
    private Grid<DeviceModel> deviceModelGrid = new Grid<DeviceModel>();

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder <Producer> binder;

    private Producer producer = new Producer();

    private ProducerDataService producerService;

    public ProducerView() {
        setId("my-device-manager-view");
        this.producerService = ProducerDataService.getInstance();
        // Configure Grid
        grid = new Grid<>(Producer.class);
        grid.setColumns("companyName", "salutation", "firstName", "lastName", "email", "phone", "place");
        grid.setDataProvider(new ProducerDataProvider());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
            	Producer producerFromBackend = producerService.getById(event.getValue().getId());
                Collection<DeviceModel> deviceModels =  DeviceModelDataService.getInstance().getDeviceModelsOfProducer(producerFromBackend);
                System.out.println("Beim Laden des ProducerFromBackend " + deviceModels.size());
                deviceModelGrid.setItems(deviceModels);
                
                // when a row is selected but the data is no longer available, refresh grid
                if (producerFromBackend != null) {
                    populateForm(producerFromBackend);
                } else {
                    refreshGrid();
                }
            } else {
                clearForm();
            }
        });

        // Configure Form
        binder = new Binder<>(Producer.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.producer == null) {
                    this.producer = new Producer();
                }
                binder.writeBean(this.producer);
                this.producer = producerService.update(this.producer);
                clearForm();
                refreshGrid();
                Notification.show("Producer details stored.");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the producer details.");
            }
        });
       

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
    }

    /**
     * Creates the ProducerForm
     * @param splitLayout
     */
    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setId("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setId("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        addFormItem(editorDiv, formLayout, companyName, "Company name");
        addFormItem(editorDiv, formLayout, salutation, "Salutation");
        addFormItem(editorDiv, formLayout, firstName, "First name");
        addFormItem(editorDiv, formLayout, lastName, "Last name");
        addFormItem(editorDiv, formLayout, email, "Email");
        addFormItem(editorDiv, formLayout, phone, "Phone");
        addFormItem(editorDiv, formLayout, addressSecondLine, "Address 2nd line");
        addFormItem(editorDiv, formLayout, streetAndNr, "Street and Nr");
        addFormItem(editorDiv, formLayout, zipCode, "Zipcode");
        addFormItem(editorDiv, formLayout, place, "Place");
        
        // add grid
        deviceModelGrid.addColumn(DeviceModel::getArtNr).setHeader("ArtNr");
        deviceModelGrid.addColumn(DeviceModel::getName).setHeader("Name");
        deviceModelGrid.addColumn(DeviceModel::getPurchasePrice).setHeader("PurchasePrice");
        deviceModelGrid.addColumn(
        	    new NativeButtonRenderer<>("Remove Device Model",
        	       clickedDeviceModel -> {
        	           this.producer.removeDeviceModel(clickedDeviceModel);
        	           clickedDeviceModel.setProducer(null);
					   // persist producer
        	           try {
							binder.writeBean(this.producer);
							this.producer = producerService.update(this.producer);
						} catch (ValidationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					   // persist clickedDevice
        	           DeviceModelDataService.getInstance().save(clickedDeviceModel);
        	           populateForm(this.producer);
        	    })
        	);
        deviceModelGrid.setWidthFull();
        
        formLayout.add(deviceModelGrid);
        
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

    private void populateForm(Producer customer) {
    	this.producer = customer;
    	binder.readBean(this.producer);
    	if (customer!=null) {
    		binder.bindInstanceFields(this);
    	} else {
    		deviceModelGrid.setItems(new ArrayList<DeviceModel>());
    	}
    }
}
