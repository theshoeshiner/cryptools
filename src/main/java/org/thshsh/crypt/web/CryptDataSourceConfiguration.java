package org.thshsh.crypt.web;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.thshsh.crypt.CryptModel;
import org.thshsh.crypt.CryptmanModel;
import org.thshsh.spring.DataSourceConfiguration;

@Configuration
@EnableJpaRepositories(
		//repositoryBaseClass = BaseRepositoryImpl.class
		basePackageClasses = {
				CryptModel.class,
				CryptmanModel.class
				},
		basePackages = {"org.thshsh.crypt","org.thshsh.cryptman"},
		transactionManagerRef = "transactionManager",
		entityManagerFactoryRef = "entityManager"
)
@EntityScan(basePackageClasses = {
		CryptModel.class,
		CryptmanModel.class
		})
//@EnableJpaAuditing
public class CryptDataSourceConfiguration extends DataSourceConfiguration {

	public static final Logger LOGGER = LoggerFactory.getLogger(CryptDataSourceConfiguration.class);
	
	public CryptDataSourceConfiguration(@Autowired CryptDataSourceProperties props) {
		super(props);
	}


	@Primary
    @Bean
    @ConfigurationProperties(prefix="crypt.db.datasource")
	@Override
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
	
	

	@Bean("entityManager")
	@Primary
	@Override
	public LocalContainerEntityManagerFactoryBean entityManager() {
		return super.entityManager();
	}


	@Bean("transactionManager")
	@Primary
	@Override
	public PlatformTransactionManager transactionManager() {
		return super.transactionManager();
	}




	@Component
	@ConfigurationProperties("crypt.db")
	public static class CryptDataSourceProperties extends org.thshsh.spring.DataSourceProperties {
		
	}
}