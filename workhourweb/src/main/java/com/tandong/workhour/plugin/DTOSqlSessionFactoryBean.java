/**
 * 
 */
package com.tandong.workhour.plugin;

import static org.springframework.util.ObjectUtils.isEmpty;
import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.tokenizeToStringArray;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;

import com.tandong.workhour.utils.ReflectUtil;

/**
 * @author PeterTan
 * 
 */
public class DTOSqlSessionFactoryBean extends SqlSessionFactoryBean {

	@Override
	protected SqlSessionFactory buildSqlSessionFactory() throws IOException {
		Configuration configuration;

		Resource configLocation = ReflectUtil.getFieldValue(this, "configLocation");
		Properties configurationProperties = ReflectUtil.getFieldValue(this, "configurationProperties");
		ObjectFactory objectFactory = ReflectUtil.getFieldValue(this, "objectFactory");
		ObjectWrapperFactory objectWrapperFactory = ReflectUtil.getFieldValue(this, "objectWrapperFactory");
		String typeAliasesPackage = ReflectUtil.getFieldValue(this, "typeAliasesPackage");
		Class<?> typeAliasesSuperType = ReflectUtil.getFieldValue(this, "typeAliasesSuperType");
		Class<?>[] typeAliases = ReflectUtil.getFieldValue(this, "typeAliases");
		Interceptor[] plugins = ReflectUtil.getFieldValue(this, "plugins");
		String typeHandlersPackage = ReflectUtil.getFieldValue(this, "typeHandlersPackage");
		TypeHandler<?>[] typeHandlers = ReflectUtil.getFieldValue(this, "typeHandlers");
		TransactionFactory transactionFactory = ReflectUtil.getFieldValue(this, "transactionFactory");
		String $environment = ReflectUtil.getFieldValue(this, "environment");
		DataSource dataSource = ReflectUtil.getFieldValue(this, "dataSource");
		DatabaseIdProvider databaseIdProvider = ReflectUtil.getFieldValue(this, "databaseIdProvider");
		Resource[] mapperLocations = ReflectUtil.getFieldValue(this, "mapperLocations");
		SqlSessionFactoryBuilder sqlSessionFactoryBuilder = ReflectUtil.getFieldValue(this, "sqlSessionFactoryBuilder");
		Log logger = ReflectUtil.getFieldValue(this, "logger");

		DTOXMLConfigBuilder xmlConfigBuilder = null;
		if (configLocation != null) {
			xmlConfigBuilder = new DTOXMLConfigBuilder(configLocation.getInputStream(), null, configurationProperties);
			configuration = xmlConfigBuilder.getConfiguration();
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Property 'configLocation' not specified, using default MyBatis Configuration");
			}
			configuration = new Configuration();
			configuration.setVariables(configurationProperties);
		}

		if (objectFactory != null) {
			configuration.setObjectFactory(objectFactory);
		}

		if (objectWrapperFactory != null) {
			configuration.setObjectWrapperFactory(objectWrapperFactory);
		}

		if (hasLength(typeAliasesPackage)) {
			String[] typeAliasPackageArray = tokenizeToStringArray(typeAliasesPackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
			for (String packageToScan : typeAliasPackageArray) {
				configuration.getTypeAliasRegistry().registerAliases(packageToScan,
						typeAliasesSuperType == null ? Object.class : typeAliasesSuperType);
				if (logger.isDebugEnabled()) {
					logger.debug("Scanned package: '" + packageToScan + "' for aliases");
				}
			}
		}

		if (!isEmpty(typeAliases)) {
			for (Class<?> typeAlias : typeAliases) {
				configuration.getTypeAliasRegistry().registerAlias(typeAlias);
				if (logger.isDebugEnabled()) {
					logger.debug("Registered type alias: '" + typeAlias + "'");
				}
			}
		}

		if (!isEmpty(plugins)) {
			for (Interceptor plugin : plugins) {
				configuration.addInterceptor(plugin);
				if (logger.isDebugEnabled()) {
					logger.debug("Registered plugin: '" + plugin + "'");
				}
			}
		}

		if (hasLength(typeHandlersPackage)) {
			String[] typeHandlersPackageArray = tokenizeToStringArray(typeHandlersPackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
			for (String packageToScan : typeHandlersPackageArray) {
				configuration.getTypeHandlerRegistry().register(packageToScan);
				if (logger.isDebugEnabled()) {
					logger.debug("Scanned package: '" + packageToScan + "' for type handlers");
				}
			}
		}

		if (!isEmpty(typeHandlers)) {
			for (TypeHandler<?> typeHandler : typeHandlers) {
				configuration.getTypeHandlerRegistry().register(typeHandler);
				if (logger.isDebugEnabled()) {
					logger.debug("Registered type handler: '" + typeHandler + "'");
				}
			}
		}

		if (xmlConfigBuilder != null) {
			try {
				xmlConfigBuilder.parse();

				if (logger.isDebugEnabled()) {
					logger.debug("Parsed configuration file: '" + configLocation + "'");
				}
			} catch (Exception ex) {
				throw new NestedIOException("Failed to parse config resource: " + configLocation, ex);
			} finally {
				ErrorContext.instance().reset();
			}
		}

		if (transactionFactory == null) {
			transactionFactory = new SpringManagedTransactionFactory();
		}

		Environment environment = new Environment($environment, transactionFactory, dataSource);
		configuration.setEnvironment(environment);

		if (databaseIdProvider != null) {
			try {
				configuration.setDatabaseId(databaseIdProvider.getDatabaseId(dataSource));
			} catch (SQLException e) {
				throw new NestedIOException("Failed getting a databaseId", e);
			}
		}

		if (!isEmpty(mapperLocations)) {
			for (Resource mapperLocation : mapperLocations) {
				if (mapperLocation == null) {
					continue;
				}

				try {
					XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation.getInputStream(), configuration,
							mapperLocation.toString(), configuration.getSqlFragments());
					xmlMapperBuilder.parse();
				} catch (Exception e) {
					throw new NestedIOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
				} finally {
					ErrorContext.instance().reset();
				}

				if (logger.isDebugEnabled()) {
					logger.debug("Parsed mapper file: '" + mapperLocation + "'");
				}
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Property 'mapperLocations' was not specified or no matching resources found");
			}
		}

		return sqlSessionFactoryBuilder.build(configuration);
	}

}
