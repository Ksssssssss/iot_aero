package com.aero.ops.entity.dto;

import lombok.Data;

@Data
public class DeviceEditDTO {
    private Integer id;

//    private String uuid;

    private String sensorName;

    private Integer secondchecktime;

    private String startvalue;

    private String serialnum;

    private String funcs;
}