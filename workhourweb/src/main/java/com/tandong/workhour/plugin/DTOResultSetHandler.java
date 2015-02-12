package com.tandong.workhour.plugin;
//package com.liantong.module.plugin;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.ibatis.executor.Executor;
//import org.apache.ibatis.executor.loader.ResultLoaderMap;
//import org.apache.ibatis.executor.parameter.ParameterHandler;
//import org.apache.ibatis.executor.resultset.FastResultSetHandler;
//import org.apache.ibatis.mapping.BoundSql;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.mapping.ResultMap;
//import org.apache.ibatis.mapping.ResultMapping;
//import org.apache.ibatis.session.ResultHandler;
//import org.apache.ibatis.session.RowBounds;
//
//import com.liantong.module.entity.DTO;
//
///**
// * @author PeterTan
// * 
// */
//public class DTOResultSetHandler extends FastResultSetHandler {
//
//	public DTOResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler resultHandler,
//			BoundSql boundSql, RowBounds rowBounds) {
//		super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
//	}
//
//	@Override
//	protected Object createResultObject(ResultSet rs, ResultMap resultMap, ResultLoaderMap lazyLoader, String columnPrefix,
//			ResultColumnCache resultColumnCache) throws SQLException {
//		final List<Class<?>> constructorArgTypes = new ArrayList<Class<?>>();
//		final List<Object> constructorArgs = new ArrayList<Object>();
//		final Object resultObject = createResultObject(rs, resultMap, constructorArgTypes, constructorArgs, columnPrefix, resultColumnCache);
//		if (resultObject != null && configuration.isLazyLoadingEnabled() && !typeHandlerRegistry.hasTypeHandler(resultMap.getType())) {
//			final List<ResultMapping> propertyMappings = resultMap.getPropertyResultMappings();
//			for (ResultMapping propertyMapping : propertyMappings) {
//				if (propertyMapping.getNestedQueryId() != null) { // issue #109
//																	// (avoid
//																	// creating
//																	// proxies
//																	// for leaf
//																	// objects)
//					Object obj = proxyFactory.createProxy(resultObject, lazyLoader, configuration, objectFactory, constructorArgTypes,
//							constructorArgs);
//					if (obj instanceof DTO) {
//						copyResultMap(resultMap, obj);
//					}
//					return obj;
//				}
//			}
//		}
//		return resultObject;
//	}
//
//	@Override
//	protected Object createResultObject(ResultSet rs, ResultMap resultMap, List<Class<?>> constructorArgTypes, List<Object> constructorArgs,
//			String columnPrefix, ResultColumnCache resultColumnCache) throws SQLException {
//		final Class<?> resultType = resultMap.getType();
//		final List<ResultMapping> constructorMappings = resultMap.getConstructorResultMappings();
//		if (typeHandlerRegistry.hasTypeHandler(resultType)) {
//			return createPrimitiveResultObject(rs, resultMap, columnPrefix, resultColumnCache);
//		} else if (constructorMappings.size() > 0) {
//			return createParameterizedResultObject(rs, resultType, constructorMappings, constructorArgTypes, constructorArgs, columnPrefix,
//					resultColumnCache);
//		} else {
//			Object obj = objectFactory.create(resultType);
//			if (obj instanceof DTO) {
//				return copyResultMap(resultMap, obj);
//			}
//			return objectFactory.create(resultType);
//		}
//	}
//
//	protected Object copyResultMap(ResultMap resultMap, Object obj) throws SQLException {
//		DTO dto = (DTO) obj;
//		for (ResultMapping resultMapping : resultMap.getResultMappings()) {
//			dto.addValue(resultMapping.getProperty(), resultMapping.getJavaType());
//		}
//		return dto;
//	}
//}