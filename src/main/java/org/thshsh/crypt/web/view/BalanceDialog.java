package org.thshsh.crypt.web.view;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.thshsh.crypt.Account;
import org.thshsh.crypt.Balance;
import org.thshsh.crypt.BigDecimalConverter;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Exchange;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.repo.AccountRepository;
import org.thshsh.crypt.repo.BalanceRepository;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.repo.ExchangeRepository;
import org.thshsh.crypt.web.AppSession;
import org.thshsh.vaadin.entity.EntityForm;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;

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
