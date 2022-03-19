package com.example.demo.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.view.tiles3.SpringLocaleResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@PropertySource("classpath:/properties/datasource.properties")
@MapperScan(basePackages = "com.example.demo.magnus.mapper")
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
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        Environment.Builder builder = new Environment.Builder("1");
        Environment build = builder.dataSource(dataSource()).transactionFactory(new SpringManagedTransactionFactory()).build();
        configuration.setEnvironment(build);
        configuration.setCacheEnabled(true);
        configuration.setLogImpl(Log4j2Impl.class);
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setConfiguration(configuration);
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(pathMatchingResourcePatternResolver.getResources("classpath:/mapper/*"));
        sqlSessionFactoryBean.setDataSource(dataSource());
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    @Primary
    public SqlSession sqlSession() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory(), ExecutorType.SIMPLE);
    }

    @Bean
    @Qualifier("batch")
    public SqlSession sqlSessionBatch() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory(), ExecutorType.BATCH);
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource());
        return jdbcTemplate;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource());
        return dataSourceTransactionManager;
    }

    @Bean
    public TransactionTemplate transactionTemplate() {
        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(transactionManager());
        return transactionTemplate;
    }
}
