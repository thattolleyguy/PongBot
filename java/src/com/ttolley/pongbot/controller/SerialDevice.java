/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ttolley.pongbot.controller;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author tyler
 */
public class SerialDevice implements SerialPortEventListener {

    SerialPort serialPort = null;

    private static final String PORT_NAMES[] = {
        //        "/dev/tty.usbmodem", // Mac OS X
        "/dev/usbdev", // Linux
        "/dev/tty", // Linux
        "/dev/serial" // Linux
    //        "COM3", // Windows
    };

    private String appName;
    private OutputStream output;

    private static final int TIME_OUT = 1000; // Port open timeout
    private static final int DATA_RATE = 9600; // Arduino serial port
    private List<SerialEventHandler> handlers = new ArrayList<>();

    public boolean initialize() {
        try {
            CommPortIdentifier portId = null;
            Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

            // Enumerate system ports and try connecting to Arduino over each
            //
            System.out.println("Trying:");
            while (portId == null && portEnum.hasMoreElements()) {
                // Iterate through your host computer's serial port IDs
                //
                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
                System.out.println("   port" + currPortId.getName());
                for (String portName : PORT_NAMES) {
                    if (currPortId.getName().equals(portName)
                            || currPortId.getName().startsWith(portName)) {

                        // Try to connect to the Arduino on this port
                        //
                        // Open serial port
                        serialPort = (SerialPort) currPortId.open(appName, TIME_OUT);
                        portId = currPortId;
                        System.out.println("Connected on port" + currPortId.getName());
                        break;
                    }
                }
            }
            portId = CommPortIdentifier.getPortIdentifier("/dev/ttyACM0");

            if (portId == null || serialPort == null) {
                System.out.println("Oops... Could not connect to Arduino");
                return false;
            }

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);

            // Give the Arduino some time
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendData(byte[] data) {
        try {
            System.out.println("Sending data: '" + data + "'");

            // open the streams and send the "y" character
            output = serialPort.getOutputStream();
            output.write(data);
            Thread.sleep(100);
        } catch (Exception e) {
            System.err.println(e.toString());
            System.exit(0);
        }
    }

    //
    // This should be called when you stop using the port
    //
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    //
    // Handle serial port event
    //
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        for (SerialEventHandler serialEventHandler : handlers) {
            try {
                serialEventHandler.handle(oEvent);

            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        
    }
    public void registerEventHandler(SerialEventHandler handler)
    {
        handler.registerSerialPort(serialPort);
        this.handlers.add(handler);
    }

    public SerialDevice() {
        appName = getClass().getName();
    }

    public static void main(String[] args) throws Exception {
        SerialDevice test = new SerialDevice();
        if (test.initialize()) {
//            test.sendData("f");
//            test.sendData("r");
//            test.sendData("f");
//            test.sendData("r");
            test.sendData("7777".getBytes());
            test.close();
        }

        // Wait 5 seconds then shutdown
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
        }
    }
}
