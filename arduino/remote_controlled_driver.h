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
            if (serial.available()) {
                receivedCommand = true;
                char command = serial.read();
                if (command != lastCommand) {
                    Serial.print("received command:");
                    Serial.println(command);
                    lastCommand = command;
                }

            }
            if (receivedCommand) {
                if (lastCommand == '4') {
                    cmd.direction = command_t::moveLeft;
                } else if (lastCommand == '6') {
                    cmd.direction = command_t::moveRight;
                }
                if (lastCommand == 'D') {
                    cmd.motion = command_t::center;
                } else if (lastCommand == 'C') {
                    cmd.motion = command_t::paddleRight;
                }

                if (lastCommand != '4' && lastCommand != '6')
                    cmd.speedPercent = 0.0;
                else
                    cmd.speedPercent = 1.0;
                return true;
            } else return false;
        }


    private:
        char lastCommand;
        bool receivedCommand = false;
#ifdef HARDWARE_SERIAL
        HardwareSerial serial;
#endif
#ifdef SOFTWARE_SERIAL
        SoftwareSerial &serial;
#endif

    };
};
