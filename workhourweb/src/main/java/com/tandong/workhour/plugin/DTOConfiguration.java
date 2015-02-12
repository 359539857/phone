/**
 * 
 */
package com.tandong.workhour.plugin;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import com.tandong.workhour.entity.DTO;
import com.tandong.workhour.utils.ReflectUtil;

/**
 * @author PeterTan
 * 
 */
public class DTOConfiguration extends Configuration {

	@Override
	public MetaObject newMetaObject(Object object) {

		MetaObject metaoObject = MetaObject.forObject(object, objectFactory, objectWrapperFactory);
		if (object instanceof DTO) {
			ReflectUtil.setFieldValue(metaoObject, "objectWrapper", new DTOWrapper(metaoObject, (DTO) object));
		}
		return metaoObject;
	}
}
