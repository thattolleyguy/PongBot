/**
 * @file bluestick_remote_control.h
 * @brief remote control driver for the BlueStick Android remote control app.
 * @author Miguel Grinberg
 */

#include "remote_control.h"

namespace Michelino {

    class RemoteControl : public RemoteControlDriver {
    public:

        /**
         * @brief Class constructor.
         */
        RemoteControl(SoftwareSerial &serial) : RemoteControlDriver(), lastChar('\0'), serial(serial) {
        }

        virtual bool commandAvailable() {
            return serial.available() <= 0;
        }

        virtual char read() {
            return serial.read();
        }

    private:
        char lastChar;
        SoftwareSerial serial;
    };
};
