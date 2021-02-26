package de.hsos.geois.ws2021.views.user;

import java.util.stream.Stream;

import com.vaadin.flow.data.provider.Query;

import de.hsos.geois.ws2021.data.entity.User;
import de.hsos.geois.ws2021.data.service.DataService;
import de.hsos.geois.ws2021.data.service.UserDataService;
import de.hsos.geois.ws2021.views.AbstractDataProvider;

public class UserDataProvider extends AbstractDataProvider<User> {
    
	private static final long serialVersionUID = -110064154580521089L;

	@Override
	protected String getFilterBase(User entity) {
		return entity.toString();
	}

	@Override
	protected DataService<User> getDataService() {
		return UserDataService.getInstance();
	}

	@Override
	protected Stream<User> fetchFromBackEnd(Query<User, String> query) {
		return UserDataService.getInstance().fetchUsers(this.filterText, query.getLimit(), query.getOffset(),
		        query.getSortOrders()).stream();
	}

	@Override
	protected int sizeInBackEnd(Query<User, String> query) {
		return UserDataService.getInstance().countUsers(this.filterText);
	}
}
