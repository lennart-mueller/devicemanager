package de.hsos.geois.ws2021.views.user;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import de.hsos.geois.ws2021.data.entity.User;
import de.hsos.geois.ws2021.data.service.UserDataService;
import de.hsos.geois.ws2021.views.MainView;

@Route(value = "user", layout = MainView.class)
@PageTitle("MyDeviceManager")
@CssImport("./styles/views/mydevicemanager/my-device-manager-view.css")
@RouteAlias(value = "", layout = MainView.class)
public class UserView extends Div {

	private static final long serialVersionUID = 4939100739729795870L;

	private Grid<User> grid;

    private TextField firstName = new TextField();
    private TextField lastName = new TextField();
    private TextField email = new TextField();
    private TextField phone = new TextField();
    private DatePicker dateOfBirth = new DatePicker();
    private TextField occupation = new TextField();

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<User> binder;

    private User user = new User();

    private UserDataService personService;

    public UserView() {
        setId("my-device-manager-view");
        this.personService = UserDataService.getInstance();
        // Configure Grid
        grid = new Grid<>(User.class);
        grid.setColumns("firstName", "lastName", "email", "phone", "dateOfBirth", "occupation");
        grid.setDataProvider(new UserDataProvider());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                User personFromBackend = personService.getById(event.getValue().getId());    
                // when a row is selected but the data is no longer available, refresh grid
                if (personFromBackend != null) {
                    populateForm(personFromBackend);
                } else {
                    refreshGrid();
                }
            } else {
                clearForm();
            }
        });

        // Configure Form
        binder = new Binder<>(User.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.user == null) {
                    this.user = new User();
                }
                binder.writeBean(this.user);
                personService.update(this.user);
                clearForm();
                refreshGrid();
                Notification.show("User details stored.");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the user details.");
            }
        });
       

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
    }

    /**
     * Creates the UserForm
     * @param splitLayout
     */
    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setId("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setId("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        addFormItem(editorDiv, formLayout, firstName, "First name");
        addFormItem(editorDiv, formLayout, lastName, "Last name");
        addFormItem(editorDiv, formLayout, email, "Email");
        addFormItem(editorDiv, formLayout, phone, "Phone");
        addFormItem(editorDiv, formLayout, dateOfBirth, "Date of birth");
        addFormItem(editorDiv, formLayout, occupation, "Occupation");

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

    @SuppressWarnings("rawtypes")
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

    private void populateForm(User user) {
    	this.user = user;
    	binder.readBean(this.user);
    }
}
