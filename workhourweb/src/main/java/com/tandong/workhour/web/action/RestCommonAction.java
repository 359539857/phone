package com.tandong.workhour.web.action;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tandong.workhour.entity.DTO;
import com.tandong.workhour.service.CommonService;

@RestController
public class RestCommonAction extends BaseAction {

	@Autowired
	private CommonService<DTO> service;

	@RequestMapping("restloadData")
	public Object loadData(DTO dto) {
		String operatetype = dto.getValue("operatetype");
		try {
			Object obj = null;
			if (operatetype.endsWith("Count")) {
				obj = service.findCount(dto);
			} else if (operatetype.endsWith("Object")) {
				obj = service.findObject(dto);
			} else {
				if (operatetype.indexOf("Page") != -1) {
					DTO page = new DTO();
					if (dto.containsKey("pageNo") && dto.getValue("pageNo") != null) {
						page.addValue("pageNo", Integer.valueOf(dto.getValue("pageNo").toString()));
					} else {
						page.addValue("pageNo", 1);
					}
					if (dto.containsKey("pageSize") && dto.getValue("pageSize") != null) {
						page.addValue("pageSize", Integer.valueOf(dto.getValue("pageSize").toString()));
					} else {
						page.addValue("pageSize", 10);
					}

					dto.addValue("page", page);
				}
				obj = service.findObjectList(dto);
			}
			dto.addValue("result", obj);
			dto.addValue("code", DTO.SUCCESS_CODE);

		} catch (Exception e) {
			dto.addValue("code", DTO.FAIL_CODE);
			dto.addValue("error", e.toString());
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}

		return dto.toAllMap();
	}
}
