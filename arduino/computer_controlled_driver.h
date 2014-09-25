/**
 * @file remote_control.h
 * @brief remote control driver definition for the Michelino robot.
 * @author Miguel Grinberg
 */

namespace Michelino {

    class RemoteControl : public ControlDriver {
    public:

        /**
         * @brief Class constructor.
         */
#ifdef HARDWARE_SERIAL

        RemoteControl(HardwareSerial &serial) : serial(serial) {
        }
#endif

#ifdef SOFTWARE_SERIAL

        RemoteControl(SoftwareSerial &serial) : serial(serial) {
        }
#endif

        /**
         * @brief Return the next remote command, if available.
         * @param cmd a reference to a command_t struct where the command
         *   information will be stored.
         * @return true if a remote command is available, false if not.
         */

        bool getRemoteCommand(command_t& cmd) {
            if (!serial.available()) {
                return false; // no commands available
            }
            char command = serial.read();

            if ((command & 0x10) != 0) {
                cmd.direction = command_t::moveLeft;
            } else {
                cmd.direction = command_t::moveRight;
            }
            if ((command & 0x40) != 0) {
                serial.println("Received center command");
                cmd.motion = command_t::center;
            } else {
                if ((command & 0x20) != 0) {
                    cmd.motion = command_t::paddleLeft;
                } else {
                    cmd.motion = command_t::paddleRight;
                }
            }
            int receivedSpeed = ((int) command & 0x0f);
            cmd.speedPercent = ((float) receivedSpeed) / 15.0;
            lastCommand = command;
            return true;
        }


    private:
        char lastCommand;
#ifdef HARDWARE_SERIAL
        HardwareSerial serial;
#endif
#ifdef SOFTWARE_SERIAL
        SoftwareSerial serial;
#endif

    };
};
