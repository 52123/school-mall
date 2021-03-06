package com.hugh.seckill.mapper;

import com.hugh.seckill.entity.MallSeckill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 52123
 * @since 2019/06/17 12:57
 */
@Mapper
public interface MallSecKillMapper {

    int selectSecKillCount(@Param("secKillId") long secKillId);

    int deleteByPrimaryKey(Long id);

    int insert(MallSeckill record);

    int insertSelective(MallSeckill record);

    MallSeckill selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MallSeckill record);

    int updateByPrimaryKey(MallSeckill record);
}