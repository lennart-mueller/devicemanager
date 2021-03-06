package de.hsos.geois.ws2021.data.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import de.hsos.geois.ws2021.data.EntityManagerHandler;
import de.hsos.geois.ws2021.data.entity.DeviceModel;
import de.hsos.geois.ws2021.data.entity.Producer;

@Service
public class DeviceModelDataService extends DataService<DeviceModel> {
	
	private static final long serialVersionUID = 9110254718971222783L;

	private static DeviceModelDataService INSTANCE;
    
    public static final String SORT_ON_ID = "dm.id";
	public static final String SORT_ON_NAME = "dm.name";
	public static final String SORT_ON_ARTNR = "dm.artNr";
	
	private DeviceModelDataService() {
		super();
	}

	/**
	 * @return a reference to an example facade for DeviceModel objects.
	 */
	public static DeviceModelDataService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DeviceModelDataService();
		}
		return INSTANCE;
	}
	
	@Override
	public DeviceModel newEntity() {
		return new DeviceModel();
	}

	@Override
	protected String getByIdQuery() {
		return "SELECT dm FROM DeviceModel dm WHERE dm.id = :id";
	}

	@Override
	protected String getAllQuery() {
		return "SELECT dm FROM DeviceModel dm ORDER BY dm.name";
	}

	@Override
	protected Class<DeviceModel> getEntityClass() {
		return DeviceModel.class;
	}
	
	/**
	 * 
	 * @param filter
	 * @return number of DeviceModels matching the filter
	 */
	public int countDeviceModels(String filter) {
		String queryString = "SELECT count(dm) FROM DeviceModel dm WHERE (CONCAT(dm.id, '') LIKE :filter "
				+ "OR LOWER(dm.name) LIKE :filter "
				+ "OR LOWER(dm.artNr) LIKE :filter)";
		return super.count(queryString, filter);
	}
	
	/**
	 * 
	 * @param filter
	 * @param limit
	 * @param offset
	 * @param sortOrders
	 * @return collection of DeviceModels complying the given parameters
	 */
	public Collection<DeviceModel> fetchDeviceModels(String filter, int limit, int offset, List<QuerySortOrder> sortOrders) {
		
		final String preparedFilter = prepareFilter(filter);
		
		// By default sort on name
		if (sortOrders == null || sortOrders.isEmpty()) {
			sortOrders = new ArrayList<>();
		    sortOrders.add(new QuerySortOrder(SORT_ON_NAME, SortDirection.ASCENDING));
		}
		
		String sortString = getSortingString(sortOrders);
		
		String queryString = "SELECT dm FROM DeviceModel dm WHERE (CONCAT(dm.id, '') LIKE :filter "
				+ "OR LOWER(dm.name) LIKE :filter "
				+ "OR LOWER(dm.artNr) LIKE :filter)" + sortString;
		
		return EntityManagerHandler.runInTransaction(em -> em.createQuery(queryString, DeviceModel.class)
				 .setParameter("filter", preparedFilter)
				 .setFirstResult(offset)
			     .setMaxResults(limit)
				 .getResultList());
	}

	/**
	 * 
	 * @param producer
	 * @return DeviceModels for the provided Producer
	 */
	public Collection<DeviceModel> getDeviceModelsOfProducer(Producer producer) {
		return EntityManagerHandler.runInTransaction(em -> em.createQuery("SELECT dm FROM DeviceModel dm WHERE dm.producer = :producer ORDER BY dm.name", DeviceModel.class)
				.setParameter("producer", producer)
				.getResultList());
	}
	
	/**
	 * 
	 * @param artNr
	 * @return DeviceModels matching the given article number
	 */
	public DeviceModel getDeviceModelByArtNr(String artNr) {
		Collection<DeviceModel> deviceModels;
		deviceModels = EntityManagerHandler.runInTransaction(em -> em.createQuery("SELECT dm FROM DeviceModel dm WHERE dm.artNr = :artNr ORDER BY dm.name", DeviceModel.class)
				.setParameter("artNr", artNr)
				.getResultList());
			for (DeviceModel dM : deviceModels) {
				if (dM.getArtNr().equals(artNr)) {
					return dM;
				}
			}
			return null;
	}
}
