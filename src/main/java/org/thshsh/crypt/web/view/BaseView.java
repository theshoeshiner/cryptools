package org.thshsh.crypt.web.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class BaseView extends VerticalLayout {

	/*	String pageTitle;
		String pagePath;
		
		public BaseView() {
			try {
				pageTitle = FieldUtils.readStaticField(this.getClass(), "PAGE_TITLE").toString();
				pagePath = FieldUtils.readStaticField(this.getClass(), "PAGE_PATH").toString();
			} 
			catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}*/

	public abstract String getPageTitle();
	public abstract String getPagePath();
	
	
}
