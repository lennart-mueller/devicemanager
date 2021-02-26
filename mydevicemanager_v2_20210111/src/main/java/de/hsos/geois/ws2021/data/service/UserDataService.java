package de.hsos.geois.ws2021.data.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import de.hsos.geois.ws2021.data.EntityManagerHandler;
import de.hsos.geois.ws2021.data.entity.User;

@Service
public class UserDataService extends DataService<User> {

	private static final long serialVersionUID = 5072749097789090163L;

	private static UserDataService INSTANCE;
    
    public static final String SORT_ON_ID = "u.id";
	public static final String SORT_ON_FIRSTNAME = "u.firstName";
	public static final String SORT_ON_LASTNAME = "u.lastName";
	
	private UserDataService() {
		super();
	}

	/**
	 * @return a reference to an example facade for Person objects.
	 */
	public static UserDataService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UserDataService();
		}
		return INSTANCE;
	}
	
	@Override
	public User newEntity() {
		return new User();
	}

	@Override
	protected String getByIdQuery() {
		return "SELECT u FROM User u WHERE u.id = :id";
	}

	@Override
	protected String getAllQuery() {
		return "SELECT u FROM User u ORDER BY u.lastName";
	}

	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}
	
	public int countUsers(String filter) {
		String queryString = "SELECT count(u) FROM User u WHERE (CONCAT(u.id, '') LIKE :filter "
				+ "OR LOWER(u.firstName) LIKE :filter "
				+ "OR LOWER(u.lastName) LIKE :filter)";
		return super.count(queryString, filter);
	}
	
	public Collection<User> fetchUsers(String filter, int limit, int offset, List<QuerySortOrder> sortOrders) {
		
		final String preparedFilter = prepareFilter(filter);
		
		// By default sort on StartDate
		if (sortOrders == null || sortOrders.isEmpty()) {
			sortOrders = new ArrayList<>();
		    sortOrders.add(new QuerySortOrder(SORT_ON_LASTNAME, SortDirection.DESCENDING));
		}
		
		String sortString = getSortingString(sortOrders);
		
		String queryString = "SELECT u FROM User u WHERE (CONCAT(u.id, '') LIKE :filter "
				+ "OR LOWER(u.firstName) LIKE :filter "
				+ "OR LOWER(u.lastName) LIKE :filter)" + sortString;
		
		return EntityManagerHandler.runInTransaction(em -> em.createQuery(queryString, User.class)
				 .setParameter("filter", preparedFilter)
				 .setFirstResult(offset)
			     .setMaxResults(limit)
				 .getResultList());
	}



}
