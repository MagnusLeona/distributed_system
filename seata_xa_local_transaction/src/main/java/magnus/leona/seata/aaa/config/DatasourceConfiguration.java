package magnus.leona.seata.aaa.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.xa.DataSourceProxyXA;
import magnus.leona.seata.aaa.mapper.aaa.AAAMapper;
import magnus.leona.seata.aaa.mapper.bbb.BBBMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfiguration {

    @Bean("aaaDataSource")
    @ConfigurationProperties(prefix = "spring.hikari.aaa")
    public DataSource hikariDataSourceAAA() {
        return new HikariDataSource();
    }

    @Bean("bbbDataSource")
    @ConfigurationProperties(prefix = "spring.hikari.bbb")
    public DataSource hikariDataSourceBBB() {
        return new HikariDataSource();
    }

    @Bean("aaaDataSourceXA")
    public DataSourceProxyXA aaaDataSourceProxyXA() {
        return new DataSourceProxyXA(hikariDataSourceAAA());
    }

    @Bean("bbbDataSourceXA")
    public DataSourceProxyXA bbbDataSourceProxyXA() {
        return new DataSourceProxyXA(hikariDataSourceBBB());
    }

    @Bean("aaaSqlSessionFactory")
    public SqlSessionFactory aaaSqlSessionFactory() {
        JdbcTransactionFactory jdbcTransactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", jdbcTransactionFactory, aaaDataSourceProxyXA());
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration(
                environment);
        configuration.addMapper(AAAMapper.class);
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    @Bean("bbbSqlSessionFactory")
    public SqlSessionFactory bbbSqlSessionFactory() {
        JdbcTransactionFactory jdbcTransactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", jdbcTransactionFactory, bbbDataSourceProxyXA());
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration(
                environment);
        configuration.addMapper(BBBMapper.class);
        return new SqlSessionFactoryBuilder().build(configuration);
    }
}
