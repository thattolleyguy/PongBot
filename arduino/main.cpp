// Michelino
// Robot Vehicle firmware for the Arduino platform
//
// Copyright (c) 2013 by Miguel Grinberg
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is furnished
// to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
// AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

/**
 * @file michelino.ino
 * @brief Arduino robot vehicle firmware.
 * @author Miguel Grinberg
 */

#define LOGGING

// Device drivers
// Enable one driver in each category

// Motor controller:
#define ENABLE_ADAFRUIT_MOTOR_DRIVER_V2

// Distance sensor
//#define ENABLE_NEWPING_DISTANCE_SENSOR_DRIVER

// Remote control:
//#define ENABLE_SOFTWARE_SERIAL_REMOTE_CONTROL_DRIVER
#define ENABLE_HARDWARE_SERIAL_REMOTE_CONTROL_DRIVER



#ifdef ENABLE_ADAFRUIT_MOTOR_DRIVER_V2
#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include <Arduino.h>
#include <Servo.h>
#include "utility/Adafruit_PWMServoDriver.h"
#include "adafruit_motor_driver_v2.h"
#define MOTOR_INIT 1,4,9
#endif


#ifdef ENABLE_NEWPING_DISTANCE_SENSOR_DRIVER
#include <NewPing.h>
#include "newping_distance_sensor.h"
#define DISTANCE_SENSOR_INIT 14,14
#endif

#ifdef ENABLE_SOFTWARE_SERIAL_REMOTE_CONTROL_DRIVER
#define BT_RX_PIN 15                    /**< RX pin for Bluetooth communcation */
#define BT_TX_PIN 16                    /**< TX pin for Bluetooth communcation */

#include <SoftwareSerial.h>
SoftwareSerial BTSerial(BT_RX_PIN, BT_TX_PIN);
#include "software_serial_controller.h"
#define REMOTE_CONTROL_INIT BTSerial
#endif

#ifdef ENABLE_HARDWARE_SERIAL_REMOTE_CONTROL_DRIVER
#include "hardware_serial_controller.h"
#define REMOTE_CONTROL_INIT Serial
#endif


#include "logging.h"

namespace Michelino {

    class Robot {
    public:

        /*
         * @brief Class constructor.
         */
        Robot()
        :
        motor(MOTOR_INIT),
        remoteControl(REMOTE_CONTROL_INIT) {
        }

        /*
         * @brief Initialize the robot state.
         */
        void initialize() {
            motor.initialize();
        }

        /*
         * @brief Update the state of the robot based on input from sensor and remote control.
         *  Must be called repeatedly while the robot is in operation.
         */
        void run() {
            RemoteControlDriver::command_t remoteCmd;
            bool haveRemoteCmd = remoteControl.getRemoteCommand(remoteCmd);
            switch (remoteCmd.direction) {
                case RemoteControlDriver::command_t::left:
                    motor.moveLeft();
                    break;
                case RemoteControlDriver::command_t::right:
                    motor.moveRight();
                    break;
                case RemoteControlDriver::command_t::stopMotor:
                    motor.stopMotor();
                    break;
                default:
                    break;
            }
            switch (remoteCmd.motion) {
                case RemoteControlDriver::command_t::forward:
                    motor.paddleForward();
                    break;
                case RemoteControlDriver::command_t::back:
                    motor.paddleBack();
                    break;
                case RemoteControlDriver::command_t::stopPaddle:
                    motor.paddleStop();
                    break;
                default:
                    break;
            }
        }

    private:
        Motor motor;
        RemoteControl remoteControl;

    };
};

Michelino::Robot robot;

void setup() {
    Serial.begin(9600);
    robot.initialize();

}

void loop() {
    robot.run();
}


