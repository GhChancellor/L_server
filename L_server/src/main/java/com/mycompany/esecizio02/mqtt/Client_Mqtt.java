/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.esecizio02.mqtt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 *
 * @author lele
 */
public class Client_Mqtt implements MqttCallback {

    private static Client_Mqtt instance = null;
    private final int qos = 2;
    private MqttClient client = null;

    private String broker = "tcp://0.0.0.0:1883";
    private String clientId = "Client del server : " + new Date().getTime();
    private final String userConnected = "UserConnected";
    private List< String> allUsers = new ArrayList<>();

    public static Client_Mqtt getInstance() {
        if (instance == null) {
            instance = new Client_Mqtt();
        }
        return instance;
    }

    /* inizializza il client */
    private Client_Mqtt() {
        super();

        try {
            client = new MqttClient(broker, clientId, new MemoryPersistence());
        } catch (MqttException ex) {
            Logger.getLogger(Client_Mqtt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * inizializza il client e accede a un canale
     */
    private void initializeConnection() {
        try {
            /* inizializza il client */
            if (client == null) {
                client = new MqttClient(broker, clientId, new MemoryPersistence());
            }

            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            /* Conneting to Broker */
            client.connect(connectOptions);
            System.out.println("Connected to broker" + broker);

            /* subscribe section */
            client.subscribe("UserConnected");
            client.subscribe("allusers");
            client.subscribe("Talk");
            client.setCallback(this);

            /* Public message on channel UserConnected*/
            publish("UserConnected", "Client " + clientId + " is connected. \n");

        } catch (Exception e) {
            Logger.getLogger(Client_Mqtt.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void connect() {
        initializeConnection();
    }

    /**
     * Public message
     *
     * @param topic
     * @param message
     */
    public void publish(String topic, String message) {

        try {
            MqttMessage messageMM = new MqttMessage(message.getBytes());
            messageMM.setQos(qos);

            if (client == null || !client.isConnected()) {
                initializeConnection();
            }

            client.publish(topic, messageMM);
        } catch (Exception ex) {
            Logger.getLogger(Client_Mqtt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void connectionLost(Throwable thrwbl) {

    }

    /**
     * Riceve un messaggio dal server e lo mette a video Dal server metodo
     * public
     *
     * @param topic
     * @param mm
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {        

        if (topic.equals(userConnected)) {
            allUsers.add(new String(mm.getPayload()));

            for (String allUser : allUsers) {
                publish("allusers", allUser);
            }
        }

        System.out.println("TOPIC: " + topic);
        System.out.println("MESSAGE: " + new String(mm.getPayload()) + "\n");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {

    }

}
