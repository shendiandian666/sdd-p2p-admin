package com.sdd;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.github.pagehelper.PageHelper;

@Configuration
public class DataSourceConfig {

	/**
	 * 数据源
	 * @return
	 */
	@Bean(name="dataSource")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource druidDataSource(){
		DruidDataSource druidDataSource = new DruidDataSource();
		return druidDataSource;
	}
	
	/**
	 * 数据源session工厂
	 * @param dataSource
	 * @return
	 * @throws Exception
	 */
	@Bean(name="sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource")DataSource dataSource) throws Exception{
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:/mybatis/**/*Mapper.xml"));
		org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
		configuration.setCallSettersOnNulls(true);//空字段也返回,不然就会返回null
		bean.setConfiguration(configuration);
		// 分页插件
        PageHelper pageHelper = new PageHelper();
        Properties props = new Properties();
        props.setProperty("reasonable", "true");
        props.setProperty("supportMethodsArguments", "true");
        props.setProperty("returnPageInfo", "check");
        props.setProperty("params", "count=countSql");
        pageHelper.setProperties(props);
        // 添加插件
        bean.setPlugins(new Interceptor[] { pageHelper });
        try {
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	
	/**
	 * 事物管理
	 * @param dataSource
	 * @return
	 */
	@Bean(name="transactionManager")
	public DataSourceTransactionManager transactionManager(@Qualifier("dataSource")DataSource dataSource){
		return new DataSourceTransactionManager(dataSource);
	}
	
	@Bean(name="sqlSessionTemplate")
	public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory")SqlSessionFactory sqlSessionFactory){
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
	/**
	 * 啊里druid数据源
	 * @return
	 */
	@Bean
	public ServletRegistrationBean druidServlet(){
		ServletRegistrationBean druid = new ServletRegistrationBean();
		druid.setServlet(new StatViewServlet());
		druid.setUrlMappings(Arrays.asList("/druid/*"));
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("loginUsername", "admin");
		params.put("loginPassword", "admin");
		druid.setInitParameters(params);
		return druid;
	}

	@Bean
	public FilterRegistrationBean webStatFilter(){
		FilterRegistrationBean fitler = new FilterRegistrationBean();
		fitler.setFilter(new WebStatFilter());
		fitler.setUrlPatterns(Arrays.asList("/*"));
		fitler.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
		return fitler;
	}
	
}
