package com.tandong.workhour.plugin;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BaseWrapper;

import com.tandong.workhour.entity.DTO;

public class DTOWrapper extends BaseWrapper {
	private DTO object;

	public DTOWrapper(MetaObject metaObject, DTO object) {
		super(metaObject);
		this.object = object;
	}

	public Object get(PropertyTokenizer prop) {
		if (prop.getIndex() != null) {
			Object collection = resolveCollection(prop, object.toMap());
			return getCollectionValue(prop, collection);
		} else {
			Object value = object.toMap().get(prop.getName());
			return  value instanceof DTO ? ((DTO)value).toMap() : value;
		}
	}

	public void set(PropertyTokenizer prop, Object value) {
		if (prop.getIndex() != null) {
			Object collection = resolveCollection(prop, object.toMap());
			setCollectionValue(prop, collection, value);
		} else {
			if(value instanceof DTO){
				object.toMap().put(prop.getName(), ((DTO)value).toMap());
			}
			else{
				object.toMap().put(prop.getName(), value);
			}
		}
	}

	public String findProperty(String name, boolean useCamelCaseMapping) {
		return name;
	}

	public Class<?> getSetterType(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
			if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
				return Object.class;
			} else {
				return metaValue.getSetterType(prop.getChildren());
			}
		} else {
			if (object.toMap().get(name) != null) {
				return object.toMap().get(name).getClass();
			} else {
				return Object.class;
			}
		}
	}

	public Class<?> getGetterType(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
			if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
				return Object.class;
			} else {
				return metaValue.getGetterType(prop.getChildren());
			}
		} else {
			if (object.toMap().get(name) != null) {
				return object.toMap().get(name).getClass();
			} else {
				return Object.class;
			}
		}
	}

	public boolean hasSetter(String name) {
		return true;
	}

	public boolean hasGetter(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			if (object.toMap().containsKey(prop.getIndexedName())) {
				MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
				if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
					return object.toMap().containsKey(name);
				} else {
					return metaValue.hasGetter(prop.getChildren());
				}
			} else {
				return false;
			}
		} else {
			return object.toMap().containsKey(prop.getName());
		}
	}

	public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		set(prop, map);
		return MetaObject.forObject(map, metaObject.getObjectFactory(), metaObject.getObjectWrapperFactory());
	}

	public boolean isCollection() {
		return false;
	}

	public void add(Object element) {
		throw new UnsupportedOperationException();
	}

	public <E> void addAll(List<E> list) {
		throw new UnsupportedOperationException();
	}

	public String[] getGetterNames() {
		return this.object.toMap().keySet().toArray(new String[this.object.toMap().keySet().size()]);
	}

	public String[] getSetterNames() {
		return this.object.toMap().keySet().toArray(new String[this.object.toMap().keySet().size()]);
	}

}
