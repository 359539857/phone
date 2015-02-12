/**
 * 
 */
package com.tandong.workhour.utils;

import org.redmine.ta.beans.User;

/**
 * @author PeterTan
 * 
 */
public class ThreadLocalMap {

	private static ThreadLocal<User> threadLocal = new ThreadLocal<User>();

	public static void set(User o) {
		threadLocal.set(o);
	}

	public static User get() {
		return threadLocal.get();
	}

	public static void clear() {
		threadLocal.remove();
	}
}
