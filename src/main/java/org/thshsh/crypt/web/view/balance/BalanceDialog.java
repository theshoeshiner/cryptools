package org.thshsh.crypt.web.view.balance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Balance;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.repo.AccountRepository;
import org.thshsh.crypt.repo.BalanceRepository;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.repo.ExchangeRepository;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.vaadin.entity.EntityForm;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public class BalanceDialog extends org.thshsh.vaadin.entity.EntityDialog<Balance, Long> {

	public static final Logger LOGGER = LoggerFactory.getLogger(BalanceDialog.class);

	@Autowired
	ApplicationContext context;

	// @Autowired
	// AssetNameDataProvider assetNamePro;

	@Autowired
	CurrencyRepository assetRepo;

	@Autowired
	ExchangeRepository exeRepo;

	@Autowired
	BalanceRepository balRepo;

	@Autowired
	AccountRepository accountRepo;

	@Autowired
	AppSession session;

	Portfolio portfolio;

	public BalanceDialog(Balance en, Portfolio p) {
		super(BalanceForm.class, en);
		this.portfolio = p;
		LOGGER.info("dialog portfolio: {}", p);
	}

	@Override
	protected EntityForm<Balance, Long> createEntityForm() {
		return context.getBean(BalanceForm.class,this.entity,this.portfolio);
	}



}
