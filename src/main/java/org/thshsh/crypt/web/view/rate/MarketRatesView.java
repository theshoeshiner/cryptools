package org.thshsh.crypt.web.view.rate;

import org.thshsh.crypt.Access;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.MarketRate;
import org.thshsh.crypt.web.security.SecuredByFeatureAccess;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.entity.EntityGridView;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = "rates", layout = MainLayout.class)
@PageTitle("Market Rates")
@SecuredByFeatureAccess(feature = Feature.Currency,access = Access.Read)
public class MarketRatesView extends EntityGridView<MarketRate, Long>{

	public MarketRatesView() {
		super(MarketRateGrid.class);
	}

}
