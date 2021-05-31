package org.thshsh.crypt.web;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.thshsh.crypt.CryptModel;
import org.thshsh.cryptman.CryptmanModel;

@Configuration
@EnableJpaRepositories(
		//repositoryBaseClass = BaseRepositoryImpl.class
		basePackageClasses = {
				CryptModel.class,
				CryptmanModel.class
				}
)
@EntityScan(basePackageClasses = {
		CryptModel.class,
		CryptmanModel.class
		})
//@EnableJpaAuditing
public class DataSourceConfiguration {

	public static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfiguration.class);



	@Primary
    @Bean
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }


}