package org.thshsh.crypt.web.view;

import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.PortfolioHistory;
import org.thshsh.crypt.web.security.SecuredByFeatureAccess;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.entity.EntityGridView;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@SuppressWarnings("serial")
@Route(value = "histories", layout = MainLayout.class)
@PageTitle("Histories")
@SecuredByFeatureAccess(feature = Feature.Portfolio,access = Access.Super)
public class PortfolioHistoriesView extends EntityGridView<PortfolioHistory, Long> {

	public PortfolioHistoriesView() {
		super(PortfolioHistoryGrid.class);
	}

}
