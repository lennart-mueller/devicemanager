package de.hsos.geois.ws2021.views.device.order;

import java.util.stream.Stream;

import com.vaadin.flow.data.provider.Query;

import de.hsos.geois.ws2021.data.entity.Device;
import de.hsos.geois.ws2021.data.entity.DeviceOrder;
import de.hsos.geois.ws2021.data.entity.OrderPosition;
import de.hsos.geois.ws2021.data.service.DataService;
import de.hsos.geois.ws2021.data.service.DeviceDataService;
import de.hsos.geois.ws2021.data.service.DeviceOrderDataService;
import de.hsos.geois.ws2021.data.service.OrderPositionDataService;
import de.hsos.geois.ws2021.views.AbstractDataProvider;

public class OrderPositionDataProvider extends AbstractDataProvider<OrderPosition> {
    
	private static final long serialVersionUID = -110064154580521089L;

	@Override
	protected String getFilterBase(OrderPosition entity) {
		return entity.toString();
	}

	@Override
	protected DataService<OrderPosition> getDataService() {
		return OrderPositionDataService.getInstance();
	}

	@Override
	protected Stream<OrderPosition> fetchFromBackEnd(Query<OrderPosition, String> query) {
		return OrderPositionDataService.getInstance().fetchOrderPositions(this.filterText, query.getLimit(), query.getOffset(),
		        query.getSortOrders()).stream();
	}

	@Override
	protected int sizeInBackEnd(Query<OrderPosition, String> query) {
		return OrderPositionDataService.getInstance().countOrderPositions(this.filterText);
	}
}
