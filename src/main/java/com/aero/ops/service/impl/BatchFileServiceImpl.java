package com.aero.ops.service.impl;

import com.aero.common.build.BatCallUtil;
import com.aero.ops.service.IBatchFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 罗涛
 * @title BatchFileServiceImpl
 * @date 2020/8/28 15:28
 */

@Service
public class BatchFileServiceImpl implements IBatchFileService {
    @Override
    public List<String>  gitCloneAndBranches(String basePath, String projectName, String gitUrl) throws Exception {
        String gitCloneOrder = BatCallUtil.buildGitCloneOrder(basePath, "master", gitUrl);
        BatCallUtil.execBat(basePath, gitCloneOrder);
        String projectCodePath = StringUtils.joinWith("/", basePath, projectName);
        BatCallUtil.execBat(projectCodePath, gitBranchesOrder(projectCodePath));
        String branchFilesPath = StringUtils.joinWith("/", projectCodePath, "branches.log");
        return getAllRemoteBranches(branchFilesPath);
    }

    @Override
    public List<String> gitPruneAndBranches(String basePath) throws Exception {
        BatCallUtil.execBat(basePath, gitPruneOrder(basePath));
        BatCallUtil.execBat(basePath, gitBranchesOrder(basePath));
        String branchFilesPath = StringUtils.joinWith("/", basePath, "branches.log");
        return getAllRemoteBranches(branchFilesPath);
    }

    public List<String> getAllRemoteBranches(String branchFilesPath) throws Exception {
        File branchFile = new File(branchFilesPath);
        if (branchFile.exists()) {
            FileInputStream fileInputStream = new FileInputStream(branchFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
            List<String> branches = new ArrayList<>();
            String line = null;
            while((line = br.readLine()) != null) {
                String pointerSign = "remotes/origin/HEAD ->";
                String prefix = "remotes/origin/";
                if (StringUtils.startsWith(line, "*") || StringUtils.countMatches(line, pointerSign)>0) {
                    continue;
                }
                branches.add(StringUtils.substringAfter(line, prefix));
            }
            return branches;
        }else {
            return null;
        }
    }

    public String gitPruneOrder(String basePath){
        StringBuilder sb = new StringBuilder();
        sb.append("@echo off \r\n");
        sb.append("cd /d ");
        sb.append(basePath);
        sb.append("\r\n");
        sb.append("call git remote update origin --prune");
        sb.append("\r\n");
        sb.append("exit");
        return sb.toString();
    }

    public String gitBranchesOrder(String basePath){
        StringBuilder sb = new StringBuilder();
        sb.append("@echo off \r\n");
        sb.append("cd /d ");
        sb.append(basePath);
        sb.append("\r\n");
        sb.append("call git branch -a > branches.log");
        sb.append("\r\n");
        sb.append("exit");
        return sb.toString();
    }
}
