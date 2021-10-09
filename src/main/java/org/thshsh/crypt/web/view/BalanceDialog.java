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



	/*
	 * @Override protected Balance createEntity() {
	 * LOGGER.info("createEntity: {}",this.portfolio); Balance b =
	 * super.createEntity(); if(portfolio!=null) b.setPortfolio(portfolio); return
	 * b; }
	 *
	 *
	 *
	 * public BalanceDialog(Balance en) { super(en, Balance.class); }
	 *
	 * @PostConstruct public void PostConstruct() { super.postConstruct(balRepo); }
	 *
	 * ComboBox<Exchange> exchangeField;
	 *
	 * @SuppressWarnings("unchecked")
	 *
	 * @Override protected void setupForm() {
	 *
	 * formLayout.startHorizontalLayout();
	 *
	 * exchangeField = new ComboBox<>("Exchange");
	 * exchangeField.setItemLabelGenerator(Exchange::getName);
	 * exchangeField.setItems(context.getBean(HasNameDataProvider.class,exeRepo));
	 * exchangeField.setWidth("250px"); formLayout.add(exchangeField);
	 *
	 * formLayout.endLayout(); formLayout.startHorizontalLayout();
	 *
	 * ComboBox<Currency> ass = new ComboBox<>("Currency");
	 * ass.setItemLabelGenerator(c -> { return c.getName() +" ("+c.getKey()+")"; });
	 * ass.setItems(context.getBean(HasSymbolDataProvider.class,assetRepo));
	 * ass.setWidth("250px"); formLayout.add(ass);
	 *
	 * TextField balance = new TextField("Balance"); formLayout.add(balance);
	 *
	 * formLayout.endLayout();
	 *
	 * binder.forField(exchangeField).asRequired().bind(b -> { return null; }, (b,e)
	 * -> {
	 *
	 * } );
	 * binder.forField(exchangeField).bind(Balance::getExchange,Balance::setExchange
	 * ); binder.forField(ass).asRequired().bind(Balance::getCurrency,
	 * Balance::setCurrency); binder.forField(balance) .asRequired()
	 * .withNullRepresentation("") .withConverter(new BigDecimalConverter())
	 * .bind(Balance::getBalance, Balance::setBalance);
	 *
	 * }
	 *
	 * @Override protected void bind() throws ValidationException { super.bind();
	 * //find default account for exchange //List<Account> accounts =
	 * accountRepo.findByUser(session.getUser()); List<Account> accounts =
	 * accountRepo.findAll(); Optional<Account> acc = accounts.stream().filter(a ->
	 * a.getExchange().equals(exchangeField.getValue())).findFirst();
	 * LOGGER.info("exchange account: {}",acc); Account a = acc.orElseGet(() -> {
	 * Account newAc = new Account(); //newAc.setUser(session.getUser());
	 * newAc.setExchange(exchangeField.getValue()); LOGGER.info("saving: {}",newAc);
	 * accountRepo.save(newAc); return newAc; }); this.getEntity().setAccount(a);
	 *
	 * }
	 */

}
