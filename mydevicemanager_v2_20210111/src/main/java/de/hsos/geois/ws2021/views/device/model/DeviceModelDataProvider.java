package de.hsos.geois.ws2021.views.device.model;

import java.util.stream.Stream;

import com.vaadin.flow.data.provider.Query;

import de.hsos.geois.ws2021.data.entity.DeviceModel;
import de.hsos.geois.ws2021.data.service.DataService;
import de.hsos.geois.ws2021.data.service.DeviceModelDataService;
import de.hsos.geois.ws2021.views.AbstractDataProvider;

public class DeviceModelDataProvider extends AbstractDataProvider<DeviceModel> {
    
	private static final long serialVersionUID = -110064154580521089L;

	@Override
	protected String getFilterBase(DeviceModel entity) {
		return entity.toString();
	}

	@Override
	protected DataService<DeviceModel> getDataService() {
		return DeviceModelDataService.getInstance();
	}

	@Override
	protected Stream<DeviceModel> fetchFromBackEnd(Query<DeviceModel, String> query) {
		return DeviceModelDataService.getInstance().fetchDeviceModels(this.filterText, query.getLimit(), query.getOffset(),
		        query.getSortOrders()).stream();
	}

	@Override
	protected int sizeInBackEnd(Query<DeviceModel, String> query) {
		return DeviceModelDataService.getInstance().countDeviceModels(this.filterText);
	}
}
