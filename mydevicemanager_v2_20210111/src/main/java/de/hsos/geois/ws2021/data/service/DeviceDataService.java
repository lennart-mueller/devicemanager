package de.hsos.geois.ws2021.data.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import de.hsos.geois.ws2021.data.EntityManagerHandler;
import de.hsos.geois.ws2021.data.entity.Customer;
import de.hsos.geois.ws2021.data.entity.Device;

@Service
public class DeviceDataService extends DataService<Device> {

	private static final long serialVersionUID = 4391509922505113557L;

	private static DeviceDataService INSTANCE;
    
    public static final String SORT_ON_ID = "d.id";
	public static final String SORT_ON_NAME = "d.name";
	public static final String SORT_ON_ARTNR = "d.artNr";
	public static final String SORT_ON_SERIALNR = "d.serialNr";
	
	private DeviceDataService() {
		super();
	}

	/**
	 * @return a reference to an example facade for Device objects.
	 */
	public static DeviceDataService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DeviceDataService();
		}
		return INSTANCE;
	}
	
	@Override
	public Device newEntity() {
		return new Device();
	}

	@Override
	protected String getByIdQuery() {
		return "SELECT d FROM Device d WHERE d.id = :id";
	}

	@Override
	protected String getAllQuery() {
		return "SELECT d FROM Device d ORDER BY d.name";
	}

	@Override
	protected Class<Device> getEntityClass() {
		return Device.class;
	}
	
	public int countDevices(String filter) {
		String queryString = "SELECT count(d) FROM Device d WHERE (CONCAT(d.id, '') LIKE :filter "
				+ "OR LOWER(d.name) LIKE :filter "
				+ "OR LOWER(d.artNr) LIKE :filter "
				+ "OR LOWER(d.serialNr) LIKE :filter)";
		return super.count(queryString, filter);
	}
	
	public Collection<Device> fetchDevices(String filter, int limit, int offset, List<QuerySortOrder> sortOrders) {
		
		final String preparedFilter = prepareFilter(filter);
		
		// By default sort on name
		if (sortOrders == null || sortOrders.isEmpty()) {
			sortOrders = new ArrayList<>();
		    sortOrders.add(new QuerySortOrder(SORT_ON_NAME, SortDirection.ASCENDING));
		}
		
		String sortString = getSortingString(sortOrders);
		
		String queryString = "SELECT d FROM Device d WHERE (CONCAT(d.id, '') LIKE :filter "
				+ "OR LOWER(d.name) LIKE :filter "
				+ "OR LOWER(d.artNr) LIKE :filter "
				+ "OR LOWER(d.serialNr) LIKE :filter)" + sortString;
		
		return EntityManagerHandler.runInTransaction(em -> em.createQuery(queryString, Device.class)
				 .setParameter("filter", preparedFilter)
				 .setFirstResult(offset)
			     .setMaxResults(limit)
				 .getResultList());
	}

	public Collection<Device> getDevicesOfCustomer(Customer customer) {
		return EntityManagerHandler.runInTransaction(em -> em.createQuery("SELECT d FROM Device d WHERE d.customer = :customer ORDER BY d.name", Device.class)
				.setParameter("customer", customer)
				.getResultList());
	}
}
