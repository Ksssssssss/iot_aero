package com.aero.ops.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @author 罗涛
 * @title ReachableUtil
 * @date 2020/8/12 16:03
 */
@Slf4j
@Component
public class DingRobotUtil {
    @Value("${monitor.warning.script.path}")
    String robotScriptPath;

    public void sendDingTalkAlarm(String msg){
        try {
            if(StringUtils.countMatches(msg, ",")>0){
                msg = StringUtils.replace(msg, ",", " - ");
            }
            ensureRobotScript();
            // 运行bat文件
            String[] strings = new String[]{robotScriptPath, msg};
            Process process = Runtime.getRuntime().exec(strings);
            InputStream in = process.getInputStream();
            try {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(in,"GB2312"));
                while ((line = br.readLine()) != null) {
                    log.info("钉钉机器人消息发送结果：{}",line);
                }
                process.waitFor();
            } catch (Exception e){
                log.error("钉钉机器人发送告警时发生异常：warning = {}, error = {}", msg, e);
            }finally {
                in.close();
            }
        } catch (Exception e) {
            log.error("执行钉钉机器人脚本时发生异常：warning = {}, error = {}", msg, e);
        }
    }


    public void ensureRobotScript() throws Exception{
        File scrpitFile = new File(robotScriptPath);
        if(!scrpitFile.exists()){
            scrpitFile.getParentFile().mkdirs();
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("dingRobotWarning.bat");
            OutputStream os = new FileOutputStream(scrpitFile);
            try {
                while (is.available()>0) {
                    os.write(is.read());
                }
            } finally {
                is.close();
                os.close();
            }
        }
    }
}
