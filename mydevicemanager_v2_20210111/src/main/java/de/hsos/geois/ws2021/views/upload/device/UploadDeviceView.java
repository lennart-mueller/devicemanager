package de.hsos.geois.ws2021.views.upload.device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import de.hsos.geois.ws2021.data.entity.Customer;
import de.hsos.geois.ws2021.data.entity.Device;
import de.hsos.geois.ws2021.data.entity.DeviceModel;
import de.hsos.geois.ws2021.data.service.DeviceDataService;
import de.hsos.geois.ws2021.data.service.DeviceModelDataService;
import de.hsos.geois.ws2021.views.MainView;

@Route(value = "upload-device", layout = MainView.class)
@PageTitle("MyDeviceManager")
@CssImport("./styles/views/mydevicemanager/my-device-manager-view.css")
@RouteAlias(value = "upload-device", layout = MainView.class)
public class UploadDeviceView extends Div {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4135719129089091503L;

	private Grid<Device> grid;

	private MemoryBuffer buffer;
	private Upload upload;
	private Div divOutput;

	private Binder<Device> binder;

	private Device currentDevice = new Device();
	private DeviceModel currentDeviceModel = new DeviceModel();
	private Collection<DeviceModel> colDeviceModels = new ArrayList<DeviceModel>();

	private DeviceDataService deviceService;

	public UploadDeviceView() {
		setId("my-device-manager-view");
        this.deviceService = DeviceDataService.getInstance();

		MemoryBuffer buffer = new MemoryBuffer();
		Upload upload = new Upload(buffer);
		Div output = new Div();
		
        // Configure Form
        binder = new Binder<>(Device.class);

//        // Bind fields. This where you'd define e.g. validation rules
//        binder.bindInstanceFields(this);

		upload.addSucceededListener(event -> {

			Component component = createTextComponent(buffer.getInputStream());
			
			createDeviceReader(buffer.getInputStream());
			
			showOutput(event.getFileName(), component, output);
		});

		add(upload, output);
	}

	private Component createTextComponent(InputStream stream) {
		String text;
		try {
			text = IOUtils.toString(stream, StandardCharsets.UTF_8);
		} catch (IOException e) {
			text = "exception reading stream";
		}
		return new Text(text);
	}

	private void showOutput(String text, Component content, HasComponents outputContainer) {
		HtmlComponent p = new HtmlComponent(Tag.P);
		p.getElement().setText(text);
		outputContainer.add(p);
		outputContainer.add(content);
	}

	private void createDeviceReader(InputStream stream) {

		InputStreamReader isReader = new InputStreamReader(stream);
		BufferedReader reader = new BufferedReader(isReader);
		colDeviceModels.addAll(DeviceModelDataService.getInstance().getAll());

		try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);) {
				currentDevice = new Device();
			for (CSVRecord csvRecord : csvParser) {
				currentDeviceModel = DeviceModelDataService.getInstance().getDeviceModelByArtNr(csvRecord.get(0));
//				currentDeviceModel = findDeviceModelByArtNrString(csvRecord.get(0), colDeviceModels);
				currentDevice.setDeviceModel(currentDeviceModel);
				currentDevice.setSerialNr(csvRecord.get(1));
				Notification.show(currentDevice.toString() + "(" + csvRecord.get(0) + " : " + csvRecord.get(1) + ")");
				try {
					binder.writeBean(this.currentDevice);
				} catch (ValidationException e1) {
					Notification.show(e1.toString());
					e1.printStackTrace();
				}
				deviceService.update(currentDevice);
			}

		} catch (IOException e) {
			Notification.show(e.toString());
			e.printStackTrace();
		}

	}
}
