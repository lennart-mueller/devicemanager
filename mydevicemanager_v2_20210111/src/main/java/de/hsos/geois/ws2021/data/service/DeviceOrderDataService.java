package de.hsos.geois.ws2021.data.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import de.hsos.geois.ws2021.data.EntityManagerHandler;
import de.hsos.geois.ws2021.data.entity.DeviceOrder;

@Service
public class DeviceOrderDataService extends DataService<DeviceOrder> {

	private static final long serialVersionUID = -379955969301984768L;

	private static DeviceOrderDataService INSTANCE;
    
    public static final String SORT_ON_ID = "do.id";
	public static final String SORT_ON_ORDERDATE = "do.orderDate";
	public static final String SORT_ON_DELIVERYDATE = "do.deliveryDate";
	
	private DeviceOrderDataService() {
		super();
	}

	/**
	 * @return a reference to an example facade for DeviceOrder objects.
	 */
	public static DeviceOrderDataService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DeviceOrderDataService();
		}
		return INSTANCE;
	}
	
	@Override
	public DeviceOrder newEntity() {
		return new DeviceOrder();
	}

	@Override
	protected String getByIdQuery() {
		return "SELECT do FROM DeviceOrder do WHERE do.id = :id";
	}

	@Override
	protected String getAllQuery() {
		return "SELECT do FROM DeviceOrder do ORDER BY do.orderDate";
	}

	@Override
	protected Class<DeviceOrder> getEntityClass() {
		return DeviceOrder.class;
	}
	
	/**
	 * 
	 * @param filter
	 * @return number of DeviceOrders matching the filter
	 */
	public int countDeviceOrders(String filter) {
		String queryString = "SELECT count(do) FROM DeviceOrder do WHERE (CONCAT(do.id, '') LIKE :filter "
				+ "OR LOWER(do.orderDate) LIKE :filter)";
		return super.count(queryString, filter);
	}
	
	/**
	 * 
	 * @param filter
	 * @param limit
	 * @param offset
	 * @param sortOrders
	 * @return collection of DeviceOrders complying the given parameters
	 */
	public Collection<DeviceOrder> fetchDeviceOrders(String filter, int limit, int offset, List<QuerySortOrder> sortOrders) {
		
		final String preparedFilter = prepareFilter(filter);
		
		// By default sort on name
		if (sortOrders == null || sortOrders.isEmpty()) {
			sortOrders = new ArrayList<>();
		    sortOrders.add(new QuerySortOrder(SORT_ON_ID, SortDirection.ASCENDING));
		}
		
		String sortString = getSortingString(sortOrders);
		
		String queryString = "SELECT do FROM DeviceOrder do WHERE (CONCAT(do.id, '') LIKE :filter "
				+ "OR LOWER(do.orderDate) LIKE :filter)" + sortString;
		
		return EntityManagerHandler.runInTransaction(em -> em.createQuery(queryString, DeviceOrder.class)
				 .setParameter("filter", preparedFilter)
				 .setFirstResult(offset)
			     .setMaxResults(limit)
				 .getResultList());
	}
}
