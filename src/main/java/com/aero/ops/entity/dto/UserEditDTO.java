package com.aero.ops.entity.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "User")
public class UserEditDTO {

    @Id
    private String id;
    private String realName;
    private String phoneNumber;
    private String dingTalkUid;
    private String dingNickName;
    private Integer roleId;
    private Boolean enable;
}
