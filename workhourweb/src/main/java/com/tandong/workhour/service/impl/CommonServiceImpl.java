package com.tandong.workhour.service.impl;

import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tandong.workhour.dao.CommonDao;
import com.tandong.workhour.entity.DTO;
import com.tandong.workhour.service.CommonService;

@Service
public class CommonServiceImpl implements CommonService {

	@Autowired
	private CommonDao commonDao;
	
	@Override
	public List<T> findObjectList(DTO dto) {
		return commonDao.findObjectList(dto);
	}

	@Override
	public T findObject(DTO dto) {
		return commonDao.findObject(dto);
	}

	@Override
	public Integer findCount(DTO dto) {
		return commonDao.findCount(dto);
	}

	@Override
	public void addObject(DTO dto) {
		commonDao.addObject(dto);
	}

	@Override
	public void batchAddObject(DTO dto) {
		commonDao.batchAddObject(dto);
	}

	@Override
	public void updateObject(DTO dto) {
		commonDao.updateObject(dto);
	}

	@Override
	public void deleteObject(DTO dto) {
		commonDao.deleteObject(dto);
	}

}
