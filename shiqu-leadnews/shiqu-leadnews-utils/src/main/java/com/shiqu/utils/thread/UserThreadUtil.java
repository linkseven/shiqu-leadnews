package com.shiqu.utils.thread;

public class UserThreadUtil {
    private static ThreadLocal<Integer> WM_ID_THREAD_LOCAL = new ThreadLocal<>();

    //存入线程中
    public static void setId(Integer id){
        WM_ID_THREAD_LOCAL.set(id);
    }

    //从线程中获取
    public static Integer getId(){
        return WM_ID_THREAD_LOCAL.get();
    }

    //清理
    public static void clear(){
        WM_ID_THREAD_LOCAL.remove();
    }
}
