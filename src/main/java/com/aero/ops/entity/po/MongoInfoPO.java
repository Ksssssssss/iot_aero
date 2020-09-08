package com.aero.ops.entity.po;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MongoInfoPO {
    private String database;
    private String collection;
    private String query;
    private int pageIndex;
    private int pageSize;
}
