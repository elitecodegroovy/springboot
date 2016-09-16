package com.biostime.swisse.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author elite_jigang@163.com
 */
@RestController
public class SpringBootController {

    @ResponseBody
    @RequestMapping(value="/", method = {RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> getIndex(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("welcome", "JohnLau");
        map.put("giveyou", 10000);
        return map;
    }
}
