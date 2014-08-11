package com.ttolley.pongbot.controller;

import com.sun.glass.events.KeyEvent;

public enum Command {

    UP(1, KeyEvent.VK_UP),
    DOWN(2, KeyEvent.VK_DOWN),
    LEFT(4, KeyEvent.VK_LEFT),
    RIGHT(8, KeyEvent.VK_RIGHT),
    FLYWHEEL_ON(16, KeyEvent.VK_R),
    FLYWHEEL_OFF(32, KeyEvent.VK_E),
    FIRE(64, KeyEvent.VK_F);

    public byte commandBit;
    public final int key;

    private Command(int bitMask, int key) {
        this.commandBit = (byte) bitMask;
        this.key = key;
    }

    public static Command fromKey(int key) {
        for (Command command : Command.values()) {
            if (command.key == key) {
                return command;
            }
        }
        return null;
    }

}
