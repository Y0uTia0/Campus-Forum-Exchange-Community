package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVo;
import com.example.service.AccountService;
import com.example.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;
import java.util.function.Supplier;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    @Resource
    AccountService service;


    @Resource
    ControllerUtils utils;
    @Autowired
    private AccountService accountService;

    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
                                        @RequestParam @Pattern(regexp = "(register|reset|modify)") String type,
                                        HttpServletRequest request){
        return utils.messageHandle(() ->
                accountService.registerEmailVerifyCode(type,email,request.getRemoteAddr()));
    }

    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo){
        return utils.messageHandle(() ->accountService.registerEmailAccount(vo));
    }

    @PostMapping("/reset-confirm")
    public RestBean<Void> resetConfirm(@RequestBody @Valid ConfirmResetVO vo){
        return utils.messageHandle(() ->accountService.resetConfirm(vo));
    }

    @PostMapping("/reset-password")
    public RestBean<Void> resetConfirm(@RequestBody @Valid EmailResetVo vo){
        return utils.messageHandle(() ->accountService.resetEmailAccountPassword(vo));
    }

    private <T> RestBean<Void> messageHandle(T vo, Function<T,String> function){
        return messageHandle(() -> function.apply(vo));
    }

    private RestBean<Void> messageHandle(Supplier<String> action){
        String message = action.get();
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }

}
