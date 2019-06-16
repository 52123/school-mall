package com.hugh.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 52123
 * @since 2019/4/8 0:58
 */
@Data
public class WebResp<T> implements Serializable {

    private static final long serialVersionUID = -2156191491231337L;

    private String resultCode = "0000";
    
    private String message;

    private Integer totalPage = null;

    private T data;

    public WebResp success(T data, Integer totalPage){
        this.data = data;
        this.totalPage = totalPage;
        return this;
    }

    public WebResp fail(String resultCode , String message){
        setMessage(message);
        setResultCode(resultCode);
        return this;
    }

    public WebResp unlogin(){
        setMessage("请先登录");
        setResultCode("501");
        return this;
    }

    public WebResp badArgument(){
        setMessage("参数值有误");
        setResultCode("502");
        return this;
    }

    public WebResp fail(){
        setMessage("服务器内部有误");
        setResultCode("500");
        return this;
    }
}
