/**
 * @file remote_control.h
 * @brief remote control driver definition for the Michelino robot.
 * @author Miguel Grinberg
 */

namespace Michelino {

    class RemoteControlDriver {
    public:

        /**
         * @brief abstract representation of a remote command.
         */
        struct command_t {

            enum moveDirection {
                left, right, stopMotor
            };

            enum paddleMotion {
                forward, back, stopPaddle
            };
            moveDirection direction; /**< function key. */
            paddleMotion motion;

            command_t() : direction(stopMotor), motion(stopPaddle) {
            }

        };

        /**
         * @brief Class constructor.
         */
        RemoteControlDriver() {
        }

        /**
         * @brief Return the next remote command, if available.
         * @param cmd a reference to a command_t struct where the command
         *   information will be stored.
         * @return true if a remote command is available, false if not.
         */
        
        virtual bool commandAvailable() = 0;

        virtual char read() = 0;
        
        
        bool getRemoteCommand(command_t& cmd) {
            cmd.motion = command_t::stopPaddle;
            cmd.direction = command_t::stopMotor;
            if (!commandAvailable()) {
                return false; // no commands available
            }
            char ch = read();
            switch (ch) {
                case '8': // up
                    cmd.motion = command_t::forward;
                    cmd.direction = command_t::stopMotor;
                    break;
                case '2': // down
                    cmd.motion = command_t::back;
                    cmd.direction = command_t::stopMotor;
                    break;
                case '4': // right
                    cmd.direction = command_t::left;
                    cmd.motion = command_t::stopPaddle;
                    break;
                case '6': // left
                    cmd.direction = command_t::right;
                    cmd.motion = command_t::stopPaddle;
                    break;
                case '7': // Swing forward and left
                    cmd.direction = command_t::left;
                    cmd.motion = command_t::forward;
                    break;
                case '9': // Swing forward and right
                    cmd.direction = command_t::right;
                    cmd.motion = command_t::forward;
                    break;
                case '1': // Swing back and left
                    cmd.direction = command_t::left;
                    cmd.motion = command_t::back;
                    break;
                case '3': // Swing back and right
                    cmd.direction = command_t::right;
                    cmd.motion = command_t::back;
                    break;
                case '5': // Stop all
                    cmd.direction = command_t::stopMotor;
                    cmd.motion = command_t::stopPaddle;
                    break;
                default:
                    break;
            }

            lastChar = ch;
            return true;
        }


    private:
        char lastChar;


    };
};
