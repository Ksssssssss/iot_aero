package com.aero.ops.service;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.ops.entity.dto.CodeLibEditDTO;
import com.aero.ops.entity.dto.CodeLibSaveDTO;
import com.aero.ops.entity.po.CodeLibPO;

import java.util.List;

public interface ICodeLibService {
    /**
     * 获取服务器分页数据
     * @param pageDTO
     * @return
     */
    PageModel<List<CodeLibPO>> getCodeByPage(PageDTO pageDTO);


    Boolean save(CodeLibSaveDTO saveDTO);

    boolean update(CodeLibEditDTO editDTO);

    boolean delete(List<String> codeIds);

    CodeLibPO getCodeLib(String id);

    CodeLibPO getCodeLibByName(String projectName);
}
