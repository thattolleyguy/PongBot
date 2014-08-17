/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ttolley.pongbot.controller;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tyler
 */
public class ConsoleSerialEventHandler implements SerialEventHandler {

    private BufferedReader input;

    @Override
    public void handle(SerialPortEvent event) {
        try {
            switch (event.getEventType()) {
                case SerialPortEvent.DATA_AVAILABLE:
                    if (input != null) {
                        String inputLine = input.readLine();
                        System.out.println(inputLine);
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    @Override
    public void registerSerialPort(SerialPort serialPort) {
        try {
            input = new BufferedReader(
                    new InputStreamReader(
                            serialPort.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(ConsoleSerialEventHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
