package de.hsos.geois.ws2021.views.device;

import java.util.stream.Stream;

import com.vaadin.flow.data.provider.Query;

import de.hsos.geois.ws2021.data.entity.Device;
import de.hsos.geois.ws2021.data.service.DataService;
import de.hsos.geois.ws2021.data.service.DeviceDataService;
import de.hsos.geois.ws2021.views.AbstractDataProvider;

public class DeviceDataProvider extends AbstractDataProvider<Device> {
    
	private static final long serialVersionUID = -110064154580521089L;

	@Override
	protected String getFilterBase(Device entity) {
		return entity.toString();
	}

	@Override
	protected DataService<Device> getDataService() {
		return DeviceDataService.getInstance();
	}

	@Override
	protected Stream<Device> fetchFromBackEnd(Query<Device, String> query) {
		return DeviceDataService.getInstance().fetchDevices(this.filterText, query.getLimit(), query.getOffset(),
		        query.getSortOrders()).stream();
	}

	@Override
	protected int sizeInBackEnd(Query<Device, String> query) {
		return DeviceDataService.getInstance().countDevices(this.filterText);
	}
}
