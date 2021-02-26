package de.hsos.geois.ws2021.data.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import de.hsos.geois.ws2021.data.EntityManagerHandler;
import de.hsos.geois.ws2021.data.entity.Customer;

@Service
public class CustomerDataService extends DataService<Customer> {

	private static final long serialVersionUID = 5072749097789090163L;

	private static CustomerDataService INSTANCE;
    
    public static final String SORT_ON_ID = "c.id";
	public static final String SORT_ON_NAME = "c.companyName";
	
	private CustomerDataService() {
		super();
	}

	/**
	 * @return a reference to an example facade for Person objects.
	 */
	public static CustomerDataService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CustomerDataService();
		}
		return INSTANCE;
	}
	
	@Override
	public Customer newEntity() {
		return new Customer();
	}

	@Override
	protected String getByIdQuery() {
		return "SELECT c FROM Customer c WHERE c.id = :id";
	}

	@Override
	protected String getAllQuery() {
		return "SELECT c FROM Customer c ORDER BY c.companyName";
	}

	@Override
	protected Class<Customer> getEntityClass() {
		return Customer.class;
	}
	
	public int countCustomers(String filter) {
		String queryString = "SELECT count(c) FROM Customer c WHERE (CONCAT(c.id, '') LIKE :filter "
				+ "OR LOWER(c.companyName) LIKE :filter "
				+ "OR LOWER(c.firstName) LIKE :filter "
				+ "OR LOWER(c.lastName) LIKE :filter)";
		return super.count(queryString, filter);
	}
	
	public Collection<Customer> fetchCustomers(String filter, int limit, int offset, List<QuerySortOrder> sortOrders) {
		
		final String preparedFilter = prepareFilter(filter);
		
		// By default sort on StartDate
		if (sortOrders == null || sortOrders.isEmpty()) {
			sortOrders = new ArrayList<>();
		    sortOrders.add(new QuerySortOrder(SORT_ON_NAME, SortDirection.DESCENDING));
		}
		
		String sortString = getSortingString(sortOrders);
		
		String queryString = "SELECT c FROM Customer c WHERE (CONCAT(c.id, '') LIKE :filter "	
				+ "OR LOWER(c.companyName) LIKE :filter "
				+ "OR LOWER(c.firstName) LIKE :filter "
				+ "OR LOWER(c.lastName) LIKE :filter)" + sortString;
		
		return EntityManagerHandler.runInTransaction(em -> em.createQuery(queryString, Customer.class)
				 .setParameter("filter", preparedFilter)
				 .setFirstResult(offset)
			     .setMaxResults(limit)
				 .getResultList());
	}



}
