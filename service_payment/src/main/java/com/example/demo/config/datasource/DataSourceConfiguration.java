package com.example.demo.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("classpath:/properties/datasource.properties")
public class DataSourceConfiguration {
    @Value("${datasource.url}")
    public String url;
    @Value("${datasource.username}")
    public String user;
    @Value("${datasource.driver}")
    public String driver;
    @Value("${datasource.password}")
    public String password;
    @Value("${druid.max-idle}")
    public String maxIdle;
    @Value("${druid.max-active}")
    public String maxActive;
    @Value("${druid.initial-size}")
    public String initialSize;

    @Bean
    public DataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        Properties properties = new Properties();
        properties.put("druid.url", url);
        properties.put("druid.username", user);
        properties.put("druid.password", password);
        properties.put("druid.maxIdle", maxIdle);
        properties.put("druid.maxActive", maxActive);
        properties.put("druid.initialSize", initialSize);
        druidDataSource.configFromPropety(properties);
        return druidDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource());
        return jdbcTemplate;
    }

    @Bean
    public TransactionManager transactionManager() {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource());
        return dataSourceTransactionManager;
    }
}
