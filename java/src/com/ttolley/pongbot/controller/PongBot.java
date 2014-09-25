package com.ttolley.pongbot.controller;

import com.ttolley.pongbot.controller.Command.PaddleAction;
import com.ttolley.pongbot.controller.Command.RobotDirection;
import com.ttolley.pongbot.opencv.CvWorker;
import com.ttolley.pongbot.opencv.FilteredObject;
import com.ttolley.pongbot.opencv.PublishObject;
import static java.lang.Compiler.command;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class PongBot {

    private final SerialDevice sd;

    private final DescriptiveStatistics yDirection;
    Double lastYPos = null;
    private boolean initialized = false;

    public PongBot() {
        this.sd = new SerialDevice();
        yDirection = new DescriptiveStatistics();
        yDirection.setWindowSize(4);
    }

    public boolean initialize() {

        this.initialized = sd.initialize();
        return initialized;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void registerEventHandler(SerialEventHandler handler) {
        this.sd.registerEventHandler(handler);
    }

    public void sendData(byte[] data) {
        this.sd.sendData(data);
    }

    public void movePaddle(double posOffset) {

    }

    public void updateBot(PublishObject obj1) {
        FilteredObject ballObject = obj1.getObject(CvWorker.FilterType.BALL);
        if (ballObject.objPosition != null) {
            final double scaleX = ballObject.thresholdImage.width() / 2.0;
            double scaledX = ((ballObject.objPosition.x / scaleX) - 1);
            final double scaleY = ballObject.thresholdImage.height() / 2.0;
            double scaledY = ((ballObject.objPosition.y / scaleY) - 1);

            RobotDirection direction = Command.RobotDirection.RIGHT;
            // Set movement direction
            if (scaledX < 0) {
                direction = RobotDirection.LEFT;
            }
            // Set movement speed
            PaddleAction paddle = Command.PaddleAction.WIND_UP_LEFT;
            if (lastYPos != null) {
                double yDiff = scaledY - lastYPos;
                yDirection.addValue(yDiff);
                if (yDirection.getMean() > 0 && scaledY > 0.9) {
                    // Swing the paddle forward
                    paddle = PaddleAction.SWING;
                }
            } else {
                paddle = PaddleAction.SWING;
            }
            Command command = new Command(direction, paddle, scaledX);

            sd.sendData(new byte[]{command.toByte()});

            lastYPos = scaledY;
        }
    }

    private void swingPaddleForward() {

    }

    private void prepPaddle() {
    }

}
