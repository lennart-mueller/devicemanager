package de.hsos.geois.ws2021.views.producer;

import java.util.stream.Stream;

import com.vaadin.flow.data.provider.Query;

import de.hsos.geois.ws2021.data.entity.Producer;
import de.hsos.geois.ws2021.data.service.DataService;
import de.hsos.geois.ws2021.data.service.ProducerDataService;
import de.hsos.geois.ws2021.views.AbstractDataProvider;

public class ProducerDataProvider extends AbstractDataProvider<Producer> {
    
	private static final long serialVersionUID = 3099783364239325506L;

	@Override
	protected String getFilterBase(Producer entity) {
		return entity.toString();
	}

	@Override
	protected DataService<Producer> getDataService() {
		return ProducerDataService.getInstance();
	}

	@Override
	protected Stream<Producer> fetchFromBackEnd(Query<Producer, String> query) {
		return ProducerDataService.getInstance().fetchProducers(this.filterText, query.getLimit(), query.getOffset(),
		        query.getSortOrders()).stream();
	}

	@Override
	protected int sizeInBackEnd(Query<Producer, String> query) {
		return ProducerDataService.getInstance().countProducers(this.filterText);
	}
}
