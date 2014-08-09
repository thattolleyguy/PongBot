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
        : MotorDriver(), currentSpeed(0), leftMotorNumber(leftNumber),
        rightMotorNumber(rightNumber), leftMotorDirection(RELEASE),
        rightMotorDirection(RELEASE), servoPin(servoPin), servoPos(0) {
            AFMS = Adafruit_MotorShield();

        }

        void initialize() {
            leftMotor = AFMS.getMotor(1);
            rightMotor = AFMS.getMotor(4);
            paddleServo.attach(servoPin);
            paddleServo.write(servoPos);
            AFMS.begin();
            setSpeed(255);

        }

        void moveLeft() {
            leftMotorDirection = FORWARD;
            rightMotorDirection = FORWARD;
            setSpeed(150);
            updateDirection();
        }

        void moveRight() {
            leftMotorDirection = BACKWARD;
            rightMotorDirection = BACKWARD;
            setSpeed(150);
            updateDirection();
        }

        void stopMotor() {
            leftMotorDirection = RELEASE;
            rightMotorDirection = RELEASE;
            setSpeed(0);
            updateDirection();
        }

        void paddleForward() {
            Serial.println("Moving paddle forward");
            if (servoPin < 180) {
                servoPos += 40;
                updatePaddle();
            }
        }

        void paddleBack() {
            Serial.println("Moving paddle backward");
            if (servoPos > 0) {
                servoPos -= 40;
                updatePaddle();
            }
        }

        void paddleStop() {

        }

        void updatePaddle() {
            paddleServo.write(servoPos);
        }

        void updateDirection() {
            leftMotor->run(leftMotorDirection);
            rightMotor->run(rightMotorDirection);
        }

        void setSpeed(int speed) {
            currentSpeed = speed;
            leftMotor->setSpeed(speed);
            rightMotor->setSpeed(speed);
        }

        int getSpeed() const {
            return currentSpeed;
        }

    private:
        Adafruit_MotorShield AFMS;
        Adafruit_DCMotor *leftMotor;
        Adafruit_DCMotor *rightMotor;
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
