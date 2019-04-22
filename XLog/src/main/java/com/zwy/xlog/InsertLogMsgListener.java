package com.zwy.xlog;

public interface InsertLogMsgListener {
    void onSucc();

    void onError(String msg);
}
