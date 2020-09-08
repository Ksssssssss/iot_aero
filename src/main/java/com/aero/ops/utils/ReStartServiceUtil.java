package com.aero.ops.utils;

import com.aero.common.build.order.GitCmd;
import com.aero.common.utils.EncryptUtil;
import com.aero.common.utils.FileUtil;
import com.aero.ops.entity.po.ServerPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * @author ksssss
 * @Date: 20-9-8 14:38
 * @Description:
 */
@Slf4j
public class ReStartServiceUtil {
    public static String buildRemoteReStartOrder(String serverName, ServerPO serverPO) {
        String userName = serverPO.getUserName();
        String encPwd = serverPO.getPassword();
        String pwd = EncryptUtil.decrypt(encPwd);
        String lanIp = serverPO.getLanIp();

        StringBuilder sb = new StringBuilder();
        sb.append("@echo off \r\n");
        sb.append("PsExec.exe \\\\")
                .append(lanIp)
                .append(" -u ")
                .append(userName)
                .append(" -p ")
                .append(pwd).append(" ");
        sb.append("sc start ");
        sb.append(serverName);
        sb.append("\r\n");
        sb.append("exit");
        return sb.toString();
    }

    public static String buildReStartOrder(String serverName) {
        StringBuilder sb = new StringBuilder();
        sb.append("@echo off \r\n");
        sb.append("sc start ");
        sb.append(serverName);
        sb.append("\r\n");
        sb.append("exit");
        return sb.toString();
    }

    public static Process execBat(String projectPath, String cmd) {
        String uuid = UUID.randomUUID().toString();
        String batPath = StringUtils.join(projectPath, "\\", uuid, ".bat");
        GeneratorBatFile(cmd, batPath);

        Process process = execBatCore(batPath);
        FileUtil.deleteFile(batPath);
        return process;
    }

    private static Process execBatCore(String batPath) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(batPath);
            process.waitFor();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return process;
    }

    private static void GeneratorBatFile(String cmd, String batPath) {
        try (FileWriter writer = new FileWriter(batPath)) {
            writer.write(cmd);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
