package com.aero.ops.entity.dao;

import com.aero.ops.entity.po.DevicePO;
import com.aero.ops.entity.po.DevicePOExample;
import com.aero.ops.entity.po.DevicePOWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DevicePOMapper {
    long countByExample(DevicePOExample example);

    int deleteByExample(DevicePOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(DevicePOWithBLOBs record);

    int insertSelective(DevicePOWithBLOBs record);

    List<DevicePOWithBLOBs> selectByExampleWithBLOBs(DevicePOExample example);

    List<DevicePO> selectByExample(DevicePOExample example);

    DevicePOWithBLOBs selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DevicePOWithBLOBs record, @Param("example") DevicePOExample example);

    int updateByExampleWithBLOBs(@Param("record") DevicePOWithBLOBs record, @Param("example") DevicePOExample example);

    int updateByExample(@Param("record") DevicePO record, @Param("example") DevicePOExample example);

    int updateByPrimaryKeySelective(DevicePOWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(DevicePOWithBLOBs record);

    int updateByPrimaryKey(DevicePO record);

    /**
     * 查询默认分页数据，包含长字段
     * @param map 分页参数
     * @return
     */
    List<DevicePOWithBLOBs> queryByPageWithBLOBs(@Param("map") Map<String, Integer> map);

    /**
     * 查询默认分页数据
     * @param map 分页参数
     * @return
     */
    List<DevicePO> queryByPage(@Param("map") Map<String, Integer> map);

    /**
     * 查询并分页
     * @param
     * @return
     */
    List<DevicePO> selectByExampleWithPage(@Param("sql") String sql, @Param("offset") int offset, @Param("end") int end);
}