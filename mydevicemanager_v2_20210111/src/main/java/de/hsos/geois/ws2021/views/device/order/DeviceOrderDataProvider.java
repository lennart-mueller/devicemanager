package de.hsos.geois.ws2021.views.device.order;

import java.util.stream.Stream;

import com.vaadin.flow.data.provider.Query;

import de.hsos.geois.ws2021.data.entity.Device;
import de.hsos.geois.ws2021.data.entity.DeviceOrder;
import de.hsos.geois.ws2021.data.service.DataService;
import de.hsos.geois.ws2021.data.service.DeviceDataService;
import de.hsos.geois.ws2021.data.service.DeviceOrderDataService;
import de.hsos.geois.ws2021.views.AbstractDataProvider;

public class DeviceOrderDataProvider extends AbstractDataProvider<DeviceOrder> {
    
	private static final long serialVersionUID = -110064154580521089L;

	@Override
	protected String getFilterBase(DeviceOrder entity) {
		return entity.toString();
	}

	@Override
	protected DataService<DeviceOrder> getDataService() {
		return DeviceOrderDataService.getInstance();
	}

	@Override
	protected Stream<DeviceOrder> fetchFromBackEnd(Query<DeviceOrder, String> query) {
		return DeviceOrderDataService.getInstance().fetchDeviceOrders(this.filterText, query.getLimit(), query.getOffset(),
		        query.getSortOrders()).stream();
	}

	@Override
	protected int sizeInBackEnd(Query<DeviceOrder, String> query) {
		return DeviceOrderDataService.getInstance().countDeviceOrders(this.filterText);
	}
}
