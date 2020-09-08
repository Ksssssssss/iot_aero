package com.aero.ops.entity.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "Cluster")
public class ClusterSaveDTO {
    private String pid;
    private String title;
    private String url;
    private Integer clusterType;
}