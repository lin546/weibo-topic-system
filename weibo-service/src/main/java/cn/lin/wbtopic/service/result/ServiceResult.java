package cn.lin.wbtopic.service.result;

import java.io.Serializable;

public class ServiceResult<T> implements Serializable {
    private boolean success = true;
    private T data;
    private String errMsg;

    public String getErrMsg() {
        return errMsg;
    }
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
