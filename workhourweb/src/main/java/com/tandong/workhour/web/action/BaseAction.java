/**
 * 
 */
package com.tandong.workhour.web.action;

import org.apache.log4j.Logger;
import org.redmine.ta.beans.User;

import com.tandong.workhour.plugin.WrapRequest;
import com.tandong.workhour.utils.PropertyConfigurer;

/**
 * @author PeterTan
 * 
 */
public class BaseAction extends WrapRequest {

	protected Logger logger = Logger.getLogger(getClass());

	protected String redmine_url = PropertyConfigurer
			.getContextPropertyStr("redmine_url");

	public String getUserNmae() {
		return ((User) request.getSession().getAttribute("user")).getLogin();
	}

	public String getPassword() {
		return ((User) request.getSession().getAttribute("user")).getPassword();
	}

}
