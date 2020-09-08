package com.aero.ops.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Accessors(chain = true)
@Document(indexName = "elk-bchd-acceptor", type = "_doc")
public class AppLogVO {
    String msg;

    public AppLogVO(String msg) {
        this.msg = msg;
    }
}
