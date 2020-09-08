package com.aero.ops.controller.websocket;

import com.aero.ops.entity.dto.ProjectDeployInfo;
import com.aero.ops.service.IDeployService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;

@Slf4j
@Component
public class DeployHandler extends AbstractWebSocketHandler {

    @Autowired
    IDeployService deployService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket连接建立成功！");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.warn("发生异常！");
        exception.printStackTrace();
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //主动关闭还没有结束的日志监控
        log.warn("session关闭！");
        super.afterConnectionClosed(session, status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String msg = message.getPayload();
            log.info("收到指令：{}", msg);
            String[] msgArray = StringUtils.split(msg, "*");
            if(msgArray.length<2){
                log.warn("指令格式错误！");
            }
            String cmd = msgArray[0];
            String deployInfoJson = msgArray[1];
            ProjectDeployInfo deployInfo = JSON.parseObject(deployInfoJson, ProjectDeployInfo.class);
            if("downloading".equalsIgnoreCase(cmd)){
                Map<String, Object> attr = session.getAttributes();
                attr.put("deployInfo", deployInfo);
                deployService.codeDownload(session);
            }else if("compiling".equalsIgnoreCase(cmd)){
                deployService.compile(session);
            }else if("packaging".equalsIgnoreCase(cmd)){
                deployService.assemble(session);
            }else if("deploying".equalsIgnoreCase(cmd)){
                deployService.copy2Target(session);
                deployService.run(session);
//                deployService.startUpLogMonitor(session);
            }
        } catch (Exception e) {
            log.error("部署过程发生异常：e => {}", e);
        }
    }
}
