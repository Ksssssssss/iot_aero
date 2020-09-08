package com.aero.ops.controller.funcs;

import com.aero.common.constants.FuncType;
import com.aero.common.model.PageModel;
import com.aero.common.model.ResponseModel;
import com.aero.ops.entity.vo.FuncVO;
import com.aero.ops.utils.LayTableUtil;
import com.aero.ops.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 罗涛
 * @title FuncController
 * @date 2020/7/31 11:44
 */

@RestController
@RequestMapping(value = "/funcs")
@Api(tags={"能力接口"}, value = "FuncController")
public class FuncController {

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ApiOperation(value = "所有能力值")
    public String all(){
        List<FuncVO> funcs = new ArrayList<>();
        for (FuncType funcType : FuncType.values()){
            FuncVO funcVO = new FuncVO();
            funcVO.setDesc(funcType.getDesc());
            funcVO.setCode(funcType.getCode());
            funcs.add(funcVO);
        }
        PageModel<List<FuncVO>> funcPage = new PageModel<>();
        funcPage.setData(funcs);
        funcPage.setCount(funcs.size());
        ResponseModel responseModel = ResultUtils.me.success(funcPage);
        return LayTableUtil.me.convertResult2LayTable(responseModel);
    }
}
