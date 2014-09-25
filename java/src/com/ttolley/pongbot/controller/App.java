/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ttolley.pongbot.controller;

import com.ttolley.pongbot.opencv.CameraEventHandler;
import com.ttolley.pongbot.opencv.CvWorker;
import com.ttolley.pongbot.opencv.CvWorker.FilterType;
import com.ttolley.pongbot.opencv.FilteredObject;
import com.ttolley.pongbot.opencv.PublishObject;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author tyler
 */
public class App extends javax.swing.JFrame {

    byte inputState = 0;

    private CvPanel webcamImagePanel;
    private JFrame webcamImageFrame;
    private CvPanel ballPanel;
    private JFrame ballFrame;
    private CvPanel robotPanel;
    private JFrame robotFrame;
    private final PongBot pb;
    private final CvWorker worker;
    private final Map<FilterType, CvFilterPanel> panelMap;

    /**
     * Creates new form App
     */
    public App() {
        worker = new CvWorker();
        panelMap = new EnumMap<>(FilterType.class);
        for (FilterType filterType : FilterType.values()) {
            final CvWorker.Filter filter = new CvWorker.Filter(filterType.hMin, filterType.sMin, filterType.vMin, filterType.hMax, filterType.sMax, filterType.vMax,
                    filterType.objSize, filterType.numObj, filterType.erode, filterType.dilate);
            worker.setFilter(filterType, filter);
            panelMap.put(filterType, new CvFilterPanel(filter));
        }
        initComponents();
        jSplitPane2.setTopComponent(panelMap.get(FilterType.BALL));
        jSplitPane2.setBottomComponent(panelMap.get(FilterType.ROBOT));
        pb = new PongBot();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSpinner2 = new javax.swing.JSpinner();
        jSpinner3 = new javax.swing.JSpinner();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        serialLog = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        dataField = new java.awt.TextField();
        jPanel8 = new javax.swing.JPanel();
        sendButton = new javax.swing.JButton();
        connectButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jSplitPane2 = new javax.swing.JSplitPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 450));

        jSplitPane1.setDividerLocation(400);

        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.BorderLayout());

        scrollPane.setAutoscrolls(true);

        serialLog.setColumns(20);
        serialLog.setRows(5);
        scrollPane.setViewportView(serialLog);

        jPanel5.add(scrollPane, java.awt.BorderLayout.CENTER);

        jPanel6.add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel7.setLayout(new java.awt.BorderLayout());
        jPanel7.add(dataField, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanel8.setLayout(new java.awt.BorderLayout());

        sendButton.setText("Send");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });
        jPanel8.add(sendButton, java.awt.BorderLayout.WEST);

        connectButton.setText("Connect");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });
        jPanel8.add(connectButton, java.awt.BorderLayout.EAST);

        jButton1.setText("Start CV");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton1, java.awt.BorderLayout.PAGE_END);

        jPanel2.add(jPanel8, java.awt.BorderLayout.LINE_END);

        jPanel6.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jSplitPane1.setRightComponent(jPanel6);

        jSplitPane2.setDividerLocation(180);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setLeftComponent(jSplitPane2);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed

        if (!pb.initialize()) {
            throw new IllegalStateException("Unable to initialize serial connection");
        }
        pb.registerEventHandler(new ConsoleSerialEventHandler());
        pb.registerEventHandler(new SerialEventHandler() {
            BufferedReader input;

            @Override
            public void handle(SerialPortEvent event) {
                String line = null;
                try {
                    switch (event.getEventType()) {
                        case SerialPortEvent.DATA_AVAILABLE:
                            if (input != null) {
                                line = input.readLine();
                            }
                            break;

                        default:
                            break;
                    }
                } catch (Exception e) {
                    line = e.toString();
                }
                App.this.serialLog.append(line + "\r\n");
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
        });
    }//GEN-LAST:event_connectButtonActionPerformed

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        final String data = dataField.getText();
        dataField.setText("");
        pb.sendData(new byte[]{(byte) Integer.parseInt(data, 16)});
    }//GEN-LAST:event_sendButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        webcamImageFrame = new JFrame("Camera");
        webcamImageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        webcamImageFrame.setSize(640, 480);
        webcamImageFrame.setBounds(0, 0, webcamImageFrame.getWidth(), webcamImageFrame.getHeight());
        webcamImagePanel = new CvPanel();
        webcamImageFrame.setContentPane(webcamImagePanel);
        ballFrame = new JFrame("Ball Threshold");
        ballFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ballFrame.setSize(640, 480);
        ballFrame.setBounds(0, 0, ballFrame.getWidth(), ballFrame.getHeight());
        ballPanel = new CvPanel();
        ballFrame.setContentPane(ballPanel);
        robotFrame = new JFrame("Robot Threshold");
        robotFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        robotFrame.setSize(640, 480);
        robotFrame.setBounds(0, 0, robotFrame.getWidth(), robotFrame.getHeight());
        robotPanel = new CvPanel();
        robotFrame.setContentPane(robotPanel);

        worker.execute();

        worker.registerCameraEventHandler(new CameraEventHandler() {

            @Override
            public void handleLatestCameraResult(PublishObject obj1) {

                if (!webcamImageFrame.isVisible()) {
                    webcamImageFrame.setVisible(true);
                    webcamImageFrame.setSize(obj1.webcamImage.width() + 40, obj1.webcamImage.height() + 60);
                }
                webcamImagePanel.setimagewithMat(obj1.webcamImage);
                webcamImageFrame.repaint();

                FilteredObject ballFilteredObject = obj1.getObject(FilterType.BALL);
                if (!ballFrame.isVisible()) {
                    ballFrame.setVisible(true);
                    ballFrame.setSize(ballFilteredObject.thresholdImage.width() + 40, ballFilteredObject.thresholdImage.height() + 60);
                }
                ballPanel.setimagewithMat(ballFilteredObject.thresholdImage);
                ballFrame.repaint();

                FilteredObject robotFilteredObject = obj1.getObject(FilterType.ROBOT);
                if (!robotFrame.isVisible()) {
                    robotFrame.setVisible(true);
                    robotFrame.setSize(robotFilteredObject.thresholdImage.width() + 40, robotFilteredObject.thresholdImage.height() + 60);
                }
                robotPanel.setimagewithMat(robotFilteredObject.thresholdImage);
                robotFrame.repaint();

                if (pb.isInitialized()) {
                    pb.updateBot(obj1);
                }

            }
        });

    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new App().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton connectButton;
    private java.awt.TextField dataField;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JSpinner jSpinner3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JButton sendButton;
    private javax.swing.JTextArea serialLog;
    // End of variables declaration//GEN-END:variables
    TimerTask timerTask;

}

