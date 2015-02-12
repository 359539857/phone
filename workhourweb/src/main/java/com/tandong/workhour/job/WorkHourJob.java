/**
 * 
 */
package com.tandong.workhour.job;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.redmine.ta.RedmineException;
import org.redmine.ta.beans.Issue;
import org.redmine.ta.beans.Project;
import org.redmine.ta.beans.TimeEntry;
import org.redmine.ta.beans.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tandong.workhour.entity.DTO;
import com.tandong.workhour.plugin.redmine.WorkRemineManager;
import com.tandong.workhour.service.CommonService;
import com.tandong.workhour.utils.DataMap;
import com.tandong.workhour.utils.PropertyConfigurer;
import com.tandong.workhour.utils.TDEncryptUtils;

/**
 * @author PeterTan
 * 
 */
@Component("workHourJob")
public class WorkHourJob {

	private static Logger logger = Logger.getLogger(WorkHourJob.class);

	private String uri = PropertyConfigurer.getContextPropertyStr("redmine_url");

	@Autowired
	private CommonService<DTO> service;

	protected void collectData() {
		logger.info("正在同步服务器数据...");

		long startTime = new Date().getTime();
		try {
			List<DTO> userList = service.findObjectList(new DTO().addValue("operatetype", "getUserList"));
			if (userList == null || userList.isEmpty()) {
				return;
			}
			WorkRemineManager mgr = new WorkRemineManager(uri, userList.get(0).getValue("username").toString(), TDEncryptUtils.decodeBase64String(userList.get(0).getValue("password")
					.toString()));
			mgr.setObjectsPerPage(1000);
			// 正在同步项目
			logger.info("正在同步服务器项目...");
			List<Project> projectList = mgr.getProjects();
			for (Project pro : projectList) {
				try {
					Integer count = service.findCount(new DTO().addValue("operatetype", "getProject").addValue("id", pro.getId()));
					if (count <= 0) {
						service.addObject(new DTO().addValue("operatetype", "addProject").addValue("id", pro.getId()).addValue("name", pro.getName())
								.addValue("description", pro.getDescription()).addValue("identifier", pro.getIdentifier()).addValue("createTime", pro.getCreatedOn())
								.addValue("updateTime", pro.getUpdatedOn()));
					}
				} catch (Exception e) {
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}
			}
			logger.info("项目同步完成。");

			logger.info("正在同步服务器问题...");
			// mgr.getIssuesBySummary(projectKey, summaryField)
			List<Issue> issueList = mgr.getIssues(new DataMap());
			if (issueList != null && !issueList.isEmpty()) {
				for (Issue issue : issueList) {
					try {
						int count = service.findCount(new DTO().addValue("operatetype", "getIssue").addValue("id", issue.getId()));
						if (count <= 0) {
							service.addObject(new DTO().addValue("operatetype", "addIssue").addValue("projectid", issue.getProject().getId()).addValue("id", issue.getId()).addValue("parent", issue.getParentId())
									.addValue("status", issue.getStatusName()).addValue("priority", issue.getPriorityText()).addValue("subject", issue.getSubject())
									.addValue("assignedtoId", issue.getAssignee() == null ? 0 : issue.getAssignee().getId())
									.addValue("authorId", issue.getAuthor() == null ? 0 : issue.getAuthor().getId()).addValue("startdate", issue.getStartDate()));

						}
					} catch (Exception e) {
						logger.error(ExceptionUtils.getFullStackTrace(e));
					}
				}
			}
			logger.info("问题同步完成。");

			logger.info("正在同步服务器工时...");
			List<TimeEntry> timeEntryList = mgr.getTimeEntries();
			if (timeEntryList != null && !timeEntryList.isEmpty()) {
				for (TimeEntry entry : timeEntryList) {
					for (DTO dtouser : userList) {
						if (entry.getUserId().equals(Integer.valueOf(dtouser.getValue("id").toString()))) {
							try {
								int count = service.findCount(new DTO().addValue("operatetype", "getHour").addValue("id", entry.getId()));
								if (count <= 0) {
									service.addObject(new DTO().addValue("operatetype", "addHour").addValue("id", entry.getId()).addValue("issueId", entry.getIssueId())
											.addValue("projectId", entry.getProjectId()).addValue("projectName", entry.getProjectName())
											.addValue("userName", dtouser.getValue("username")).addValue("activityName", entry.getActivityName())
											.addValue("hour", entry.getHours()).addValue("comment", entry.getComment()).addValue("createdOn", entry.getCreatedOn())
											.addValue("spentOn", entry.getSpentOn()).addValue("updatedOn", entry.getUpdatedOn()));
									continue;
								}
							} catch (Exception e) {
								logger.error(ExceptionUtils.getFullStackTrace(e));
							}

						}
					}
				}
				logger.info("工时同步完成。");
				long endTime = new Date().getTime();
				long consumeTime = endTime - startTime;
				logger.info("本次数据同步总耗时：" + consumeTime + "毫秒");

			}
		} catch (RedmineException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} catch (IOException e1) {
			logger.error(ExceptionUtils.getFullStackTrace(e1));
		}
	}

}
