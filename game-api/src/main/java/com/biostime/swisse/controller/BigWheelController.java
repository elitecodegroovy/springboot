package com.biostime.swisse.controller;

import com.biostime.swisse.model.SwisseNamespace;
import com.biostime.swisse.model.bean.ActivityDetail;
import com.biostime.swisse.model.bean.GridItem;
import com.biostime.swisse.model.bean.User;
import com.biostime.swisse.web.service.WeiXinService;
import com.biostime.swisse.web.util.ResponseUtil;
import com.biostime.swisse.web.util.StringUtil;
import com.biostime.swisse.web.util.bean.BigWheelReq;
import com.biostime.swisse.web.util.bean.ReceiverInfoReq;
import com.biostime.swisse.web.util.bean.UserResponse;
import com.biostime.swisse.web.util.validator.ActionValidator;
import com.mama100.cache.redis.util.RedisUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 *     大转盘前段接口。
 * @author liujigang@biostime.com
 */

@Controller
@RequestMapping(value = "swisse-api")
public class BigWheelController {

    private static final Logger LOGGER = LogManager.getLogger(BigWheelController.class);

    @Autowired
    private WeiXinService weiXinService;


    @ResponseBody
    @RequestMapping(value = "/api/getUser", method = {RequestMethod.GET} )
    public Map<String, Object> getUser(@RequestParam(value = "openId", required = false) String openId,
                                       @RequestParam(value = "phone", required = false) String phone,
                                       @RequestParam(value = "system") String system){
        LOGGER.info("req parameter {}, {}, {} ", openId, phone, system);
        Map<String, Object> map = ResponseUtil.getResponseMap();
        openId = ActionValidator.getRealValue(openId, phone, map);
        if(openId == null){
            return map;
        }
        ActivityDetail act = weiXinService.getCurrenctActivity(system);
        if(act != null){
            try{
                if(openId.trim().length() != 0){
                    User user  = weiXinService.findUser(openId, system);
                    LOGGER.info("user {}", RedisUtil.GSON.toJson(user));
                    if(user != null){
                        UserResponse userResponse = new UserResponse();
                        userResponse.setCustomerId(user.getCustomerId());
                        userResponse.setMobile(user.getMobile());
                        userResponse.setFreeTimes(weiXinService.getFreeTimes(user.getCustomerId(),
                                act.getActivityId(), system));
                        long cId = Long.parseLong(user.getCustomerId());
                        Long totalPoints = weiXinService.getSwisseTotalPoints(cId, map, system);
                        if(totalPoints == null){
                            return map;
                        }
                        userResponse.setTotalPoints(totalPoints.intValue());
                        map.put("user", userResponse);
                    }else {
                        ResponseUtil.setResult(map, ResponseUtil.ResultCode.USER_NOT_FOUND);
                    }
                }else {
                    ResponseUtil.setResult(map, ResponseUtil.ResultCode.INVALID_OPEN_ID);
                }
            }catch (Exception e){
                ResponseUtil.setResult(map, ResponseUtil.ResultCode.ERROR);
                return map;
            }
        }else{
            ResponseUtil.setResult(map, ResponseUtil.ResultCode.ACT_NOT_FOUND);
        }

        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/api/updateBonusInfo", method = {RequestMethod.POST} )
    public Map<String, Object> updateBonusInfo(@RequestBody ReceiverInfoReq req){
        Map<String, Object> map = ResponseUtil.getResponseMap();

        try{
            //validate the req bean.
            if(ActionValidator.validateReceiverInfoReq(req, map)){
                if(!weiXinService.updateReceiverInfo(req)){
                    ResponseUtil.setResult(map, ResponseUtil.ResultCode.ERROR, ", updateReceiverInfo failed");
                }
            }
        }catch (Exception e){
            ResponseUtil.setResult(map, ResponseUtil.ResultCode.ERROR, e.getMessage());
            return map;
        }
        return map;
    }


    @ResponseBody
    @RequestMapping(value = "/api/playGame", method = {RequestMethod.POST} )
    public Map<String, Object> playGame(@RequestBody BigWheelReq req){
        LOGGER.info("req parameter {} ", RedisUtil.GSON.toJson(req));
        Map<String, Object> map = ResponseUtil.getResponseMap();
        User user = null;
        String openId = ActionValidator.getRealValue(req.getOpendId(), req.getPhone(), map);
        if(openId == null){
            return map;
        }
        req.setOpendId(openId);
        if(req.getOpendId() != null && req.getOpendId().trim().length() != 0){
            user =  weiXinService.findUser(req.getOpendId().trim(), req.getSystem());
        }else {
            ResponseUtil.setResult(map, ResponseUtil.ResultCode.INVALID_OPEN_ID);
        }

        if(user != null){
            if(ActionValidator.validateGameReq(req, map) &&
                    weiXinService.checkUserRight(user, req, map, req.getSystem())){
                GridItem item = weiXinService.playGame(req, map, user);
                if(item != null && !weiXinService.traceUserAction(item, user, map, req)){
                    ResponseUtil.setResult(map, ResponseUtil.ResultCode.INVALID_TRACING_ACTION, ",用户行为记录失败！");
                    LOGGER.warn("can't trace the user's action [openId:{}]", req.getOpendId());
                }
                map.put(SwisseNamespace.GRID_DATA, item);
            }
            //ACT_NOT_RIGHT
        }else {
            ResponseUtil.setResult(map, ResponseUtil.ResultCode.INVAID_USER);
        }
        return map;
    }


    @ResponseBody
    @RequestMapping(value = "/api/playGame4Internal", method = {RequestMethod.POST} )
    public Map<String, Object> playGame4Internal(@RequestBody BigWheelReq req){
        LOGGER.info("req parameter {} ", RedisUtil.GSON.toJson(req));
        Map<String, Object> map = ResponseUtil.getResponseMap();
        if(StringUtil.SECURE_KEY.equals(req.getOpendId())){
            if(req.getActivityId() > 1 && req.getSystem() != null){
                GridItem item = weiXinService.playGame4Internal(req, map);
                if(item != null && !weiXinService.traceUserAction(item, null, map, req)){
                    ResponseUtil.setResult(map, ResponseUtil.ResultCode.INVALID_TRACING_ACTION, ",用户行为记录失败！");
                    LOGGER.warn("can't trace the user's action [openId:{}]", req.getOpendId());
                }
                map.put(SwisseNamespace.GRID_DATA, item);
            }
        }else {
            ResponseUtil.setResult(map, ResponseUtil.ResultCode.INVAID_USER);
        }
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/api/getOnlineGame", method = {RequestMethod.GET})
    public Map<String, Object> getOnlineGame(@RequestParam(value = "system") String system){
        Map<String, Object> map = ResponseUtil.getResponseMap();
        try{
            ActivityDetail act = weiXinService.getCurrenctActivity(system);
            map.put("data", act);
        }catch (Exception e){
            LOGGER.error("getOnlineGame interface with an error:" + e.getMessage(), e);
            map.put(ResponseUtil.ResultCode.ALERT.getCode(),
                    "error:" +e.getMessage()+  ResponseUtil.ResultCode.ALERT.getMsg());
            return map;
        }
        return map;
    }



    @ResponseBody
    @RequestMapping(value = "/api/getWinningUser", method = {RequestMethod.GET})
    public Map<String, Object> getWinningUser(@RequestParam(value = "activityId") String activityId,
                                              @RequestParam(value = "system") String system,
                                              @RequestParam(value = "phone" ,required = false) String phone){
        Map<String, Object> map = ResponseUtil.getResponseMap();
        long actId = StringUtil.parseLong(activityId);
        if(actId == 0){
            ResponseUtil.setResult(map, ResponseUtil.ResultCode.INVALID_REQUEST, "activityId必须为数字，并且大于0");
        } else{
            map.put("data", weiXinService.getWinningUser(actId, system, phone==null? "":phone));
        }
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/api/getActionId", method = {RequestMethod.GET})
    public Map<String, Object> getActionId(@RequestParam(value = "activityId") String activityId,
                                             @RequestParam(value = "phone") String phone){
        Map<String, Object> map = ResponseUtil.getResponseMap();
        if(ActionValidator.validateApiReq(phone, activityId, map)){
            Long actionId = null;
            try{
                long reqId = Long.parseLong(activityId);
                Map<String, Object> dataMap = new HashMap<String, Object>();
                dataMap.put("phone", phone);
                dataMap.put("activityId", reqId);
                actionId = weiXinService.getActionId(dataMap);
            }catch (Exception e){
                ResponseUtil.setResult(map, ResponseUtil.ResultCode.ERROR, e.getMessage());
                return map;
            }
            map.put("actionId", actionId);
        }
        return map;
    }
}
