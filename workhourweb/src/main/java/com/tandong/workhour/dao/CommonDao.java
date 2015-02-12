/**
 * 
 */
package com.tandong.workhour.dao;

import java.util.List;

import org.apache.poi.ss.formula.functions.T;

import com.tandong.workhour.entity.DTO;

/**
 * @author PeterTan
 * 
 */
public interface CommonDao {
	List<T> findObjectList(DTO dto);

	T findObject(DTO dto);

	Integer findCount(DTO dto);

	void addObject(DTO dto);

	void batchAddObject(DTO dto);

	void updateObject(DTO dto);

	void deleteObject(DTO dto);
}
