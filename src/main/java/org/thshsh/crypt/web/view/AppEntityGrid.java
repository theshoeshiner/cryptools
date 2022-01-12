package org.thshsh.crypt.web.view;

import org.thshsh.crypt.IdedEntity;
import org.thshsh.vaadin.entity.EntityGrid;

import com.vaadin.flow.component.Component;

@SuppressWarnings("serial")
public abstract class AppEntityGrid<T extends IdedEntity> extends EntityGrid<T,Long> {

	public AppEntityGrid(Class<T> c, Class<? extends Component> ev, FilterMode fm) {
		super(c, ev, fm, "id");
	}



	@Override
	public Long getEntityId(T entity) {
		return entity.getId();
	}

	
}
