package com.zwy.xlog;

public enum CreateLogFileState {
    PermissionDenied,//权限不足
    PathError,//路径错误找不到
    Success_EXISTS,//已经存在
    Success_Create,//不存在已创建成功
}
