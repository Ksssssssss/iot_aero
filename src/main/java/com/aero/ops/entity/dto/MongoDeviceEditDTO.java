package com.aero.ops.entity.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "Device")
public class MongoDeviceEditDTO {
    @Id
    private String _id;

    private String sensorName;

    private Integer secondchecktime;

    private String startvalue;

    private String serialnum;

    private String funcs;
}