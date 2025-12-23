package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {


    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐与相关套餐菜品
     * @param setmealDTO
     */
    void saveWithSetmealDish(SetmealDTO setmealDTO);

    /**
     * 根据id查询套餐和套餐关系菜品
     * @param id
     * @return
     */
    SetmealVO getByIdWithDishes(Long id);

    /**
     * 修改套餐与关系的菜品
     * @param setmealDTO
     */
    void updateWithDishes(SetmealDTO setmealDTO);

    /**
     * 批量删除套餐和关系菜品
     * @param ids
     */
    void deleteBatchWithDish(List<Long> ids);

    /**
     * 根据id起售、停售商品
     * @param id
     * @param status
     */
    void updateStatusById(Long id, Integer status);

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    List<Setmeal> listByCategoryId(Long categoryId);

    /**
     * 根据套餐id查询包含菜品
     * @param setmealId
     * @return
     */
    List<DishItemVO> getDishesBySetmealId(Long setmealId);
}
