package com.tandong.workhour.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtil {
	/**
	 * 利用反射获取指定对象的指定属性
	 * 
	 * @param obj
	 *            目标对象
	 * @param fieldName
	 *            目标属性
	 * @return 目标属性的值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Object obj, String fieldName) {
		Object result = null;
		Field field = ReflectUtil.getField(obj, fieldName);
		if (field != null) {
			field.setAccessible(true);
			try {
				result = field.get(obj);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (T) result;
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Object obj, String methodName, boolean supportPrivateMethod, Object... methodParameters) {
		Object result = null;
		Class<?>[] parameters = null;
		if (methodParameters != null) {
			parameters = new Class<?>[methodParameters.length];
			for (int i = 0; i < methodParameters.length; i++) {
				parameters[i] = methodParameters[i].getClass();
			}
		}
		Method method = ReflectUtil.getMethod(obj, methodName, parameters);
		if (method != null) {
			if (supportPrivateMethod) {
				method.setAccessible(true);
			}
			try {
				if (methodParameters == null) {
					result = method.invoke(obj);
				} else {
					result = method.invoke(obj, methodParameters);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return (T) result;
	}

	/**
	 * 利用反射获取指定对象里面的指定属性
	 * 
	 * @param obj
	 *            目标对象
	 * @param fieldName
	 *            目标属性
	 * @return 目标字段
	 */
	private static Field getField(Object obj, String fieldName) {
		Field field = null;
		for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				field = clazz.getDeclaredField(fieldName);
				break;
			} catch (NoSuchFieldException e) {
				// 这里不用做处理，子类没有该字段可能对应的父类有，都没有就返回null。
			}
		}
		return field;
	}

	public static Method getMethod(Object obj, String methodName, Class<?>... methodParameters) {
		Method method = null;
		for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				if (methodParameters == null) {
					method = clazz.getDeclaredMethod(methodName);
				} else {
					method = clazz.getDeclaredMethod(methodName, methodParameters);
				}
				break;
			} catch (NoSuchMethodException e) {
				// 这里不用做处理，子类没有该字段可能对应的父类有，都没有就返回null。
			}
		}
		return method;
	}

	/**
	 * 利用反射设置指定对象的指定属性为指定的值
	 * 
	 * @param obj
	 *            目标对象
	 * @param fieldName
	 *            目标属性
	 * @param fieldValue
	 *            目标值
	 */
	public static void setFieldValue(Object obj, String fieldName, Object fieldValue) {
		Field field = ReflectUtil.getField(obj, fieldName);
		if (field != null) {
			try {
				field.setAccessible(true);
				field.set(obj, fieldValue);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}