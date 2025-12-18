package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> setmealVOS = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(setmealVOS.getTotal(), setmealVOS.getResult());
    }

    /**
     * 新增套餐与相关套餐菜品
     *
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void saveWithSetmealDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //插入套餐数据
        setmealMapper.insert(setmeal);
        Long setmealId = setmeal.getId();
        //获取套餐相关菜品，设置套餐id
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        //批次插入
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 根据id查询套餐与套餐关系菜品
     *
     * @param id
     * @return
     */
    @Override
    public SetmealVO getByIdWithDishes(Long id) {
        //根据id获取套餐数据
        Setmeal setmeal = setmealMapper.getById(id);
        //根据套餐id获取关系的菜品数据
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(setmeal.getId());

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐与关系的菜品
     *
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void updateWithDishes(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //修改套餐数据
        setmealMapper.update(setmeal);
        //批量删除套餐关系菜品
        Long setmealId = setmealDTO.getId();
        setmealDishMapper.deleteBySetmealId(setmealId);
        //批次插入新的套餐关系菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 批量删除套餐与关联菜品
     *
     * @param ids
     */
    @Transactional
    @Override
    public void deleteBatchWithDish(List<Long> ids) {
        //批量删除套餐数据
        setmealMapper.deleteBatch(ids);
        //批量删除套餐关系菜品
        setmealDishMapper.deleteBatchBySetmealId(ids);
    }

    /**
     * 根据id起售、停售商品
     *
     * @param id
     * @param status
     */
    @Override
    public void updateStatusById(Long id, Integer status) {
        //如果是启售套餐，判断套餐内是否有停售菜品
        if (status == StatusConstant.ENABLE) {
            //根据套餐id获取关系菜品
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            dishList.forEach(dish -> {
                if (dish.getStatus() == StatusConstant.DISABLE) {
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            });
        }
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }
}
