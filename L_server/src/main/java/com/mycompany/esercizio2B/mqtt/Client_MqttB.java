/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.esercizio2B.mqtt;

import java.util.Date;
import java.util.LinkedList;
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
public class Client_MqttB implements MqttCallback {

    private static Client_MqttB instance = null;
    private final int qos = 2;
    private MqttClient sampleClient = null;

    private String broker = "tcp://0.0.0.0:1883";
    private String clientId = "Client del server : " + new Date().getTime();

    private List<String> ON_LINE = new LinkedList<>();    
    
    /*
        String string = "004-034556";
        String[] parts = string.split("-");
        String part1 = parts[0]; // 004
        String part2 = parts[1]; // 034556
        System.out.println("" + part1);
        System.out.println("" + part2);
    
    */
    
    
    public static Client_MqttB getInstance() {
        if (instance == null) {
            instance = new Client_MqttB();
        }
        return instance;
    }

    /* inizializza il client */
    private Client_MqttB() {
        super();

        try {
            sampleClient = new MqttClient(broker, clientId, new MemoryPersistence());
        } catch (MqttException ex) {
            Logger.getLogger(Client_MqttB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * inizializza il client e accede a un canale 
     */
    private void initializeConnection() {
        try {
            /* inizializza il client */
            if (sampleClient == null) {
                sampleClient = new MqttClient(broker, clientId, new MemoryPersistence());
            }

            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            /* Conneting to Broker */
            sampleClient.connect(connectOptions);
            System.out.println("Connected to broker" + broker );
            
            /* subscribe section */
            sampleClient.subscribe("UserConnected");
            sampleClient.subscribe("Talk");
            sampleClient.setCallback(this);
            
            publish("UserConnected", "is connected\n");

            
        } catch (Exception e) {
            Logger.getLogger(Client_MqttB.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void connect() {
        initializeConnection();        
    }

    /**
     * Public message
     * @param topic
     * @param message 
     */
    public void publish(String topic, String message) {
        String _message = clientId + " " + message;
                
        
        try {
            MqttMessage messageMM = new MqttMessage(_message.getBytes());
            messageMM.setQos(qos);
            
            if (sampleClient == null || !sampleClient.isConnected()) {
                initializeConnection();
            }
            
            sampleClient.publish(topic, messageMM);
        } catch (Exception ex) {
            Logger.getLogger(Client_MqttB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void connectionLost(Throwable thrwbl) {

    }

    /**
     * Riceve un messaggio dal server e lo mette a video
     * Dal server metodo public
     * @param topic
     * @param mm
     * @throws Exception 
     */
    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
        System.out.println("TOPIC: " + topic);
        System.out.println("MESSAGE: " + new String(mm.getPayload()) + "\n");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {

    }

    private List<String> getON_LINE() {
        return ON_LINE;
    }

    private void addON_LINE(String ON_LINE) {
        this.ON_LINE.add(ON_LINE);
    }

    
}
