package com.aero.ops.service.impl;

import com.aero.common.build.BatCallUtil;
import com.aero.common.build.order.GradleCmd;
import com.aero.common.constants.DateFormat;
import com.aero.common.utils.DateTimeUtil;
import com.aero.common.utils.EncryptUtil;
import com.aero.common.utils.FileUtil;
import com.aero.ops.entity.po.ServerPO;
import com.aero.ops.entity.dto.ProjectDeployInfo;
import com.aero.ops.service.IDeployService;
import com.aero.ops.service.IServerService;
import com.aero.ops.service.KafkaStartLogClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author 罗涛
 * @title DeployServiceImpl
 * @date 2020/7/7 19:56
 */
@Slf4j
@Service
public class DeployServiceImpl implements IDeployService {
    public static final String LOGGER_PATH = "log";
    public static final String LOGGER_FILENAME = "sys-info.log";
//    public static final String DEFAULT_CODE_PATH = "C:\\Users\\Administrator\\deploy\\code";
//    public static final String DEFAULT_DEPLOY_PATH = "C:\\Users\\Administrator\\deploy\\target";

    @Autowired
    IServerService serverService;

    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServers;

    @Value("${app.start.timeOut}")
    long startTimeOut;

    @Override
    public void codeDownload(WebSocketSession session) throws Exception {
        try {
            ProjectDeployInfo deployInfo = (ProjectDeployInfo) session.getAttributes().get("deployInfo");
            //如果要部署的服务器不包含本机，代码的下载，编译过程仍然在本机进行，路径为默认路径
            String codeBasePath = getCodeBasePath(deployInfo);
            String projectName = deployInfo.getProjectName();
            String branch = deployInfo.getBranch();
            String codeLibrary = deployInfo.getCodeLibrary();
            String gitOperationPath = StringUtils.joinWith("\\", codeBasePath, projectName);
            File file = new File(gitOperationPath);
            String cmd = BatCallUtil.buildGitCloneOrder(codeBasePath, branch, codeLibrary);
            if (file.exists()) {
                //如果句柄被占用，无法删除时，没有报错，此环节需要完善 TODO
                FileUtil.deleteFolder(gitOperationPath);
            }
            log.info("执行下载代码脚本：{}", cmd);
            execBat(codeBasePath, cmd, session);
            session.sendMessage(new TextMessage("##SUCCESS##: code download !!!"));
        } catch (Exception e) {
            handleError(e, session);
        }
    }


    @Override
    public void compile(WebSocketSession session) throws Exception {
        try {
            ProjectDeployInfo deployInfo = (ProjectDeployInfo) session.getAttributes().get("deployInfo");
            //如果要部署的服务器不包含本机，代码的下载，编译过程仍然在本机进行，路径为默认路径
            String codeBasePath = getCodeBasePath(deployInfo);
            String projectName = deployInfo.getProjectName();
            String projectPath = StringUtils.joinWith("\\", codeBasePath, projectName);
            String cleanCmd = BatCallUtil.buildGradlewOrder(projectPath, GradleCmd.CLEAN);
            log.info("执行clean脚本：{}", cleanCmd);
            execBat(projectPath, cleanCmd, session);
            session.sendMessage(new TextMessage("##SUCCESS##: code compile !!!"));
        } catch (Exception e) {
            handleError(e, session);
        }
    }


    @Override
    public void assemble(WebSocketSession session) throws Exception {
        try {
            ProjectDeployInfo deployInfo = (ProjectDeployInfo) session.getAttributes().get("deployInfo");
            String codeBasePath = getCodeBasePath(deployInfo);
            String projectName = deployInfo.getProjectName();
            String projectPath = StringUtils.joinWith("\\", codeBasePath, projectName);
            String buildCmd = BatCallUtil.buildGradlewOrder(projectPath, GradleCmd.ASSEMBLE);
            log.info("执行assemble脚本：{}", buildCmd);
            execBat(projectPath, buildCmd, session);
            session.sendMessage(new TextMessage("##SUCCESS##: project assemble !!!"));
        } catch (Exception e) {
            handleError(e, session);
        }
    }

    @Override
    public void copy2Target(WebSocketSession session) throws Exception {
        try {
            ProjectDeployInfo deployInfo = (ProjectDeployInfo) session.getAttributes().get("deployInfo");
            String codeBasePath = getCodeBasePath(deployInfo);
            List<ServerPO> servers = deployInfo.getMachines();
            String localIp = System.getProperty("local-ip");
            //正式环境下需要遍历所有服务器
            for (ServerPO server : servers) {
                String targetBasePath = server.getDeployBasePath();
                String projectName = deployInfo.getProjectName();
                String projectPath = StringUtils.joinWith("\\", codeBasePath, projectName);
                String gradleJarPath = "build\\libs";
                String targetServicePath = StringUtils.joinWith("\\", targetBasePath, projectName);
                //目标服务文件夹 当文件重复时，如何处理？好像是直接覆盖了
                //目前只考虑了本机的情况，如果是集群，要考虑远程复制，远程启动 TODO
                log.info("执行文件脚本拷贝,ip = {}", server.getLanIp());
                if (!server.getLanIp().equals(localIp)) {
                    String remoteCopyOrder = buildRemoteCopyOrder(projectName, server, projectPath);
                    execBat(projectPath, remoteCopyOrder, session);
                    String tips = "项目文件拷贝成功，服务器：" + server.getLanIp();
                    session.sendMessage(new TextMessage(tips));
                } else {
                    BatCallUtil.copy2Target(projectPath, gradleJarPath, targetServicePath);
                    String tips = "项目文件拷贝成功，服务器：" + localIp;
                    session.sendMessage(new TextMessage(tips));
                }
            }


        } catch (Exception e) {
            handleError(e, session);
        }
    }

    @Override
    public void run(WebSocketSession session) throws Exception {
        try {
            ProjectDeployInfo deployInfo = (ProjectDeployInfo) session.getAttributes().get("deployInfo");
            String localIp = System.getProperty("local-ip");
            List<ServerPO> servers = deployInfo.getMachines();
            CountDownLatch latch = new CountDownLatch(servers.size());
            for (ServerPO server : servers) {
                String lanIp = server.getLanIp();
                String projectName = deployInfo.getProjectName();
                String targetBasePath = null;
                String targetServicePath = null;
                String startCmd = null;
                if (server.getLanIp().equals(localIp)) {
                    targetBasePath = server.getDeployBasePath();
                    targetServicePath = StringUtils.joinWith("\\", targetBasePath, projectName);
                    startCmd = BatCallUtil.buildStartOrder(targetServicePath);
                    log.info("执行部署脚本：ip = {}, cmd = {}", lanIp, startCmd);
                } else {
                    ServerPO thisServer = serverService.getServerByLanIp(localIp);
                    String remoteScriptFolder = StringUtils.joinWith("\\", thisServer.getDeployBasePath(), "remoteScript");
                    File file = new File(remoteScriptFolder);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    targetServicePath = remoteScriptFolder;
                    startCmd = buildRemoteStartOrder(projectName, server);
                    log.info("执行部署脚本：ip = {}, cmd = {}", lanIp, startCmd);
                }
                Process process = execBatWithoutLog(targetServicePath, startCmd);
                kafkaStartUpLogListen(session, server, latch);
            }
//            if (latch.await(startTimeOut*servers.size(),TimeUnit.MILLISECONDS)) {
            //如果所有的服务器都部署完成 就返回成功，多台服务器需要countDownLatch todo
            session.sendMessage(new TextMessage("##END##: service cluster deploy complete !!! success: " + (servers.size() - latch.getCount()) + ", fail: " + latch.getCount()));
            session.close();
//            }
        } catch (Exception e) {
            handleError(e, session);
        }
    }

    @Override
    public void startUpLogMonitor(WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage("<br><br>===============   startup log(1m)   ===============<br>"));
        ProjectDeployInfo deployInfo = (ProjectDeployInfo) session.getAttributes().get("deployInfo");
//        String targetBasePath = deployInfo.getTargetBasePath();
        String localIp = System.getProperty("local-ip");
        ServerPO server = getServerByLanIp(deployInfo, localIp);
        String targetBasePath = server.getDeployBasePath();
        String targetLogPath = StringUtils.joinWith("\\", targetBasePath, LOGGER_PATH);
        String targetLogFile = StringUtils.joinWith("\\", targetLogPath, LOGGER_FILENAME);
        File logFile = new File(targetLogFile);
        if (!logFile.exists()) {
            logFile.getParentFile().mkdirs();
            logFile.createNewFile();
        }
        String logMonitorCmd = BatCallUtil.buildLogMonitorOrder(targetLogFile);
        String batFileName = "logMonitor.bat";
        String logMonitorBatAbsPath = StringUtils.joinWith("\\", targetLogPath, batFileName);
        File batFile = new File(logMonitorBatAbsPath);
        if (!batFile.exists()) {
            batFile.getParentFile().mkdirs();
            batFile.createNewFile();
        }
        Process logMonitorProcess = execBat(targetLogPath, logMonitorCmd, batFileName);
        ExecutorService pool = Executors.newFixedThreadPool(1);
        pool.execute(() -> {
            InputStream in = logMonitorProcess.getInputStream();
            try {

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "GB2312"));
                while ((line = br.readLine()) != null) {
                    if (session.isOpen()) {
                        synchronized (session) {
                            session.sendMessage(new TextMessage(line));
                        }
                    } else {
                        //"taskkill /f /im tail.exe"
//                        logMonitorProcess.destroyForcibly();
                        String killTail = buildKillTailOrder();
                        execBatWithoutLog(".", killTail);
                        break;
                    }
                }
                if (logMonitorProcess != null && logMonitorProcess.isAlive()) {
                    logMonitorProcess.waitFor();
                }
            } catch (Exception e) {
                log.error("监听启动日志时发生异常， e = {}", e);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        ScheduledExecutorService schedule = new ScheduledThreadPoolExecutor(1);
        schedule.schedule(new Runnable() {
            @Override
            public void run() {
                try {
//                    logMonitorProcess.destroyForcibly();
                    String killTail = buildKillTailOrder();
                    execBatWithoutLog(".", killTail);
                    FileUtil.deleteFile(logMonitorBatAbsPath);
                    log.warn("关闭监听启动日志线程");
                    pool.shutdownNow();
                    log.warn("关闭部署日志socket连接");
                    session.close();
                } catch (Exception e) {
                    log.error("关闭监听启动日志线程和关闭websocket session时发生异常， e = {}", e);
                }
            }
        }, 60, TimeUnit.SECONDS);
    }


    @Override
    public void kafkaStartUpLogListen(WebSocketSession session, ServerPO server, CountDownLatch latch) throws Exception {
        session.sendMessage(new TextMessage("<br><br>===============   startup log(" + server.getLanIp() + ")   ===============<br>"));
        ProjectDeployInfo deployInfo = (ProjectDeployInfo) session.getAttributes().get("deployInfo");
        String monitorIp = server.getLanIp();
        String topic = StringUtils.join("elk-", deployInfo.getProjectName());
        KafkaStartLogClient kafkaClient = new KafkaStartLogClient(monitorIp, topic, startTimeOut, bootstrapServers, session, latch);
        kafkaClient.startLogMonitor();
    }

    public void execBat(String projectPath, String cmd, WebSocketSession session) throws Exception {
        String uuid = UUID.randomUUID().toString();
        String batPath = StringUtils.join(projectPath, "\\", uuid, ".bat");
        FileWriter fw = null;
        // 生成bat文件
        fw = new FileWriter(batPath);
        fw.write(cmd);
        fw.close();

        // 运行bat文件
        Process process = Runtime.getRuntime().exec(batPath);
        InputStream in = process.getInputStream();
        try {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "GB2312"));
            while ((line = br.readLine()) != null) {
//                log.error("部署日志：{}", line);
                session.sendMessage(new TextMessage(line));
            }
            process.waitFor();
            FileUtil.deleteFile(batPath);
        } catch (Exception e) {
            throw e;
        } finally {
            in.close();
        }
    }

    public Process execBat(String projectPath, String cmd, String batFileName) throws Exception {
        String batPath = StringUtils.joinWith("\\", projectPath, batFileName);
        FileWriter fw = null;
        // 生成bat文件
        fw = new FileWriter(batPath);
        fw.write(cmd);
        fw.close();

        // 运行bat文件
        Process process = Runtime.getRuntime().exec(batPath);
        return process;
    }

    public Process execBatWithoutLog(String projectPath, String cmd) throws Exception {
        String uuid = UUID.randomUUID().toString();
        String batPath = StringUtils.join(projectPath, "\\", uuid, ".bat");
        FileWriter fw = null;
        // 生成bat文件
        fw = new FileWriter(batPath);
        fw.write(cmd);
        fw.close();
        // 运行bat文件
//        String[] cmds = {"cmd.exe", "/k", cmd};
//        Process process = Runtime.getRuntime().exec(cmds);
        Process process = Runtime.getRuntime().exec(batPath);
        process.waitFor();
        FileUtil.deleteFile(batPath);
        return process;
    }


    private String formatErrorMsg(String error) {
        StringBuilder sb = new StringBuilder();
        sb.append("<span style='color:red;font-weight:bold'>");
        sb.append(error);
        sb.append("</span>");
        return sb.toString();
    }

    private void handleError(Exception e, WebSocketSession session) throws Exception {
        String error = e.getMessage();
        log.error("部署错误信息， error = {}", error);
        String errorMsg = formatErrorMsg(error);
        TextMessage textMessage = new TextMessage(errorMsg);
        synchronized (session) {
            session.sendMessage(textMessage);
        }
        session.close();
        e.printStackTrace();
        throw e;
    }

    private ServerPO getServerByLanIp(ProjectDeployInfo deployInfo, String lanIp) {
        List<ServerPO> machines = deployInfo.getMachines();
        for (ServerPO server : machines) {
            if (lanIp.equalsIgnoreCase(server.getLanIp())) {
                return server;
            }
        }
        return null;
    }

    private String getCodeBasePath(ProjectDeployInfo deployInfo) throws Exception {
        String localIp = System.getProperty("local-ip");
        ServerPO targetServer = getServerByLanIp(deployInfo, localIp);
        String codeBasePath = null;
        //如果要部署的服务器不包含本机，代码的下载，编译过程仍然在本机进行，路径为默认路径
        if (Objects.nonNull(targetServer)) {
            codeBasePath = targetServer.getCodeBasePath();
        } else {
            ServerPO thisServer = serverService.getServerByLanIp(localIp);
            if (Objects.nonNull(thisServer)) {
                codeBasePath = thisServer.getCodeBasePath();
            } else {
                throw new Exception("ip为" + localIp + "的服务器未加入管理列表");
            }
        }
        return codeBasePath;
    }

//    private String getAndCreateDefaultCodePath(){
//        StringBuilder sb = new StringBuilder();
//        sb.append("C:\\\\Users\\\\");
//        String username = System.getenv().get("USERNAME");
//        sb.append(username);
//        sb.append("\\\\deploy\\\\code");
//        String codePath = sb.toString();
//        File file = new File(codePath);
//        if(!file.exists()){
//            file.mkdirs();
//        }
//        return codePath;
//    }

    public static String buildRemoteCopyOrder(String projectName, ServerPO server, String thisCompileFolder) {
        String userName = server.getUserName();
        String encPwd = server.getPassword();
        String pwd = EncryptUtil.decrypt(encPwd);
        String lanIp = server.getLanIp();
        String deploySharePath = server.getDeploySharePath();
        String targetFolder = StringUtils.join(deploySharePath, "\\", projectName, "\\");

        String thisStartScript = StringUtils.joinWith("\\", thisCompileFolder, "start.bat");
        String thisProjectJar = StringUtils.joinWith("\\", thisCompileFolder, "build\\libs\\*.*");


        String sb = "@echo off \r\n" +
                /**建立远程连接*/
                "net use \\\\" +
                lanIp +
                "\\ipc$ " +
                pwd +
                " /user:" +
                userName +
                "\r\n" +

                /**远程创建项目部署路径*/
                "md " +
                targetFolder +
                "\r\n" +

                /**远程复制启动脚本*/
                "copy " +
                thisStartScript +
                " " +
                targetFolder +
                "\r\n" +

                /**远程项目jar包*/
                "copy " +
                thisProjectJar +
                " " +
                targetFolder +
                "\r\n" +

                /**关闭远程连接*/
                "net use \\\\" +
                lanIp +
                "\\ipc$ /delete" +
                "\r\n" +
                "exit";
        return sb;
    }

    //wmic方案 实践是可行的，只是配置复杂一点，可以保留
//    private String buildRemoteStartOrder(String projectName, ServerPO server){
//     如果执行脚本时出现wmic没有权限的问题，请参考:https:jingyan.baidu.com/article/db55b609f2be764ba20a2f60.html
//        String userName = server.getUserName();
//        String encPwd = server.getPassword();
//        String pwd = EncryptUtil.decrypt(encPwd);
//        String lanIp = server.getLanIp();
//        String deploySharePath = server.getDeploySharePath();
//        String targetFolder = StringUtils.join(deploySharePath, "\\", projectName, "\\");
//        String targetStartScript = StringUtils.join(targetFolder, "start.bat");
//
//
//        String sb = "@echo off \r\n" +
//                "wmic /node:" +
//                lanIp +
//                " /user:" +
//                userName +
//                " /password:" +
//                pwd +
//                " process call create \"cmd.exe /c start " +
//                targetStartScript +
//                "\"";
//        return sb;
//    }

    //psexec方案
    private String buildRemoteStartOrder(String projectName, ServerPO server) {
        // 执行这个脚本前，需要在winServer中下载PSTools，并将其放到环境变量中:https://download.sysinternals.com/files/PSTools.zip
        String userName = server.getUserName();
        String encPwd = server.getPassword();
        String pwd = EncryptUtil.decrypt(encPwd);
        String lanIp = server.getLanIp();
        String deploySharePath = server.getDeploySharePath();
        String targetFolder = StringUtils.join(deploySharePath, "\\", projectName, "\\");
        String targetStartScript = StringUtils.join(targetFolder, "start.bat");


        String cmd = "@echo off \r\n" +

                /**远程调用启动脚本*/
                "PsExec.exe \\\\" +
                lanIp +
                " -u " +
                userName +
                " -p " +
                pwd +
                " /accepteula -h cmd /c " +
                targetStartScript +
                "\r\n" +
                "exit";
        return cmd;
    }


    private String buildKillTailOrder() {
        return "@echo off\r\n taskkill /f /im tail.exe\r\n exit";
    }
}
