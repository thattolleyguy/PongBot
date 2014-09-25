#define LOGGING

// Device drivers
// Enable one driver in each category

// Motor controller:
#define ENABLE_ADAFRUIT_MOTOR_DRIVER_V2
//#define ENABLE_ADAFRUIT_STEPPER_MOTOR_DRIVER_V2

// Remote control:
//#define SOFTWARE_SERIAL
#define HARDWARE_SERIAL
#define COMPUTER_CONTROLLED_DRIVER
//#define REMOTE_CONTROLLED_DRIVER



#ifdef ENABLE_ADAFRUIT_MOTOR_DRIVER_V2
#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include <Arduino.h>
#include <Servo.h>
#include "utility/Adafruit_PWMServoDriver.h"
#include "adafruit_motor_driver_v2.h"
#define MOTOR_INIT 1,4,9
#endif

#ifdef ENABLE_ADAFRUIT_STEPPER_MOTOR_DRIVER_V2
#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include <Arduino.h>
#include <Servo.h>
#include "utility/Adafruit_PWMServoDriver.h"
#include "adafruit_stepper_motor_driver_v2.h"
#define MOTOR_INIT 1,4,9
#endif


#ifdef ENABLE_NEWPING_DISTANCE_SENSOR_DRIVER
#include <NewPing.h>
#include "newping_distance_sensor.h"
#define DISTANCE_SENSOR_INIT 14,14
#endif

#include "control_driver.h"

#ifdef HARDWARE_SERIAL
#define REMOTE_CONTROL_INIT Serial
#endif

#ifdef SOFTWARE_SERIAL
#define BT_RX_PIN 15                    /**< RX pin for Bluetooth communcation */
#define BT_TX_PIN 16                    /**< TX pin for Bluetooth communcation */

#include <SoftwareSerial.h>
SoftwareSerial BTSerial(BT_RX_PIN, BT_TX_PIN);
#define REMOTE_CONTROL_INIT BTSerial
#endif

#ifdef COMPUTER_CONTROLLED_DRIVER
#include "computer_controlled_driver.h"
#endif

#ifdef REMOTE_CONTROLLED_DRIVER
#include "remote_controlled_driver.h"
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
            ControlDriver::command_t remoteCmd;
            bool haveRemoteCmd = remoteControl.getRemoteCommand(remoteCmd);

            if (haveRemoteCmd) {
                motor.setSpeed((int) (255 * remoteCmd.speedPercent));
                switch (remoteCmd.direction) {
                    case ControlDriver::command_t::moveLeft:
                        motor.moveLeft();
                        break;
                    case ControlDriver::command_t::moveRight:
                        motor.moveRight();
                        break;
                    default:
                        break;
                }
                switch (remoteCmd.motion) {
                    case ControlDriver::command_t::center:
                        motor.paddleCenter();
                        break;
                    case ControlDriver::command_t::paddleLeft:
                        motor.paddleLeft();
                        break;
                    case ControlDriver::command_t::paddleRight:
                        motor.paddleRight();
                        break;
                    default:
                        break;
                }
            }

        }

    private:
        Motor motor;
        RemoteControl remoteControl;

    };
};

Michelino::Robot robot;

void setup() {

#ifdef SOFTWARE_SERIAL
    BTSerial.begin(9600);
#endif
    Serial.begin(9600);
    robot.initialize();

}

void loop() {
    robot.run();
}


