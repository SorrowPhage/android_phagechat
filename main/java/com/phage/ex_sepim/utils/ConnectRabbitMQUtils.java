package com.phage.ex_sepim.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectRabbitMQUtils {

    public static Connection getConnection() throws IOException, TimeoutException
    {
        ConnectionFactory conn = new ConnectionFactory();
        conn.setHost("192.168.102.100");  //RabbitMQ服务所在的ip地址
        conn.setPort(5673);   //RabbitMQ服务所在的端口号
        conn.setVirtualHost("/ems");
        conn.setUsername("ems");
        conn.setPassword("ems");
        return conn.newConnection();
    }


}
