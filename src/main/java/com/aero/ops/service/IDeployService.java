package com.aero.ops.service;

import com.aero.ops.entity.po.ServerPO;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.CountDownLatch;

/**
 * @author 罗涛
 * @title IDeployService
 * @date 2020/7/7 19:55
 */
public interface IDeployService {

    void codeDownload(WebSocketSession session) throws Exception;

    void compile(WebSocketSession session) throws Exception;

    void assemble(WebSocketSession session) throws Exception;

    void copy2Target(WebSocketSession session) throws Exception;

    void run(WebSocketSession session) throws Exception;

    void startUpLogMonitor(WebSocketSession session) throws Exception;

    void kafkaStartUpLogListen(WebSocketSession session, ServerPO server, CountDownLatch latch) throws Exception;
}
