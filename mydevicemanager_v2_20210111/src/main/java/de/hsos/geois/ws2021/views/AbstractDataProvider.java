package de.hsos.geois.ws2021.views;

import java.util.Locale;
import java.util.Objects;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;

import de.hsos.geois.ws2021.data.AbstractEntity;
import de.hsos.geois.ws2021.data.service.DataService;

public abstract class AbstractDataProvider<T extends AbstractEntity> extends AbstractBackEndDataProvider<T, String> {

	private static final long serialVersionUID = 5732661343383387893L;
	protected String filterText = "";

	@Override
	public Long getId(T entity) {
		Objects.requireNonNull(entity, "Cannot provide an id for a null entity.");
		return entity.getId();
	}

	@Override
	public boolean isInMemory() {
		// TODO does it make sense to have this set to true?
		return true;
	}


	/**
	 * Sets the filter to use for the this data provider and refreshes data.
	 * <p>
	 * Filter is compared to getFilterBase.
	 * 
	 * @param filterText the text to filter by, never null
	 */
	public void setFilter(String filterText) {
		Objects.requireNonNull(filterText, "Filter text cannot be null");
		if (Objects.equals(this.filterText, filterText)) {
			return;
		}
		this.filterText = filterText.trim();
		refreshAll();
	}

	public String getFilterText() {
		return filterText;
	}

	protected boolean passesFilter(T entity, String filterText) {
		return entity != null && getFilterBase(entity).toLowerCase(Locale.GERMAN).contains(filterText);
	}

	/**
	 * Store given entity to the backing data service.
	 * 
	 * @param entity the updated or new entity
	 */
	public void save(T entity) {
		boolean isNewEntity = entity.getId() == null;

		if (isNewEntity) {
			getDataService().add(entity);
			refreshAll();
		} else {
			getDataService().update(entity);
			refreshItem(entity);
		}
	}

	/**
	 * Delete given entity using the backing data service.
	 * 
	 * @param entity to be deleted
	 */
	public void delete(T entity) {
		getDataService().delete(entity);
		refreshAll();
	}

	/**
	 * Determines the on what part of the entity the filter is applied.
	 * 
	 * @param entity Entity on which the filter shall be applied
	 * @return Bases on which the filter is checked
	 */
	protected abstract String getFilterBase(T entity);

	/**
	 * 
	 * @return DataService backing the DataProvider
	 */
	protected abstract DataService<T> getDataService();
}
