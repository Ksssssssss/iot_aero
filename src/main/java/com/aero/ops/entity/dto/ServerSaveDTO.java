package com.aero.ops.entity.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author 罗涛
 * @title DeployServer
 * @date 2020/7/9 20:41
 */
@Data
@Document(collection = "Server")
public class ServerSaveDTO {
    private String serverName;
    private String os;
    private String lanIp;
    private String wlanIp;
    private String codeBasePath;
    private String deployBasePath;
    private String deploySharePath;
    private String userName;
    private String password;
}
