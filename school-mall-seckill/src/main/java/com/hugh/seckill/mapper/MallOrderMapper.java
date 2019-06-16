package com.hugh.seckill.mapper;

import com.hugh.seckill.entity.MallOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 52123
 * @since 2019/06/17 12:57
 */
@Mapper
public interface MallOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MallOrder record);

    int insertSelective(MallOrder record);

    MallOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MallOrder record);

    int updateByPrimaryKey(MallOrder record);
}