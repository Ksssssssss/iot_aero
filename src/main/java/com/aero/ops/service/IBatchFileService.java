package com.aero.ops.service;

import java.util.List;

/**
 * @author 罗涛
 * @title IBatchFileService
 * @date 2020/8/28 15:28
 */
public interface IBatchFileService {
    List<String> gitCloneAndBranches(String basePath, String projectName,  String gitUrl) throws Exception;

    List<String> gitPruneAndBranches(String basePath) throws Exception;
}
