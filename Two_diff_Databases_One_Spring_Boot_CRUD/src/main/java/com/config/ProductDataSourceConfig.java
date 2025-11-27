package com.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.example.multidb.product.repo",
    entityManagerFactoryRef = "productEntityManagerFactory",
    transactionManagerRef = "productTransactionManager"
)
public class ProductDataSourceConfig implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Bean(name = "productDataSource")
    public DataSource productDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(env.getProperty("spring.datasource.product.driver-class-name"))
                .url(env.getProperty("spring.datasource.product.jdbc-url"))
                .username(env.getProperty("spring.datasource.product.username"))
                .password(env.getProperty("spring.datasource.product.password"))
                .build();
    }

    @Bean(name = "productEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean productEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(productDataSource());
        emf.setPackagesToScan("com.example.multidb.product.entity");
        emf.setPersistenceUnitName("productPU");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> jpaProps = new HashMap<>();
        if (env.getProperty("spring.jpa.product.hibernate.ddl-auto") != null) {
            jpaProps.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.product.hibernate.ddl-auto"));
        }
        if (env.getProperty("spring.jpa.product.show-sql") != null) {
            jpaProps.put("hibernate.show_sql", env.getProperty("spring.jpa.product.show-sql"));
        }
        if (env.getProperty("spring.jpa.product.database-platform") != null) {
            jpaProps.put("hibernate.dialect", env.getProperty("spring.jpa.product.database-platform"));
        }
        emf.setJpaPropertyMap(jpaProps);

        return emf;
    }

    @Bean(name = "productTransactionManager")
    public PlatformTransactionManager productTransactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(productEntityManagerFactory().getObject());
        return tm;
    }
}
