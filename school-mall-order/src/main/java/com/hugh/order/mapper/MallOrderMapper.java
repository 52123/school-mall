package com.hugh.order.mapper;

import com.hugh.order.entity.MallOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 52123
 * @since 2019/06/14 04:01
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