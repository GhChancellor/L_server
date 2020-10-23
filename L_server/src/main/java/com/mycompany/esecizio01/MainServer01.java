/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.esecizio01;

import com.mycompany.esecizio01.mqtt.MQTTServer;

/**
 *
 * @author lele
 */
public class MainServer01 {
    
    public static void main(String[] args) {
        
        try {
            MQTTServer mQTTServer = new MQTTServer();
            mQTTServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
                
    }
    
}
