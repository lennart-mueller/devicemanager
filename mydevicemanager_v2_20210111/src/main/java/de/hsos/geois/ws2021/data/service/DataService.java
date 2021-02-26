package de.hsos.geois.ws2021.data.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Query;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import de.hsos.geois.ws2021.data.AbstractEntity;
import de.hsos.geois.ws2021.data.EntityManagerHandler;

public abstract class DataService<T extends AbstractEntity> implements Serializable {

	private static final long serialVersionUID = 5700629335575254342L;
	private static final Logger LOGGER = Logger.getLogger(DataService.class.getName());

	public DataService() {	}
	
	public T getById(long id) {
		List<T> entities = EntityManagerHandler.runInTransaction(em -> em.createQuery(getByIdQuery(), getEntityClass()).setParameter("id", id).getResultList());
		if (!entities.isEmpty()) {
			return entities.get(0);
		}
		return null;
	}

	public Collection<T> get(int limit, int offset) {
		String queryString = getAllQuery(); // must be invoked separately, since in case of some EntityTypes an additional transaction is started to get the currentUser.
		return EntityManagerHandler.runInTransaction(em -> em.createQuery(queryString, getEntityClass())
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList());
	}
	
	public void save(T t) {
		boolean isManaged = t.getId()!=null;
		if (isManaged){
			update(t);
		} else { // new Entity
			add(t);
		}
	}

	/**
	 * Adds the entity to the system. Also assigns an identifier to the entity.
	 *
	 * @param entity to be added
	 */
	public synchronized T add(T t) {
		if (t == null) {
			LOGGER.log(Level.SEVERE, "Entity to be added is null");
			return t;
		}
		EntityManagerHandler.runInTransaction(em -> {
			em.persist(t);
			//em.flush();
			return null;
		});
		return t;
	}

	public T update(T t) {
		return EntityManagerHandler.runInTransaction(em -> {
			return em.merge(t);
		});
	}

	public void delete(T t) {
		EntityManagerHandler.runInTransaction(em -> {
			Object managed = em.merge(t);
			em.remove(managed);
			return null;
		});
	}

	public Collection<T> getAll() {
		return get(Integer.MAX_VALUE, 0);
	}

	public abstract T newEntity();

	protected abstract String getByIdQuery();

	protected abstract String getAllQuery();

	protected abstract Class<T> getEntityClass();

	public synchronized int count(String queryString, String filter) {
		filter = prepareFilter(filter);
		Query query = EntityManagerHandler.getEntityManager().createQuery(queryString);
		query.setParameter("filter", filter);
		return count(query);
	}
	
	public synchronized int count(Query query) {	
		if (query.getSingleResult()!=null) {
			int numberOfElements = ((Long)query.getSingleResult()).intValue();
			EntityManagerHandler.closeEntityManager();
			return numberOfElements;
		} else {
			EntityManagerHandler.closeEntityManager();
			return 0;
		}
	}
	
	protected static String prepareFilter(String filter) {
		if (filter == null) {
			filter = "";
		}
		filter = "%" + filter.toLowerCase().trim() + "%";
		return filter;
	}
	
	protected static String getSortingString(List<QuerySortOrder> sortOrders) {
		String sortString = "";
		int i = 0;
		for (QuerySortOrder sortOrder : sortOrders) {
	        String order = getOrder(sortOrder.getDirection());
	        if (i == 0) {
	          sortString = " order by " + sortOrder.getSorted() + " " + order; 
	          i++;
	        } else {
	          sortString = sortString + ", " + sortOrder.getSorted() + " " + order;
	        }
	      }
		return sortString;
	}
	
	private static String getOrder(SortDirection sortDirection) {
	    String order = "asc";
	    if (sortDirection == SortDirection.DESCENDING) {
	      order = "desc";
	    }
	    return order;
	  }
}
