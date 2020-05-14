package com.willkong.networkdemo.api;

import java.util.List;

/**
 * @ProjectName: NetWorkDemo
 * @Package: com.willkong.network
 * @Author: willkong
 * @CreateDate: 2020/5/13 11:11
 * @Description: java类作用描述
 */
public class DataBean {

    private List<MessageTypeBean> messageType;

    public List<MessageTypeBean> getMessageType() {
        return messageType;
    }

    public void setMessageType(List<MessageTypeBean> messageType) {
        this.messageType = messageType;
    }

    public static class MessageTypeBean {
        /**
         * name : 推荐理由
         * value : 1
         * orderline : 1
         */

        private String name;
        private String value;
        private int orderline;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getOrderline() {
            return orderline;
        }

        public void setOrderline(int orderline) {
            this.orderline = orderline;
        }
    }
}
