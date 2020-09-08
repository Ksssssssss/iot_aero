package com.aero.ops.config;

import com.aero.ops.constants.DefaultRoles;
import com.aero.ops.entity.dto.RoleSaveDTO;
import com.aero.ops.entity.po.MenuPO;
import com.aero.ops.entity.po.RolePO;
import com.aero.ops.entity.po.UserPO;
import com.aero.ops.service.IMenuService;
import com.aero.ops.service.IRoleService;
import com.aero.ops.service.IUserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@Configuration
public class UcenterInitConfig{

    @Autowired
    @Qualifier("devOpsMongoTemplate")
    MongoTemplate mongoTemplate;

    @Autowired
    IUserService userService;

    @Autowired
    IMenuService menuService;

    @Autowired
    IRoleService roleService;

    @PostConstruct
    public void initUcenter(){
        try {
            menuAdjust();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            initRole();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            roleService.refreshAdminPrivilege();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            initUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initRole(){
        boolean flag = mongoTemplate.collectionExists(RolePO.class);
        if(!flag){
            insertDefaultRoles();
        }else{
            Query query = new Query();
            long count = mongoTemplate.count(query, RolePO.class);
            if(count==0){
                insertDefaultRoles();
            }
        }
    }

    public void initUser(){
        boolean flag = mongoTemplate.collectionExists(UserPO.class);
        if(!flag){
            insertAdmin();
        }else{
            Query query = new Query();
            Criteria where = Criteria.where("roleId").is(DefaultRoles.ADMIN.getRoleId());
            query.addCriteria(where);
            long count = mongoTemplate.count(query, UserPO.class);
            if(count==0){
                insertAdmin();
            }
        }
    }


//    public void initMenus(){
//        boolean flag = mongoTemplate.collectionExists(MenuPO.class);
//        if(!flag){
//            insertDefaultMenus();
//        }
//    }

    public void insertAdmin(){
        UserPO user = new UserPO();
        user.setRealName("admin");
        user.setRoleId(DefaultRoles.ADMIN.getRoleId());
        user.setEnable(Boolean.TRUE);
        user.setCrtTime(new Date());
        userService.save(user);
    }

    public void insertDefaultRoles(){
        for (DefaultRoles dr:DefaultRoles.values()) {
            RoleSaveDTO role = new RoleSaveDTO();
            role.setRoleName(dr.getRoleName());
            role.setRemark(dr.getRemark());
            role.setEnable(DefaultRoles.ADMIN.equals(dr));
            roleService.save(role);
        }
    }

    public void menuAdjust(){
        try {
            String adjust = getMenuAdjustJson();
            JSONObject adjContent = (JSONObject) JSON.parse(adjust);
            boolean flag = mongoTemplate.collectionExists(MenuPO.class);

            if (!flag && adjContent.containsKey("init")) {
                JSONArray initNavs = (JSONArray)adjContent.get("init");
                if (initNavs.size()>0){
                    execMenuInit(initNavs);
                }
            }

            if (adjContent.containsKey("delete")) {
                JSONArray deleteNavs = (JSONArray)adjContent.get("delete");
                if (deleteNavs.size()>0){
                    List<String> deleteIds = new ArrayList<>();
                    deleteNavs.forEach(new Consumer<Object>() {
                        @Override
                        public void accept(Object obj) {
                            String id = ((String) obj);
                            deleteIds.add(id);
                        }
                    });
                    menuService.delete(deleteIds);
                }
            }

            if (adjContent.containsKey("insert")) {
                JSONArray insertNavs = (JSONArray)adjContent.get("insert");
                if(insertNavs.size()>0){
                    execMenuInit(insertNavs);
                }
            }
        } catch (Exception e) {
            log.error("执行菜单调整脚本时发生异常：{}", e.getMessage());
        }
    }

    public String getMenuAdjustJson() throws Exception{
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("menuAdjust.json");
        ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
        try {
            while (is.available() > 0) {
                os.write(is.read());
            }
            byte[] bytes = os.toByteArray();
            return new String(bytes, "UTF-8");
        }finally {
            is.close();
            os.close();
        }
    }


    public void execMenuInit(JSONArray navs) throws Exception{
        List<MenuPO> directInsertMenus = new ArrayList<>();
        navs.forEach(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                JSONObject firstNav = (JSONObject) o;
                boolean firstHasChild = firstNav.containsKey("children");
                MenuPO firstMenu = convertJson2Menu(firstNav, null);
                if(firstHasChild){
                    MenuPO firstSave = mongoTemplate.save(firstMenu);
                    List<MenuPO> firstChildren = new ArrayList<>();
                    JSONArray secondNavs = (JSONArray) firstNav.get("children");
                    secondNavs.forEach(new Consumer<Object>() {
                        @Override
                        public void accept(Object obj) {
                            JSONObject secondNav = (JSONObject) obj;
                            boolean secondHasChild = secondNav.containsKey("children");
                            if(secondHasChild){
                                MenuPO secondMenu = convertJson2Menu(secondNav, firstSave.getId());
                                MenuPO secondSave = mongoTemplate.save(secondMenu);
                                List<MenuPO> secondChildren = new ArrayList<>();
                                JSONArray thirdNavs = (JSONArray) secondNav.get("children");
                                thirdNavs.forEach(new Consumer<Object>() {
                                    @Override
                                    public void accept(Object object) {
                                        JSONObject thirdNav = (JSONObject) object;
                                        MenuPO thirdMenu = convertJson2Menu(thirdNav, secondSave.getId());
                                        secondChildren.add(thirdMenu);
                                    }
                                });
                                mongoTemplate.insertAll(secondChildren);
                            }else {
                                MenuPO secondMenuWithoutChild = convertJson2Menu(secondNav, firstSave.getId());
                                firstChildren.add(secondMenuWithoutChild);
                            }
                        }
                    });
                    mongoTemplate.insertAll(firstChildren);
                }else {
                    directInsertMenus.add(firstMenu);
                }
            }
        });
        mongoTemplate.insertAll(directInsertMenus);
    }


    public void insertDefaultMenus(){
        try {
//            Resource resource = new ClassPathResource("navs.json");
//            File file = resource.getFile();
//            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//            StringBuilder sb = new StringBuilder();
//            br.lines().forEach(f->sb.append(f));
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/common/json/menu-tree.json");
            ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
            try {
                while (is.available()>0) {
                    os.write(is.read());
                }
                byte[] bytes = os.toByteArray();
                JSONArray navs = JSON.parseArray(new String(bytes));
                execMenuInit(navs);
            } finally {
                is.close();
                os.close();
            }

        } catch (Exception e) {
            log.error("初始化菜单时发生异常：{}", e.getMessage());
        }
    }

    public MenuPO convertJson2Menu(JSONObject jsonObject, String parentId){
        MenuPO menu = new MenuPO();
        menu.setTitle(jsonObject.getString("title"));
        menu.setIcon(jsonObject.getString("icon"));
        menu.setHref(jsonObject.getString("href"));
        menu.setSpread(jsonObject.getBoolean("spread"));
        menu.setMenuType(jsonObject.getInteger("menuType"));
        if(StringUtils.isNotEmpty(parentId)){
            menu.setParentId(parentId);
        }else {
            if(Objects.nonNull(jsonObject.get("parentId"))){
                menu.setParentId((String)jsonObject.get("parentId"));
            }
        }
        menu.setCrtTime(new Date());
        menu.setUpdTime(new Date());
        return menu;
    }
}
