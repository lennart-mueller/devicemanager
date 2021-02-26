package de.hsos.geois.ws2021.views.customer;

import java.util.stream.Stream;

import com.vaadin.flow.data.provider.Query;

import de.hsos.geois.ws2021.data.entity.Customer;
import de.hsos.geois.ws2021.data.service.DataService;
import de.hsos.geois.ws2021.data.service.CustomerDataService;
import de.hsos.geois.ws2021.views.AbstractDataProvider;

public class CustomerDataProvider extends AbstractDataProvider<Customer> {
    
	private static final long serialVersionUID = 3099783364239325506L;

	@Override
	protected String getFilterBase(Customer entity) {
		return entity.toString();
	}

	@Override
	protected DataService<Customer> getDataService() {
		return CustomerDataService.getInstance();
	}

	@Override
	protected Stream<Customer> fetchFromBackEnd(Query<Customer, String> query) {
		return CustomerDataService.getInstance().fetchCustomers(this.filterText, query.getLimit(), query.getOffset(),
		        query.getSortOrders()).stream();
	}

	@Override
	protected int sizeInBackEnd(Query<Customer, String> query) {
		return CustomerDataService.getInstance().countCustomers(this.filterText);
	}
}
