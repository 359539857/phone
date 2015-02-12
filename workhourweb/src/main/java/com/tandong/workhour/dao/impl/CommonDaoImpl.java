package com.tandong.workhour.dao.impl;

import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Repository;

import com.tandong.workhour.dao.CommonDao;
import com.tandong.workhour.entity.DTO;

@Repository
public class CommonDaoImpl extends BaseDao implements CommonDao {

	@Override
	public List<T> findObjectList(DTO dto) {
		return this.getSqlSession().selectList("findObjectList", dto);
	}

	@Override
	public T findObject(DTO dto) {
		return this.getSqlSession().selectOne("findObject", dto);
	}

	@Override
	public Integer findCount(DTO dto) {
		return this.getSqlSession().selectOne("findCount", dto);
	}

	@Override
	public void addObject(DTO dto) {
		this.getSqlSession().insert("addObject", dto);
	}

	@Override
	public void batchAddObject(DTO dto) {
		this.getSqlSession().insert("batchAddObject", dto);
	}

	@Override
	public void updateObject(DTO dto) {
		this.getSqlSession().update("updateObject", dto);
	}

	@Override
	public void deleteObject(DTO dto) {
		this.getSqlSession().delete("deleteObject", dto);
	}

}
