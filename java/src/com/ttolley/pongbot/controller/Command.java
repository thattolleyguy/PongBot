package com.ttolley.pongbot.controller;

public class Command {

    public static enum RobotDirection {

        LEFT((byte) 0x10),
        RIGHT((byte) 0x00);
        public final byte bitmask;

        private RobotDirection(byte bitmask) {
            this.bitmask = bitmask;
        }

    }

    public static enum PaddleAction {

        SWING((byte) 0x40),
        WIND_UP_LEFT((byte) 0x20),
        WIND_UP_RIGHT((byte) 0x00);
        public final byte bitmask;

        private PaddleAction(byte bitmask) {
            this.bitmask = bitmask;
        }
    }

    public final RobotDirection direciton;
    public final PaddleAction paddle;
    public final double speedPercentage;

    public Command(RobotDirection direciton, PaddleAction paddle, double speedPercentage) {
        this.direciton = direciton;
        this.paddle = paddle;
        this.speedPercentage = speedPercentage;
    }

    public byte toByte() {
        byte command = direciton.bitmask;
        command += paddle.bitmask;
        command += 16 * Math.abs(speedPercentage);
        return command;
    }

}
