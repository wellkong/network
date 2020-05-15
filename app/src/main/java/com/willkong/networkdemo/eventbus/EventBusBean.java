package com.willkong.networkdemo.eventbus;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.networkdemo.eventbus
 * @Author: willkong
 * @CreateDate: 2019/5/10 15:23
 * @Description: 通知消息类
 */
public class EventBusBean extends BaseEventbusBean<Object>{

    public EventBusBean(int type, Object obj) {
        super(type, obj);
    }
}
