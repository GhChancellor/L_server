/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.esecizio02.mqtt;

import static com.hazelcast.client.impl.protocol.util.UnsafeBuffer.UTF_8;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptAcknowledgedMessage;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptConnectionLostMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.server.Server;
import io.moquette.server.config.ClasspathResourceLoader;
import io.moquette.server.config.ResourceLoaderConfig;
import io.netty.buffer.ByteBufUtil;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author lele
 */
public class MQTTServer {

    private List<String> ON_LINE = new LinkedList<>();

    public MQTTServer() {

    }

    public void start() throws IOException {
        final Server mqtt_broker = new Server();

        mqtt_broker.startServer(new ResourceLoaderConfig(new ClasspathResourceLoader()),
                Collections.singletonList(new AbstractInterceptHandler() {
                    @Override
                    public String getID() {
                        return "EmbeddedLauncherPublishListener";
                    }

                    @Override
                    public void onDisconnect(InterceptDisconnectMessage msg) {
                        ON_LINE.remove(msg.getClientID());
                    }

                    @Override
                    public void onConnectionLost(InterceptConnectionLostMessage msg) {
                        ON_LINE.remove(msg.getClientID());
                    }

                    @Override
                    public void onConnect(InterceptConnectMessage msg) {
                        System.out.println("si p connesso " + msg.getClientID());
                        Client_Mqtt.getInstance().publish("prova01", "benvenuto");
                        ON_LINE.add(msg.getClientID());
                    }

                    @Override
                    public void onPublish(InterceptPublishMessage msg) {
                        final String decodedPayload
                                = new String(ByteBufUtil.getBytes(msg.getPayload()), UTF_8);

                        System.out.println("Received on topic: " + msg.getTopicName()
                                + " content: " + decodedPayload);
                    }
                })
        );

        Client_Mqtt.getInstance().connect();

        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> {
                    mqtt_broker.stopServer();
                }
                ));

    }

    public void prova() {
        // news UserConnected
        Client_Mqtt.getInstance().publish("UserConnected", "asdasdsdasdas");

    }

}
