package com.nx.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nx.common.exception.BaseException;
import com.nx.common.exception.ServerException;
import com.nx.common.exception.ServiceException;
import com.nx.common.model.constant.HttpStatus;
import com.nx.common.model.constant.ResultCode;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;
import java.util.Objects;

@Data
public class Result<T> implements java.io.Serializable{
    public final static String PARAM_MSG = "msg";
    public final static String PARAM_CODE = "code";
    public final static String PARAM_DATA = "dat";
    public final static String PARAM_TRACE_ID = "traceId";

    // 自定义返回码起始码
    private static final int CUSTOM_ERROR_CODE_BEGIN = 10000;


    private int code;

    /**
     * in18:开头的为
     */
    private String msg;


    /**
     * traceId
     */
    private String traceId;

    public Result<T> traceId(String traceId){
        this.traceId = traceId;
        return this;
    }

    /**
     * 数据
     */
    private T data;

    public Result<T> data(T data){
        this.data = data;
        return this;
    }

    public Result() {

    }

    public Result code(int code) {
        this.code = code;
        return this;
    }


    @JsonIgnore
    public boolean isOK(){
        return (code==0 || code== HttpStatus.OK);
    }


    public boolean isSuccess(){
        return (code==0 || code== HttpStatus.OK);
        //return Objects.equals(code, ResultCode.SUCCESS.getCode());
    }

    public static boolean isSuccess(Integer code){
        return (code==0 || code== HttpStatus.OK);
        //return Objects.equals(code, ResultCode.SUCCESS.getCode());
    }

    @JsonIgnore
    public boolean state(){
        return (code==0 || code== HttpStatus.OK);
    }

    public String message(){
        return msg;
    }

    public Result msg(String msg) {
        this.msg = msg;
        return this;
    }


    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Result(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result(ResultCode.SUCCESS, data);
    }

    public static <T> Result<T> OK(T data) {
        return new Result(ResultCode.SUCCESS, data);
    }

    public static <T> Result<T> OK() {
        return new Result<T>(ResultCode.SUCCESS, null);
    }

    public static <T> Result<T> FAIL() {
        return new Result<T>(ResultCode.INTERNAL_SERVER_ERROR, null);
    }

    public static <T> Result<T> FAIL(String msg) {
        return new Result<T>(ResultCode.INTERNAL_SERVER_ERROR.getCode(), msg, null);
    }

    public static <T> Result<T> ERROR(String msg) {
        return new Result<T>(ResultCode.INTERNAL_SERVER_ERROR.getCode(), msg, null);
    }

    public static <T> Result<T> error(BaseException baseException) {
        return error(baseException.getCode(), baseException.getMessage());
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.msg = message;
        return result;
    }

    public static <T> Result<T> FAIL(int code, String msg) {
        if (code == ResultCode.SUCCESS.getCode() || code == ResultCode.SUCCESS_0.getCode()) {
            throw new RuntimeException("200与0是成功编码，不可用于异常编码");
        }
        return new Result<T>(code, msg, null);
    }

    public static <T> Result<T> FAIL(ResultCode resultCode) {
        if (resultCode == ResultCode.SUCCESS_0 || resultCode == ResultCode.SUCCESS) {
            throw new RuntimeException("200与0是成功编码，不可用于异常编码");
        }
        return new Result<T>(resultCode.getCode(), resultCode.getMsg(), null);
    }

    public static <T> Result<T> FAIL(ResultCode resultCode, String msg) {
        if (resultCode == ResultCode.SUCCESS_0 || resultCode == ResultCode.SUCCESS) {
            throw new RuntimeException("200与0是成功编码，不可用于异常编码");
        }

        return new Result<T>(resultCode.getCode(), msg != null && msg.length() > 0 ? msg : resultCode.getMsg(), null);
    }


    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getMsg() {
        return msg;
    }

    public Result setCode(int code) {
        this.code = code;
        return this;
    }

    public Result setMsg(String msg) {
        this.msg = msg;
        return this;
    }


    public Result setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }


    public Result setData(T data) {
        this.data = data;
        return this;
    }

    /**
     * @JsonIgnoreProperties
     * 判断是否有异常。如果有，则抛出 {@link ServiceException} 异常
     */
    @JsonIgnore
    public void checkError() throws ServiceException {
        if (isSuccess()) {
            return;
        }
        // 服务端异常
        if (ResultCode.isServerErrorCode(code)) {
            throw new ServerException(code, msg);
        }
        // 业务异常
        throw new ServiceException(code, msg);
    }
}
