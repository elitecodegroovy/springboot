package com.biostime.swisse.controller;

import com.biostime.swisse.model.bean.HealthMonitoring;
import com.biostime.swisse.web.service.MonitoringService;
import com.biostime.swisse.web.util.ResponseUtil;
import com.biostime.swisse.web.util.validator.ActionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author elite_jigang@163.com
 */
@Controller
@RequestMapping(value = "swisse-api")
public class HealthMonitoringController {


    @Autowired
    private MonitoringService monitoringService;


    @ResponseBody
    @RequestMapping(value = "/system/recordHealthInfo", method = {RequestMethod.POST})
    public Map<String, Object> recordHealthInfo(@RequestBody HealthMonitoring req){
        Map<String, Object> map = ResponseUtil.getResponseMap();
        if(ActionValidator.validateHealthMonitoring(req, map)){
            monitoringService.saveReqHealthInfo(req);
        }
        return map;
    }
}
