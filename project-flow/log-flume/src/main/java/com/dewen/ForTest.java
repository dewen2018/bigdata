package com.dewen;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

import java.nio.charset.Charset;

public class ForTest {
    public static void main(String[] args) throws EventDeliveryException {
        String ip = "master";
        int port = 44444;

        RpcClient client = RpcClientFactory.getDefaultInstance(ip, port);
        Event event = EventBuilder.withBody("hello word", Charset.forName("UTF-8"));
        client.append(event);
        client.close();
    }
}
