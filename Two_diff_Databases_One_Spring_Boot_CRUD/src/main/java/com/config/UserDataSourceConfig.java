package com.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.example.multidb.user.repo",
    entityManagerFactoryRef = "userEntityManagerFactory",
    transactionManagerRef = "userTransactionManager"
)
public class UserDataSourceConfig implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Bean(name = "userDataSource")
    public DataSource userDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(env.getProperty("spring.datasource.user.driver-class-name"))
                .url(env.getProperty("spring.datasource.user.jdbc-url"))
                .username(env.getProperty("spring.datasource.user.username"))
                .password(env.getProperty("spring.datasource.user.password"))
                .build();
    }

    @Bean(name = "userEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean userEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(userDataSource());
        emf.setPackagesToScan("com.example.multidb.user.entity");
        emf.setPersistenceUnitName("userPU");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> jpaProps = new HashMap<>();
        // pick up properties from application.properties with prefix spring.jpa.user.*
        if (env.getProperty("spring.jpa.user.hibernate.ddl-auto") != null) {
            jpaProps.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.user.hibernate.ddl-auto"));
        }
        if (env.getProperty("spring.jpa.user.show-sql") != null) {
            jpaProps.put("hibernate.show_sql", env.getProperty("spring.jpa.user.show-sql"));
        }
        if (env.getProperty("spring.jpa.user.database-platform") != null) {
            jpaProps.put("hibernate.dialect", env.getProperty("spring.jpa.user.database-platform"));
        }
        emf.setJpaPropertyMap(jpaProps);

        return emf;
    }

    @Bean(name = "userTransactionManager")
    public PlatformTransactionManager userTransactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(userEntityManagerFactory().getObject());
        return tm;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }
}