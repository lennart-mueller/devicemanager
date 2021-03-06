package de.hsos.geois.ws2021.data.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import de.hsos.geois.ws2021.data.EntityManagerHandler;
import de.hsos.geois.ws2021.data.entity.OrderPosition;

@Service
public class OrderPositionDataService extends DataService<OrderPosition> {
	
	private static final long serialVersionUID = -2172704833718243700L;

	private static OrderPositionDataService INSTANCE;
    
    public static final String SORT_ON_ID = "op.id";
	public static final String SORT_ON_DEVICEMODEL = "do.deviceModel";
	public static final String SORT_ON_QUANTITY = "do.quantity";
	
	private OrderPositionDataService() {
		super();
	}

	/**
	 * @return a reference to an example facade for Device objects.
	 */
	public static OrderPositionDataService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new OrderPositionDataService();
		}
		return INSTANCE;
	}
	
	@Override
	public OrderPosition newEntity() {
		return new OrderPosition();
	}

	@Override
	protected String getByIdQuery() {
		return "SELECT op FROM OrderPosition op WHERE op.id = :id";
	}

	@Override
	protected String getAllQuery() {
		return "SELECT op FROM OrderPosition op ORDER BY op.id";
	}

	@Override
	protected Class<OrderPosition> getEntityClass() {
		return OrderPosition.class;
	}
	
	public int countOrderPositions(String filter) {
		String queryString = "SELECT count(op) FROM OrderPosition op WHERE (CONCAT(op.id, '') LIKE :filter "
				+ "OR LOWER(op.deviceModel) LIKE :filter)";
		return super.count(queryString, filter);
	}
	
	public Collection<OrderPosition> fetchOrderPositions(String filter, int limit, int offset, List<QuerySortOrder> sortOrders) {
		
		final String preparedFilter = prepareFilter(filter);
		
		// By default sort on name
		if (sortOrders == null || sortOrders.isEmpty()) {
			sortOrders = new ArrayList<>();
		    sortOrders.add(new QuerySortOrder(SORT_ON_ID, SortDirection.ASCENDING));
		}
		
		String sortString = getSortingString(sortOrders);
		
		String queryString = "SELECT op FROM OrderPosition op WHERE (CONCAT(op.id, '') LIKE :filter "
				+ "OR LOWER(op.deviceModel) LIKE :filter)" + sortString;
		
		return EntityManagerHandler.runInTransaction(em -> em.createQuery(queryString, OrderPosition.class)
				 .setParameter("filter", preparedFilter)
				 .setFirstResult(offset)
			     .setMaxResults(limit)
				 .getResultList());
	}

	public Collection<OrderPosition> getOrderPositionsOfDeviceOrder(String deviceOrder) {
		return EntityManagerHandler.runInTransaction(em -> em.createQuery("SELECT op FROM OrderPosition op WHERE op.deviceOrder = :deviceOrder ORDER BY op.id", OrderPosition.class)
				.setParameter("producer", deviceOrder)
				.getResultList());
	}
}
