package com.dbmanagesys.manage.service.base;

import java.util.Date;
import java.util.List;

import com.dbmanagesys.manage.pojo.BasePojo;
import com.github.abel533.entity.Example;
import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * @author TJW
 *
 */
// 定义泛型 和泛型上限
public interface BaseService<T extends BasePojo> {

	public abstract Mapper<T> getMapper();

	/**
	 * 根据id查询数据
	 * 
	 * @author TJW
	 * @param id
	 * 
	 */
	public T queryById(Long id);

	/**
	 * 查询所有
	 * 
	 * @author TJW
	 * @param
	 * 
	 */
	public List<T> queryAll();

	/**
	 * 指定条件查询一条数据
	 * 
	 * @author TJW
	 * @param
	 * 
	 */
	public T queryOne(T record);

	/**
	 * 指定条件查询数据列表
	 * 
	 * @author TJW
	 * @param
	 * 
	 */
	public List<T> queryListByWhere(T record);

	/**
	 * 分页查询
	 * 
	 * @author TJW
	 * @param Integer
	 *            page, Integer rows,T record
	 * 
	 */
	public PageInfo<T> queryPageListByWhere(Integer page, Integer rows, T record);

	/**
	 * 新增数据 新增数据，返回成功的条数
	 * 
	 * @author TJW
	 * @param Integer
	 *            page, Integer rows,T record
	 * 
	 */

	public Integer save(T record);

	/**
	 * 新增数据，使用不为null的字段，返回成功的条数
	 * 
	 * @author TJW
	 * @param Integer
	 *            page, Integer rows,T record
	 * 
	 */

	public Integer insertSelective(T record);

	/**
	 * 修改数据
	 * 
	 * @author TJW
	 * 
	 * 
	 */

	public Integer update(T record);

	/**
	 * 修改数据，使用不为null的字段，返回成功的条数
	 * 
	 * @param record
	 * @return
	 */
	public Integer updateSelective(T record);

	/**
	 * 根据id删除数据（物理删除）
	 * 
	 * @param id
	 * @return
	 */
	public Integer deleteById(Long id);

	/**
	 * 批量删除
	 * 
	 * @param clazz
	 * @param property
	 * @param values
	 * @return
	 */
	public Integer deleteByIds(Class<T> clazz, String property, List<Object> values);

	/**
	 * 根据条件做删除
	 * 
	 * @param record
	 * @return
	 */

	public Integer deleteByWhrere(T record);

}
