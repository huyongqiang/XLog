# XLog
优雅的日志，自动检测并格式化json数据，让你看日志不再眼花。哪里有bug点哪里，so easy.

#### 引入项目

```
aaaaa
```

#### 效果 

![log1](https://github.com/devzwy/XLog/blob/master/image/log1.png)  
![log2](https://github.com/devzwy/XLog/blob/master/image/log2.png)  
![log3](https://github.com/devzwy/XLog/blob/master/image/log3.png)   
![log4](https://github.com/devzwy/XLog/blob/master/image/log4.png)      


#### 使用

```kotlin
     val str =
            "  {\"key\":\"sjkhuwebewbklsiuqww\",\"userName\":\"张三\",\"list\":[{\"a\":123,\"b\":true},{\"a\":44444,\"b\":false}] }     "
        //开启日志写入文件功能 参数默认值：logFileDirPath_ : XUtils.getSDCardPathByEnvironment() + "/XLog/ fileMaxCapacity:5M
//        XLog.openLog2File()
        /**
         * 开启日志写入文件功能(每一行日志都会写入)
         *
         * @param logFileDirPath_ 路径，必须以斜杠结尾，文件名称会以当前时间命名，单个文件超过5M时会创建第二个文件,以此类推
         * @param fileMaxCapacity 单个日志文件超过该大小时重新创建文件，单个文件过大时打开会耗时 默认5M
         */
        XLog.openLog2File(XUtils.getSDCardPathByEnvironment() + "/logFileCache/", 1)
        //日志输出标签使用默认值：XLog
        XLog.d("你好，我是一条测试的日志")
        //单条日志可以使用自定义标签输出，
        XLog.d("小米推送相关日志", "推送注册成功......")
        //全局使用的日志输出标签
        XLog.setTag("App运行日志")
        //设置了全局日志后，采用如下方式未指定TAG输出的日志均会采用全局标签
        XLog.d("测试日志")
        XLog.e("测试日志")
        XLog.e(Throwable("异常"))
        XLog.e("自定义标签", "11111111", Throwable("异常"))
        XLog.d("ResponseStr", str)
        
        
        //清除所有日志文件
       // XLog.clearLogFiles()

```

