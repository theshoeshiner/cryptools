package org.thshsh.crypt.web.view;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.ldap.repository.support.SimpleLdapRepository;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.thshsh.crypt.User;
import org.thshsh.crypt.web.repo.AppUserRepository;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;

public class UserDialog extends EntityDialog<User> {

	//@Autowired
	//LdapTemplate ldapTemplate;

	/*@Autowired
	RoleRepository roleRepo;*/

	@Autowired
	SimpleLdapRepository<LdapUser> ldapRepo;

	@Value("${ldap.user.base}")
	String ldapUserBase;

	@Autowired
	AppUserRepository userRepo;

	ComboBox<LdapUser> userSearch;

	public UserDialog(User entity) {
		super(entity,User.class);
	}

	@PostConstruct
	public void postConstruct() {
		this.createText ="Add";
		super.postConstruct(userRepo);
	}

	@Override
	protected void setupForm() {

		LOGGER.info("setup: {}",create);

		if(create) {

			//SimpleLdapRepository<LdapUser> slr = new SimpleLdapRepository<>(ldapTemplate, new DefaultObjectDirectoryMapper(), LdapUser.class);

			formLayout.startHorizontalLayout();

			userSearch = new ComboBox<LdapUser>();
			userSearch.setWidth("250px");
			userSearch.setPlaceholder("Name");
			userSearch.setHelperText("Start typing to search");
			userSearch.setPageSize(150);
			userSearch.setItemLabelGenerator(user -> {
				return user.getDisplayName();
			});

			userSearch.setClearButtonVisible(true);
			userSearch.setItems(query -> {
				int pageSize = query.getPageSize();
				query.getPage();
				if(!query.getFilter().isPresent() || query.getFilter().get().length() == 0) {
					return Collections.EMPTY_LIST.stream();
				}
				LdapQuery q = LdapQueryBuilder
						.query()
						.base(ldapUserBase)
						.countLimit(pageSize)
						.filter("(cn=*{0}*)", query.getFilter().get());

				List<LdapUser> users = ldapRepo.findAll(q);

				Collections.sort(users, (u0,u1) -> {
					return u0.getDisplayName().toLowerCase().compareTo(u1.getDisplayName().toLowerCase());
				});
				return users.stream();
			});

			formLayout.add(userSearch);
			/*userSearch.addValueChangeListener(change -> {
				LdapUser user = change.getValue();
				LOGGER.info("change: {}",user);
				if(userRepo.findByEmailIgnoreCase(user.getEmail()).isPresent()) {
					LOGGER.info("error!");
					userSearch.setErrorMessage("User already exists");

				}
			});
			*/
			binder
			.forField(userSearch)
			.withValidator((v,c) -> {
				if(userRepo.findByEmailIgnoreCase(userSearch.getValue().getEmail()).isPresent()) {
					return ValidationResult.error("User already exists");
				}
				else return ValidationResult.ok();
			})
			.bind(User::getLdapUser, User::setLdapUser);

			formLayout.endLayout();

		}


		/*	formLayout.startHorizontalLayout();
			TextField email = new TextField("Email");
			email.setWidth("250px");
			email.setReadOnly(true);

			formLayout.add(email);
			formLayout.endLayout();*/

		/*	ListBox<Role> listBox = new ListBox<>();
			listBox.set
			formLayout.add(listBox);
			listBox.setItems(roleRepo.findAll());*/

		/*	List<Role> all = roleRepo.findAll();
			LOGGER.info("all: {}",all);

			MultiselectComboBox<Role> multiselectComboBox = new MultiselectComboBox<>("Roles");
			multiselectComboBox.setWidth("450px");
			//multiselectComboBox.setLabel("Select Roles");
			//multiselectComboBox.setPlaceholder("Choose...");
			multiselectComboBox.setItems(all);
			//multiselectComboBox.setCompactMode(true);
			//Stream<Role> fetch = multiselectComboBox.getDataProvider().fetch(new Query<>());
			//LOGGER.info("Stream: {}",fetch.count());
			//List<Role> found = fetch.collect(Collectors.toList());
			//LOGGER.info("found: {}",found);

		multiselectComboBox.setItemLabelGenerator(r -> {
			//LOGGER.info("get item label {}",r);
			//LOGGER.error("get item label",new RuntimeException());
			return r.getName();
		});
		formLayout.add(multiselectComboBox);

		binder.forField(multiselectComboBox).bind(User::getRoles, User::setRoles);
		*/

		/*ComboBox<Role> cb = new ComboBox<>();
		cb.setItemLabelGenerator(r->r.getName());
		cb.setItems(all);

		formLayout.add(cb);
		*/

		//listBox.setValue("Option one");

		//Grid<Role> roleGrid = new Grid<>();
		//formLayout.add(roleGrid);

		//GridField<Role> permissions = new GridField<>(permGrid);
		//binder.forField(permissions).bind(null, null)

		if(create) {
			/*userSearch.addValueChangeListener(change -> {
				LdapUser user = change.getValue();
				email.setValue(user.getEmail());
				getEntity().update(user);
			});*/
		}

	}

	@Override
	protected void bind() throws ValidationException {
		super.bind();
		if(create) {
			getEntity().update(userSearch.getValue());
		}
	}



}
