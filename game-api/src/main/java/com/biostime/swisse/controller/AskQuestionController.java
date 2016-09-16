package com.biostime.swisse.controller;


import com.biostime.swisse.model.bean.AskQuestion;
import com.biostime.swisse.model.bean.User;
import com.biostime.swisse.web.service.AskQuestionService;
import com.biostime.swisse.web.service.WeiXinService;
import com.biostime.swisse.web.util.ResponseUtil;
import com.biostime.swisse.web.util.bean.Question;
import com.biostime.swisse.web.util.bean.RightAskReq;
import com.biostime.swisse.web.util.validator.ActionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 描述：
 *     大转盘前段问答接口定义。
 * @author liujigang@biostime.com
 */

@Controller
@RequestMapping(value = "swisse-api")
public class AskQuestionController {

    @Autowired
    private AskQuestionService askService;

    @Autowired
    private WeiXinService weiXinService;

    @ResponseBody
    @RequestMapping(value = "/api/getQuestion", method = {RequestMethod.GET})
    public Map<String, Object> getQuestion(@RequestParam(value = "system") String system,
                                           @RequestParam(value = "openId", required = false) String openId,
                                           @RequestParam(value = "phone", required = false) String phone){
        Map<String, Object> map = ResponseUtil.getResponseMap();
        openId = ActionValidator.getRealValue(openId, phone, map);
        if(openId == null){
            return map;
        }
        User user  = weiXinService.findUser(openId, system);
        if(user != null){
            Question question = askService.getQuestion(user.getCustomerId(), system);
            map.put("data", question);
        }else {
            ResponseUtil.setResult(map, ResponseUtil.ResultCode.INVAID_USER);
        }
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/api/getRightAsk", method = {RequestMethod.POST})
    public Map<String, Object> getRightAsk(@RequestBody RightAskReq req){
        Map<String, Object> map = ResponseUtil.getResponseMap();
        if(!ActionValidator.validateRightAskReq(req, map)){
            return map;
        }
        String openId = ActionValidator.getRealValue(req.getOpenId(), req.getPhone(), map);
        if(openId == null){
            return map;
        }
        User user  = weiXinService.findUser(openId, req.getSystem());
        if(user != null){
            AskQuestion ask = askService.getQuestionById(req.getAskId());
            if(ask != null && ask.getRightAsk().equals(req.getAsk())
                    && askService.addFreePlayGame(req, user)){
                //get the right for playing game
                map.put("isRight", true);
            }else {
                map.put("isRight", false);
                map.put("rightAsk", ask != null ?ask.getRightAsk():"百度合生元即可知晓！");
            }
        }else {
            ResponseUtil.setResult(map, ResponseUtil.ResultCode.INVAID_USER);
        }
        return map;
    }
}
