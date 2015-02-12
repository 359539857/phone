package com.tandong.workhour.service;

import java.util.List;

import com.tandong.workhour.entity.DTO;

public interface CommonService<T> {

	List<T> findObjectList(DTO dto);

	T findObject(DTO dto);

	Integer findCount(DTO dto);

	void addObject(DTO dto);

	void batchAddObject(DTO dto);

	void updateObject(DTO dto);

	void deleteObject(DTO dto);

}
