package com.shiqu.behavior.config;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.Objects;

//@Configuration
public class MyBatisConfig {

    @Autowired
    private DataSource dataSource;
/*
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        factoryBean.setDataSource(dataSource);

        // 其他配置...

        return factoryBean.getObject();

    }*/

    @Value("${mybatis-plus.mapper-locations}")
    private String mapperLocations;
    //配置FactoryBean
    @Bean(name = "sqlSessionFactoryBean")
    public SqlSessionFactoryBean sqlSessionFactoryBean() {
        SqlSessionFactoryBean sqlSessionFactoryBean = null;
        try {
            // 加载JNDI配置
            Context context = new InitialContext();
            // 实例SessionFactory
            sqlSessionFactoryBean = new SqlSessionFactoryBean();
            // 配置数据源
            sqlSessionFactoryBean.setDataSource(dataSource);
            // 加载MyBatis配置文件
            PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            // 能加载多个，所以可以配置通配符(如：classpath*:mapper/**/*.xml)
            sqlSessionFactoryBean.setMapperLocations(resourcePatternResolver.getResources(mapperLocations));
            // 配置mybatis的config文件(我目前用不上)
            // sqlSessionFactoryBean.setConfigLocation("mybatis-config.xml");
        } catch (Exception e) {
            System.out.println("创建SqlSession连接工厂错误：{}");
        }
        return sqlSessionFactoryBean;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate() throws Exception {
        SqlSessionTemplate sqlSessionTemplate=new SqlSessionTemplate(Objects.requireNonNull(sqlSessionFactoryBean().getObject()), ExecutorType.BATCH);
        return sqlSessionTemplate;
    }
}
