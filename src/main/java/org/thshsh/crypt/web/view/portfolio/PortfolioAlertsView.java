package org.thshsh.crypt.web.view.portfolio;

import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.PortfolioAlert;
import org.thshsh.crypt.web.security.SecuredByFeatureAccess;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.entity.EntityGridView;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "alerts", layout = MainLayout.class)
@PageTitle("Alerts")
@SecuredByFeatureAccess(feature = Feature.Portfolio,access = Access.Super)
public class PortfolioAlertsView extends EntityGridView<PortfolioAlert, Long> {

	public PortfolioAlertsView() {
		super(PortfolioAlertGrid.class);
	}

}
