/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ttolley.pongbot.controller;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;

/**
 *
 * @author tyler
 */
public interface SerialEventHandler {

    public void handle(SerialPortEvent event) ;

    public void registerSerialPort(SerialPort serialPort);

}
