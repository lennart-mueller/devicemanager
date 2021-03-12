package de.hsos.geois.ws2021.data.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import de.hsos.geois.ws2021.data.EntityManagerHandler;
import de.hsos.geois.ws2021.data.entity.Customer;
import de.hsos.geois.ws2021.data.entity.Producer;

@Service
public class ProducerDataService extends DataService<Producer> {

	private static final long serialVersionUID = 5072749097789090163L;

	private static ProducerDataService INSTANCE;
    
    public static final String SORT_ON_ID = "p.id";
	public static final String SORT_ON_NAME = "p.companyName";
	
	private ProducerDataService() {
		super();
	}

	/**
	 * @return a reference to an example facade for Producer objects.
	 */
	public static ProducerDataService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ProducerDataService();
		}
		return INSTANCE;
	}
	
	@Override
	public Producer newEntity() {
		return new Producer();
	}

	@Override
	protected String getByIdQuery() {
		return "SELECT p FROM Producer p WHERE p.id = :id";
	}

	@Override
	protected String getAllQuery() {
		return "SELECT p FROM Producer p ORDER BY p.companyName";
	}

	@Override
	protected Class<Producer> getEntityClass() {
		return Producer.class;
	}
	
	/**
	 * 
	 * @param filter
	 * @return number of Producers matching the filter
	 */
	public int countProducers(String filter) {
		String queryString = "SELECT count(p) FROM Producer p WHERE (CONCAT(p.id, '') LIKE :filter "
				+ "OR LOWER(p.companyName) LIKE :filter "
				+ "OR LOWER(p.firstName) LIKE :filter "
				+ "OR LOWER(p.lastName) LIKE :filter)";
		return super.count(queryString, filter);
	}
	
	/**
	 * 
	 * @param filter
	 * @param limit
	 * @param offset
	 * @param sortOrders
	 * @return collection of Producers complying the given parameters
	 */
	public Collection<Producer> fetchProducers(String filter, int limit, int offset, List<QuerySortOrder> sortOrders) {
		
		final String preparedFilter = prepareFilter(filter);
		
		// By default sort on StartDate
		if (sortOrders == null || sortOrders.isEmpty()) {
			sortOrders = new ArrayList<>();
		    sortOrders.add(new QuerySortOrder(SORT_ON_NAME, SortDirection.DESCENDING));
		}
		
		String sortString = getSortingString(sortOrders);
		
		String queryString = "SELECT p FROM Producer p WHERE (CONCAT(p.id, '') LIKE :filter "	
				+ "OR LOWER(p.companyName) LIKE :filter "
				+ "OR LOWER(p.firstName) LIKE :filter "
				+ "OR LOWER(p.lastName) LIKE :filter)" + sortString;
		
		return EntityManagerHandler.runInTransaction(em -> em.createQuery(queryString, Producer.class)
				 .setParameter("filter", preparedFilter)
				 .setFirstResult(offset)
			     .setMaxResults(limit)
				 .getResultList());
	}
}
