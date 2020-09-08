package com.aero.ops.entity.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

/**
 * @author 罗涛
 * @title MongoDevicePO
 * @date 2020/7/31 14:05
 */

@Data
@Document(value = "Device")
public class MongoDevicePO {
    @Id
    private String _id;

    @Field("UUID")
    private String uuid;

    @Field("SENSOR_TYPE")
    private String sensorType;

    @Field("TIME_JOIN")
    private String timeJoin;

    @Field("TIME_RECENTLY")
    private String timeRecently;

    @Field("SENSOR_NAME")
    private String sensorName;

    @Field("STATUS")
    private String status;

    @Field("MAC")
    private String mac;

    @Field("GROUP_ID")
    private String groupId;

    @Field("AVATAR")
    private String avatar;

    @Field("MONGO_TABLE")
    private String mongoTable;

    @Field("PROJECT_UUID")
    private String projectUuid;

    @Field("IsStopped")
    private String isstopped;

    @Field("SecondCheckTime")
    private Integer secondchecktime;

    @Field("StartValue")
    private String startvalue;

    @Field("RuleId")
    private Integer ruleid;

    @Field("SerialNum")
    private String serialnum;

    @Field("Funcs")
    private String funcs;

    @Field("RealRate")
    private BigDecimal realrate;

    @Field("Visible")
    private Integer visible;

    @Field("PARAMS")
    private String params;

    @Field("VALUE_RECENTLY")
    private String valueRecently;

    @Field("EXTRA")
    private String extra;

    @Field("HTML")
    private String html;

    @Field("DeviceValueSettings")
    private String devicevaluesettings;
}
