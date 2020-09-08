package com.aero.ops.service.impl;

import com.aero.common.model.PageDTO;
import com.aero.common.model.PageModel;
import com.aero.ops.entity.dto.ServerSaveDTO;
import com.aero.ops.entity.po.ServerPO;
import com.aero.ops.service.IServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ServerServiceImpl implements IServerService {

    @Autowired
    @Qualifier("devOpsMongoTemplate")
    MongoTemplate devOpsMongoTemplate;

    @Override
    public PageModel<List<ServerPO>> getServerByPage(PageDTO pageDTO) {
        Query query = new Query();
        Long count = devOpsMongoTemplate.count(query, ServerPO.class);
        PageModel page = new PageModel();
        page.setCount(count.intValue());
        if (count>0) {
            int pageSize = 0;
            int skip = 0;
            if (Objects.nonNull(pageDTO)) {
                pageSize = pageDTO.getPageSize();
                skip = (pageDTO.getPageIndex()-1)*pageSize;
            }else {
                pageSize = Integer.MAX_VALUE;
            }
            query.skip(skip).limit(pageSize);
            List<ServerPO> serverPOS = devOpsMongoTemplate.find(query, ServerPO.class);
            page.setData(serverPOS);
        }
        return page;
    }

    @Override
    public boolean save(ServerSaveDTO saveDTO) throws Exception{
        try {
            devOpsMongoTemplate.save(saveDTO);
            return true;
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public ServerPO getServerByLanIp(String lanIp) {
        Query query = new Query();
        query.addCriteria(Criteria.where("lanIp").is(lanIp));
        List<ServerPO> servers = devOpsMongoTemplate.find(query, ServerPO.class);
        if(CollectionUtils.isEmpty(servers)){
            return null;
        }
        if(servers.size()>1){
            log.warn("有多台ip相同的服务器，请确认唯一索引！！ ip = {}", lanIp);
        }
        return servers.get(0);
    }
}
