package de.hsos.geois.ws2021.views.upload.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
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
import de.hsos.geois.ws2021.data.entity.Producer;
import de.hsos.geois.ws2021.data.entity.User;
import de.hsos.geois.ws2021.data.service.CustomerDataService;
import de.hsos.geois.ws2021.data.service.DeviceDataService;
import de.hsos.geois.ws2021.data.service.DeviceModelDataService;
import de.hsos.geois.ws2021.data.service.ProducerDataService;
import de.hsos.geois.ws2021.data.service.UserDataService;
import de.hsos.geois.ws2021.views.MainView;

@Route(value = "upload-data", layout = MainView.class)
@PageTitle("MyDeviceManager")
@CssImport("./styles/views/mydevicemanager/my-device-manager-view.css")
@RouteAlias(value = "upload-data", layout = MainView.class)
public class UploadDataView extends Div {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4135719129089091503L;

	private MemoryBuffer bufferDevice;
	private MemoryBuffer bufferDeviceModel;
	private MemoryBuffer bufferProducer;
	private MemoryBuffer bufferCustomer;
	private MemoryBuffer bufferUser;

	private Upload uploadDevice;
	private Upload uploadDeviceModel;
	private Upload uploadProducer;
	private Upload uploadCustomer;
	private Upload uploadUser;

	private Binder<Device> binderDevice;
	private Binder<DeviceModel> binderDeviceModel;
	private Binder<Producer> binderProducer;
	private Binder<Customer> binderCustomer;
	private Binder<User> binderUser;

	private Device currentDevice = new Device();
	private DeviceModel currentDeviceModel = new DeviceModel();
	private Producer currentProducer = new Producer();
	private Customer currentCustomer = new Customer();
	private User currentUser = new User();

	private Collection<DeviceModel> colDeviceModels = new ArrayList<DeviceModel>();

	private DeviceDataService deviceService;
	private DeviceModelDataService deviceModelDataService;
	private ProducerDataService producerDataService;
	private CustomerDataService customerDataService;
	private UserDataService userDataService;

	public UploadDataView() {
		setId("my-device-manager-view");

		// Device
		Div outputDevice = new Div();
		this.deviceService = DeviceDataService.getInstance();
		binderDevice = new Binder<>(Device.class);
		MemoryBuffer bufferDevice = new MemoryBuffer();
		Upload uploadDevice = new Upload(bufferDevice);
		uploadDevice.setDropLabel(new Label("Upload files in .csv format for Data Device"));
		//uploadDevice.setMaxFiles(1);
		//uploadDevice.setMaxFileSize(300);
		uploadDevice.addSucceededListener(event -> {
			readDevices(bufferDevice.getInputStream());
		});
		add(uploadDevice, outputDevice);

		//Device Model
		Div outputDeviceModel = new Div();
		this.deviceModelDataService = DeviceModelDataService.getInstance();
		binderDeviceModel = new Binder<>(DeviceModel.class);
		MemoryBuffer bufferDeviceModel = new MemoryBuffer();
		Upload uploadDeviceModel = new Upload(bufferDeviceModel);
		uploadDeviceModel.setDropLabel(new Label("Upload files in .csv format for Data Device Model (WIP)"));
		uploadDeviceModel.addSucceededListener(event -> {	
		//				TODO: readDeviceModel
			//readDevices(bufferDeviceModel.getInputStream());
		});
		add(uploadDeviceModel, outputDeviceModel);
		
		//Producer
		Div outputProducer = new Div();
		this.producerDataService = ProducerDataService.getInstance();
		binderProducer = new Binder<>(Producer.class);
		MemoryBuffer bufferProducer = new MemoryBuffer();
		Upload uploadProducer = new Upload(bufferProducer);
		uploadProducer.setDropLabel(new Label("Upload files in .csv format for Data Producer (WIP)"));
		uploadProducer.addSucceededListener(event -> {
		//		TODO: readProducer
			//readDevices(bufferProducer.getInputStream());
		});
		add(uploadProducer, outputProducer);
		
		//Customer
		Div outputCustomer = new Div();
		this.customerDataService = CustomerDataService.getInstance();
		binderCustomer = new Binder<>(Customer.class);
		MemoryBuffer bufferCustomer = new MemoryBuffer();
		Upload uploadCustomer = new Upload(bufferCustomer);
		uploadCustomer.setDropLabel(new Label("Upload files in .csv format for Data Customer (WIP)"));
		uploadCustomer.addSucceededListener(event -> {	
		//		TODO: readCustomer
			//readDevices(bufferCustomer.getInputStream());
		});
		add(uploadCustomer, outputCustomer);
		
		//User
		Div outputUser = new Div();
		this.userDataService = UserDataService.getInstance();
		binderUser = new Binder<>(User.class);
		MemoryBuffer bufferUser = new MemoryBuffer();
		Upload uploadUser = new Upload(bufferUser);
		uploadUser.setDropLabel(new Label("Upload files in .csv format for Data User (WIP)"));
		uploadUser.addSucceededListener(event -> {	
		//		TODO: readUser
			//readDevices(bufferUser.getInputStream());
		});
		add(uploadUser, outputUser);

	}

	/**
	 * Reads Device data from a csv file and on success adds the Devices to the data records
	 * @param stream
	 */
	private void readDevices(InputStream stream) {

		InputStreamReader isReader = new InputStreamReader(stream);
		BufferedReader reader = new BufferedReader(isReader);
		colDeviceModels.addAll(DeviceModelDataService.getInstance().getAll());

		try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);) {
			currentDevice = new Device();
			for (CSVRecord csvRecord : csvParser) {
				currentDeviceModel = DeviceModelDataService.getInstance().getDeviceModelByArtNr(csvRecord.get(0));
				if (currentDeviceModel == null) {
					Notification.show("Reading Data failed!");
					return;
				}
				currentDevice.setDeviceModel(currentDeviceModel);
				currentDevice.setSerialNr(csvRecord.get(1));
				try {
					binderDevice.writeBean(this.currentDevice);
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
		Notification.show("Reading Data was Successfull!");
	}
}
