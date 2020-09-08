package com.aero.ops.entity.po;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author 罗涛
 * @title AppLog
 * @date 2020/7/3 10:01
 */
@Data
@Accessors(chain = true)
@Document(indexName = "elk-bchd-acceptor", type = "_doc")
public class AppLog {
    //服务器ip
    private String serverIp;

    //服务器系统
//    private String os;

    //消息时间
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss,SSS")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss,SSS", timezone = "GMT+8")
    private String timestamp;

    //日志级别
    private String level;

    //项目名称
//    private String appName;

    //类名
    private String classname;

    //线程名
    private String thread;

    //消息内容
    private String content;
}
