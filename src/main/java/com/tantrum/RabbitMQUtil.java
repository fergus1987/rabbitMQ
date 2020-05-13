package com.tantrum;

import cn.hutool.core.net.NetUtil;

import javax.swing.JOptionPane;

// 判断服务器是否启动
public class RabbitMQUtil {

    public static void checkServer() {

        if (NetUtil.isUsableLocalPort(15672)) {
            JOptionPane.showMessageDialog(null, "RabbitMQ 服务器未启动");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        checkServer();
    }
}