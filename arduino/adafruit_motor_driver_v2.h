/**
 * @file adafruit_motor_driver.h
 * @brief Motor device driver for the Adafruit motor shield.
 * @author Miguel Grinberg
 */

#include "motor_driver.h"

namespace Michelino {

    class Motor : public MotorDriver {
    public:

        /*
         * @brief Class constructor.
         * @param number the DC motor number to control, from 1 to 4.
         */
        Motor(int leftNumber, int rightNumber, int servoPin)
        : MotorDriver(), currentSpeed(0), motorDirection(RELEASE),
        servoPin(servoPin), servoPos(90) {
            AFMS = Adafruit_MotorShield();

        }

        void initialize() {
            motor1 = AFMS.getMotor(1);
            motor2 = AFMS.getMotor(2);
            motor3 = AFMS.getMotor(3);
            motor4 = AFMS.getMotor(4);
            paddleServo.attach(servoPin);
            paddleServo.write(servoPos);
            AFMS.begin();
            setSpeed(255);

        }

        void moveLeft() {
            if (motorDirection != BACKWARD) {
                Serial.println("Changing direction to left");
                motorDirection = BACKWARD;
                updateDirection();
            }
        }

        void moveRight() {
            if (motorDirection != FORWARD) {
                Serial.println("Changing direction to right");
                motorDirection = FORWARD;
                updateDirection();
            }
        }

        void stopMotor() {
            //            leftMotorDirection = RELEASE;
            //            rightMotorDirection = RELEASE;
            //            setSpeed(0);
            //            updateDirection();
        }

        void paddleCenter() {
            Serial.println("Moving paddle center");
            servoPos = 90;
            updatePaddle();
        }

        void paddleLeft() {
            Serial.println("Prepping paddle left");
            servoPos = 180;
            updatePaddle();
        }

        void paddleRight() {
            Serial.println("Prepping paddle right");
            servoPos = 0;
            updatePaddle();
        }

        void updatePaddle() {
            paddleServo.write(servoPos);
        }

        void updateDirection() {
            motor1->run(motorDirection);
            motor2->run(motorDirection);
            motor3->run(motorDirection);
            motor4->run(motorDirection);
        }

        void setSpeed(int speed) {
            if (speed != currentSpeed) {
                Serial.print("Changing speed to");
                Serial.println(speed);
                currentSpeed = speed;
                motor1->setSpeed(speed);
                motor2->setSpeed(speed);
                motor3->setSpeed(speed);
                motor4->setSpeed(speed);
            }
        }

        int getSpeed() const {
            return currentSpeed;
        }

    private:
        Adafruit_MotorShield AFMS;
        Adafruit_DCMotor *motor1;
        Adafruit_DCMotor *motor2;
        Adafruit_DCMotor *motor3;
        Adafruit_DCMotor *motor4;
        Servo paddleServo;
        int servoPin;
        int servoPos;
        int currentSpeed;
        int motorDirection;
    };
};
