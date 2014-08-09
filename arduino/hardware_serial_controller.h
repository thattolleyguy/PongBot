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
        RemoteControl(HardwareSerial &serial) : RemoteControlDriver(), serial(serial) {
        }

        virtual bool commandAvailable() {
            serial.print("Checking for command available:");
            int available = serial.available();
            serial.println(available);
            return available > 0;
        }

        virtual char read() {
            char ch = serial.read();
            serial.print("Read character ");
            serial.println(ch);
            return ch;
        }

    private:
        HardwareSerial serial;
    };
};
