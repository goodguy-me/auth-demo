package org.example.authservice.service.login.factory;

import org.example.authservice.model.enums.LoginType;
import org.example.authservice.model.enums.ResponseCode;
import org.example.authservice.service.login.LoginStrategy;
import org.example.authservice.service.login.impl.FormLoginStrategy;
import org.example.authservice.service.login.impl.GithubAuthStrategy;
import org.example.authservice.service.login.impl.LdapLoginStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZSZ
 * @date 2026/2/12 8:25
 * @description
 */
@Service
public class LoginStrategyFactory {
    private final Map<String, LoginStrategy> strategyMap;


    public LoginStrategyFactory(Map<String, LoginStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    public LoginStrategy getStrategy(String type) {
        LoginStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException(ResponseCode.LGIN_ERROR.getInfo()+":" + type);
        }
        return strategy;
    }
}
