package org.thshsh.crypt.web.view;

import java.io.Serializable;

import org.thshsh.crypt.Access;
import org.thshsh.crypt.web.SecurityUtils;
import org.thshsh.vaadin.entity.EntityGrid;

import com.vaadin.flow.component.Component;

@SuppressWarnings("serial")
public abstract class AppEntityGrid<T,ID extends Serializable> extends EntityGrid<T,ID> {

	public AppEntityGrid(Class<T> c, Class<? extends Component> ev, FilterMode fm) {
		super(c, ev, fm, "id");
		this.showEditButton = SecurityUtils.hasAccess(c, Access.ReadWrite);
		this.showDeleteButton = SecurityUtils.hasAccess(c, Access.ReadWriteDelete);
	}

	/*@Override
	public void postConstruct() {
		super.postConstruct();
	}*/
	
	

	
	
}
