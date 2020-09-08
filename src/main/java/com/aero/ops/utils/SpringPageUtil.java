package com.aero.ops.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class SpringPageUtil {

    public static Pageable buildSpringPageRequest(int pageIndex, int pageSize){
        return PageRequest.of(pageIndex-1,pageSize);
    }
}
