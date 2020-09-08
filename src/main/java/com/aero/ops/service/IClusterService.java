package com.aero.ops.service;

import com.aero.ops.entity.dto.ClusterEditDTO;
import com.aero.ops.entity.dto.ClusterSaveDTO;
import com.aero.ops.entity.po.ClusterPO;

import java.util.List;

/**
 * @author 罗涛
 * @title IClusterService
 * @date 2020/8/11 11:38
 */
public interface IClusterService {

    List<ClusterPO> getAll();

    ClusterPO save(ClusterSaveDTO saveDTO);

    ClusterPO getById(String id);

    List<ClusterPO> getAllCluster();

    boolean delete(List<String> cids);

    boolean edit(ClusterEditDTO editDTO);

    boolean batchStatusUpdate(List<String> urls, boolean pingAccess);
}
