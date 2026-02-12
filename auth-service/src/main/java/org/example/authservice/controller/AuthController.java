package org.example.authservice.controller;

/**
 * @author ZSZ
 * @date 2026/2/11 6:42
 * @description
 */

import com.alibaba.cloud.commons.lang.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.authservice.model.dto.LoginRequestDTO;
import org.example.authservice.model.dto.LoginResponseDTO;
import org.example.authservice.model.enums.ResponseCode;
import org.example.authservice.model.response.Response;
import org.example.authservice.service.login.LoginStrategy;
import org.example.authservice.service.login.factory.LoginStrategyFactory;
import org.example.authservice.service.login.impl.GithubCallbackStrategy;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController   // 使用 @RestController 替代 @Controller，方便返回 JSON
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginStrategyFactory strategyFactory;
    private final GithubCallbackStrategy githubCallbackStrategy;


    /**
     * 统一登录入口
     */
    @PostMapping("/login")
    public Response<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginStrategy strategy = null;
        try {
            // 根据 type 选择策略并执行
            strategy = strategyFactory.getStrategy(request.getType());
            LoginResponseDTO login = strategy.login(request);
            return Response.<LoginResponseDTO>builder()
                    .code(ResponseCode.LOGIN_SUCCESS.getCode())
                    .info(ResponseCode.LOGIN_SUCCESS.getInfo())
                    .data(login)
                    .build();
        } catch (Exception e) {
            log.error("登录失败,username:{} password:{}",request.getUsername(),request.getUsername(),e);
            return Response.<LoginResponseDTO>builder()
                    .code(ResponseCode.LOGIN_FAILED.getCode())
                    .info(ResponseCode.LOGIN_FAILED.getInfo()+":"+e.getMessage())
                    .build();
        }

    }

//    /**
//     * GitHub 回调入口
//     */
//    @GetMapping("/github/callback")
//    public LoginResponseDTO githubCallback(@RequestParam("code") String code) {
//        return githubCallbackStrategy.handleCallback(code);
//    }
}

