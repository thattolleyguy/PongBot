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
        : MotorDriver(), currentSpeed(30), leftMotorNumber(leftNumber),
        rightMotorNumber(rightNumber), leftMotorDirection(FORWARD),
        rightMotorDirection(FORWARD), servoPin(servoPin), servoPos(90) {
            AFMS = Adafruit_MotorShield();

        }

        void initialize() {
            leftMotor = AFMS.getStepper(200, 1);
            rightMotor = AFMS.getStepper(200, 2);
            paddleServo.attach(servoPin);
            paddleServo.write(servoPos);
            AFMS.begin();
            setSpeed(500);

        }

        void moveLeft() {
            if (leftMotorDirection != FORWARD) {
                Serial.println("Changing direction to left");
                leftMotorDirection = FORWARD;
                rightMotorDirection = FORWARD;
                //            setSpeed(255);
            }
            move();
        }

        void moveRight() {
            if (leftMotorDirection != BACKWARD) {
                Serial.println("Changing direction to right");
                leftMotorDirection = BACKWARD;
                rightMotorDirection = BACKWARD;
                //            setSpeed(255);
            }
            move();
        }

        void stopMotor() {
            //            leftMotorDirection = RELEASE;
            //            rightMotorDirection = RELEASE;
            //            setSpeed(0);
            //            updateDirection();
        }

        void paddleCenter() {
            //            Serial.println("Moving paddle center");
            servoPos = 90;
            updatePaddle();
        }

        void paddleLeft() {
            //            Serial.println("Prepping paddle left");
            servoPos = 0;
            updatePaddle();
        }

        void paddleRight() {
            //            Serial.println("Prepping paddle right");
            servoPos = 180;
            updatePaddle();
        }

        void paddleStop() {

        }

        void updatePaddle() {
            paddleServo.write(servoPos);
        }

        void move() {
            //            leftMotor->step(100, leftMotorDirection, SINGLE);
            //            rightMotor->step(100, rightMotorDirection, SINGLE);
            leftMotor->step(100, FORWARD, SINGLE);
            rightMotor->step(100, FORWARD, SINGLE);
        }

        void setSpeed(int speed) {
            if (speed != currentSpeed) {
                Serial.print("Changing speed to");
                Serial.println(speed);
                currentSpeed = speed;
                leftMotor->setSpeed(speed);
                rightMotor->setSpeed(speed);
            }
        }

        int getSpeed() const {
            return currentSpeed;
        }

    private:
        Adafruit_MotorShield AFMS;
        Adafruit_StepperMotor *leftMotor;
        Adafruit_StepperMotor *rightMotor;
        Servo paddleServo;
        int servoPin;
        int servoPos;
        int currentSpeed;
        int leftMotorNumber;
        int rightMotorNumber;
        int leftMotorDirection;
        int rightMotorDirection;
    };
};
