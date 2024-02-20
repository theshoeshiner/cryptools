package org.thshsh.crypt.web.view;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.imaging.Imaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;
import org.thshsh.coinbase.adv.AdvancedTradeApi;
import org.thshsh.color.AbstractColor;
import org.thshsh.color.ColorSpaceConverter;
import org.thshsh.color.ColorUtils;
import org.thshsh.color.LchColor;
import org.thshsh.crypt.Access;
import org.thshsh.crypt.Currency;
import org.thshsh.crypt.Feature;
import org.thshsh.crypt.Portfolio;
import org.thshsh.crypt.repo.CurrencyRepository;
import org.thshsh.crypt.repo.PortfolioRepository;
import org.thshsh.crypt.serv.ImageService;
import org.thshsh.crypt.serv.ManagePortfolioService;
import org.thshsh.crypt.web.security.SecuredByFeatureAccess;
import org.thshsh.crypt.web.views.main.MainLayout;
import org.thshsh.vaadin.ClickableAnchor;
import org.thshsh.vaadin.UIUtils;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.DataLabelsBuilder;
import com.github.appreciated.apexcharts.config.builder.LegendBuilder;
import com.github.appreciated.apexcharts.config.builder.StrokeBuilder;
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.legend.HorizontalAlign;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.xaxis.XAxisType;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.componentfactory.Popup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "testing", layout = MainLayout.class)
@PageTitle("Testing")
@SecuredByFeatureAccess(feature = Feature.System, access = Access.Read)
public class TestingView extends VerticalLayout {

	public static final Logger LOGGER = LoggerFactory.getLogger(TestingView.class);

	// @Autowired
	// LdapTemplate ldapTemplate;

	// @Autowired
	// AuditReader auditReader;

	// @Autowired
	// LdapUserRepository ldapRepo;

	@Autowired
	Breadcrumbs breadcrumbs;

	@Autowired
	ImageService imageService;

	@Autowired
	TaskExecutor executor;

	@Autowired
	CurrencyRepository currRepo;

	@Autowired
	ApplicationContext context;

	@Autowired
	PortfolioRepository portRepo;

	@Autowired
	ManagePortfolioService portService;
	
	@Autowired
	AdvancedTradeApi atApi;
	

	// coinbase oauth
	// clientid: 4008e661cd4e9c943ece898ccd3671050d852f03517341e453a5a7bc1a6f5e25
	// secret: d905f7b250a654762288560e694e246ebfc4546d5abc54d09c11275118c2030d

	// https://www.coinbase.com/oauth/authorize?client_id=4008e661cd4e9c943ece898ccd3671050d852f03517341e453a5a7bc1a6f5e25&redirect_uri=https%3A%2F%2Fcryptools.thshsh.org%2Foauthcallback&response_type=code&scope=wallet%3Aaccounts%3Aread
	// respose code 861655c572acef09c8a91064ee31afb3988f49e9c3b743940718806e1ddc7319
	// curl https://api.coinbase.com/v2/accounts \

	// @Value("${ldap.user.base}")
	// String ldapUserBase;

	public TestingView() {

	}

	

	public void coinbase() {
		Anchor l = new Anchor(atApi.getAuthUrl().toString(),"Authorize Coinbase");
		add(l);
	}
	
	public void chart() {
		ApexCharts chart = ApexChartsBuilder.get()

				.withChart(ChartBuilder
						.get().withType(Type.LINE).withZoom(ZoomBuilder.get().withEnabled(false).build()).build())
				.withDataLabels(DataLabelsBuilder.get().withEnabled(false).build())
				.withStroke(StrokeBuilder.get().withCurve(Curve.SMOOTH)
						// .withColors(colors.toArray(new String[0]))
						.withWidth(3d).build())
				// .withFill(FillBuilder.get().withOpacity(0d).build())
				.withSeries(new Series<>(1, 2, 3, 5, 7, 2, 1)
				// series.toArray(new Series[] {})
				// new Series<>("USD Value", valuePerHour.toArray())
				// ,new Series<>(curr.getKey(), currencyValues.toArray())

				// .withSeries(new Series<>("USD Value", valuePerHour.toArray())
				// new Series<>("Alert Threshold", thresh.toArray())
				)

				/*.withLabels(
					   //dates.toArray(new String[dates.size()])
					   "A","B","C","D","E","F","G","G"
					   )
				*/
				.withXaxis(XAxisBuilder.get().withType(XAxisType.NUMERIC)
						/*.withLabels(LabelsBuilder
								.get()
								.withFormat("MMM dd")
								.build())*/
						.build())
				/* .withYaxis(
				 		YAxisBuilder.get().with
				     		.withDecimalsInFloat(10d)
				     		//.withTickAmount(10d)
				             .withMin(0d)
				             .build()
				 ,YAxisBuilder.get()
				 	.withOpposite(true)
				 	.withMax(1)
				 	.withMin(0)
				 	.withDecimalsInFloat(2d)
				 	.build()
				         )
				*/ .withLegend(LegendBuilder.get().withHorizontalAlign(HorizontalAlign.LEFT)

						.build())
				.build();
		add(chart);
		// chart.setHeight("600px");
	}

	public void callCoinbase() {
		// OAuth2RestTemplate rest = new OAuth2RestTemplate();
		// OAuth2AuthorizedClientService as;
		RestTemplate coinbase = new RestTemplate();

		UriBuilderFactory uriBuilder = new DefaultUriBuilderFactory();
		;

		// https://api.coinbase.com/oauth/token
		UriBuilder builder = uriBuilder.uriString("");
		// coinbase.postForObject(URI.create(""), request, responseType)

	}

	@PostConstruct
	public void postConstruct() {
		// breadcrumbs.resetBreadcrumbs().addBreadcrumb(DashboardView.TITLE,
		// DashboardView.class).addBreadcrumb("About", TestingView.class);

		chart();

		{
			ClickableAnchor ca = new ClickableAnchor("", "Text");
			add(ca);
		}

		{
			VerticalLayout runLayout = new VerticalLayout();
			add(runLayout);
			ComboBox<Portfolio> ports = new ComboBox<Portfolio>();
			ports.setItemLabelGenerator(Portfolio::getName);
			ports.setItems(context.getBean(HasNameDataProvider.class, portRepo));
			runLayout.add(ports);
			Button runHistory = new Button("Run 500 History Jobs");
			runLayout.add(runHistory);
			runHistory.addClickListener(click -> {
				if (!ports.isEmpty()) {
					executor.execute(() -> {
						for (int i = 0; i < 500; i++) {
							LOGGER.info("Running: {}", i);
							portService.createHistory(ports.getValue());
						}
					});
				}

			});

		}

		{

			H1 h1 = new H1("This is an H1");
			add(h1);

			H3 h3 = new H3("This is an H3");
			add(h3);

			Button smallbutton = new Button("Small Primary");
			smallbutton.addThemeName("primary");
			smallbutton.addThemeName("small");
			add(smallbutton);

			Button button = new Button("Primary");
			button.addThemeName("primary");
			add(button);

			Button noclick = new Button("Unclickable");
			noclick.addClassName("unclickable");
			add(noclick);

			/*List<LdapUser> found = search("ab");
			LOGGER.info("found: {}",found);
			found.forEach(user -> {
				LOGGER.info("user: {}",user.getDisplayName());
			});*/

			/*ExampleFilterDataProvider<LdapUser,String> dataProvider = new ExampleFilterDataProvider<>(
					ldapRepo, 
					ExampleMatcher.matchingAny()
					.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
					.withIgnoreCase().withIgnoreNullValues()
					, QuerySortOrder.asc("surName").build()
					);*/

			// LdapUserMapper lum = new LdapUserMapper();

			// SimpleLdapRepository<LdapUser> slr = new SimpleLdapRepository<>(ldapTemplate,
			// new DefaultObjectDirectoryMapper(), LdapUser.class);

			// DataProvider<LdapUser,LdapUser> dp = dataProvider;

			/*ComboBox<LdapUser> userSearch = new ComboBox<LdapUser>("User");
			userSearch.setWidth("250px");
			userSearch.setPlaceholder("User Name");
			userSearch.setPageSize(150);
			userSearch.setItemLabelGenerator(user -> {
				return user.getDisplayName();
			});
			userSearch.addValueChangeListener(change -> {
			
			});
			userSearch.setClearButtonVisible(true);
			
			//MessageFormat.format(ldapUserBase, null);
			
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
				
				List<LdapUser> users = slr.findAll(q);
				
				Collections.sort(users, (u0,u1) -> {
					return u0.getDisplayName().toLowerCase().compareTo(u1.getDisplayName().toLowerCase());
				});
				return users.stream();
			});
			
			add(userSearch);
			*/

			/*	AuditQuery query =  auditReader
					    .createQuery()
					    .forRevisionsOfEntity( ProfileConfiguration.class, true, false )
					    .addOrder( AuditEntity.revisionProperty("timestamp").desc() );
				 query.add(AuditEntity.revisionNumber().maximize().computeAggregationInInstanceContext());
				 
			
				List<?> results =  query.getResultList();
			
				
				results.forEach(o -> {
					LOGGER.info("entity: {}",o);
					
				});*/

		}

		Button button = new Button("Push Me");
		button.setId("push-me");

		Popup popup = new Popup();
		popup.setFor(button.getId().orElse(null));
		Div text = new Div();
		text.setText("element 1");
		Div text2 = new Div();
		text2.setText("element 2");
		popup.add(text, text2);

		add(button, popup);

		/*	Div closeOnClickStatus = new Div();
			closeOnClickStatus.setText("Close on click: " + popup.isCloseOnClick());
			Div eventStatus = new Div();
			popup.addPopupOpenChangedEventListener(event -> {
				if (event.isOpened())
					eventStatus.setText("Popup opened");
				else
					eventStatus.setText("Popup closed");
			});
		
			add(closeOnClickStatus, eventStatus);*/

		/* Button pacsTest = new Button("PACS Test",click -> this.pacsTest());
		 add(pacsTest);*/

		FormLayout fl = new FormLayout();
		add(fl);

		TextField name = new TextField("Name");
		UIUtils.setElementAttribute(name, "name", "name");
		name.setAutocomplete(Autocomplete.NAME);
		fl.add(name);

		TextField email = new TextField("email");
		UIUtils.setElementAttribute(email, "name", "email");
		email.setAutocomplete(Autocomplete.EMAIL);
		fl.add(email);

		Button submit = new Button("submit");
		fl.add(submit);
		submit.addClickListener(click -> {
			LOGGER.info("name: {} email: {}", name.getValue(), email.getValue());
			name.clear();
			email.clear();
		});

		Button images = new Button("Scan Images", click -> {

			executor.execute(() -> {

				// List<String> check = Arrays.asList("grt","amp","comp","band","bat","nmr");
				List<String> check = Arrays.asList(
						// "grt",
						// "dai",
						"xrp", "eos", "xlm", "forth", "xmr"

				// "amp"
				);

				List<Currency> save = new ArrayList<>();

				ColorSpaceConverter csc = new ColorSpaceConverter();
				currRepo.findAll().forEach(cur -> {

					if (cur.getColorHex() != null)
						return;

					LOGGER.info("checking image for: {}", cur);
					try {
						LOGGER.info("image: {}", cur.getImageUrl());
						InputStream image = imageService.getImage(cur);

						if (image == null)
							return;

						BufferedImage bi = null;
						try {
							bi = Imaging.getBufferedImage(image);
						} catch (IllegalArgumentException iae) {
							LOGGER.error("", iae);
							image = imageService.getImage(cur);
							try {
								bi = ImageIO.read(image);
							} catch (Exception e) {
								LOGGER.error("", e);
							}
						}

						if (bi == null) {
							LOGGER.error("Could not load image");
							return;
							// throw new IllegalStateException("Could not load image");
						}

						List<AbstractColor> toAvg = new ArrayList<>();
						for (int x = 0; x < bi.getWidth(); x++) {
							for (int y = 0; y < bi.getHeight(); y++) {
								int color = bi.getRGB(x, y);

								LchColor lch = new LchColor(csc.InttoLCH(color));
								// CieLabColor c = new CieLabColor(csc.InttoLAB(color));
								// LOGGER.info("color: {}",lch);
								if (lch.getL() < 90 && lch.getL() > 10) {

									if (lch.getC() > 25) {
										// LOGGER.info("color: {}",lch);
										toAvg.add(lch);
									}
								}

							}
						}

						if (toAvg.size() > 0) {
							LchColor average = (LchColor) ColorUtils.averageColors(toAvg);
							LOGGER.info("average: {}", average);
							int[] rgb = csc.LABtoRGB(csc.LCHtoLAB(average.getComponentsPrimitive()));
							byte[] rbgb = new byte[] { (byte) rgb[0], (byte) rgb[1], (byte) rgb[2] };
							LOGGER.info("rbg: {}", new Object[] { rgb });
							String hex = Hex.encodeHexString(rbgb);
							LOGGER.info("hex: {}", hex);
							cur.setColorHex(hex);
							save.add(cur);
							// currRepo.save(cur);
						} else {
							LOGGER.info("NO COLORS TO AVERAGE");
						}

					} catch (Exception e) {
						LOGGER.error("", e);
					}

					// }

				});

				currRepo.saveAll(save);

			});

			/*this.template.executeWithoutResult(action -> {
				histRepo.deleteAllByPortfolio(entity);
				refreshChartTab();
			});
			*/
		});
		add(images);
	}

	/* public List<LdapUser> search(String username) {
	
		List<LdapUser> users = ldapTemplate
		          .search(
		            "OU=Internal,OU=Development Accounts,DC=biodev,DC=corp", 
		            "cn=*" + username+"*", 
		            (AttributesMapper<LdapUser>) attrs -> {
		            	return new LdapUser(attrs);
		            });
		
		LdapQuery q;
		
		return users;
	
	}*/

	/*  protected void pacsTest() {
		 LOGGER.info("study repo: {}",studyRepo);
	 	
	
		 EntityManager em = studyRepo.getEntityManager();
		 LOGGER.info("em: {}",em);
		 
		 List<?> results = em.createNativeQuery("select TOP 10  pacsstudy0_.studyid as studyid1_0_ from staging_biopacs.study pacsstudy0_").getResultList();
		 LOGGER.info("results: {} = {}",results.size() , results);
		 
		 List<?> projects = em.createNativeQuery("select TOP 10  * from staging_biopacs.project pr").getResultList();
		 LOGGER.info("projects: {} = {}",projects.size() , projects);
		 
		 
		Page<Project> page= projectRepo.findAll(PageRequest.of(10, 10));
			 
		 page.forEach( p -> {
			 LOGGER.info("Project: {}",p);
		 });
	}*/

}
