/* 
 * File:   ControlDriver.h
 * Author: tyler
 *
 * Created on September 23, 2014, 8:40 PM
 */

namespace Michelino {

    class ControlDriver {
    public:

        /**
         * @brief abstract representation of a remote command.
         */
        struct command_t {

            enum moveDirection {
                moveLeft, moveRight
            };

            enum paddleMotion {
                center, paddleLeft, paddleRight
            };
            moveDirection direction; /**< function key. */
            paddleMotion motion;
            float speedPercent;

            command_t() : direction(moveLeft), motion(center), speedPercent(0.0) {
            }

        };

        /**
         * @brief Class constructor.
         */
        ControlDriver() {
        }

        /**
         * @brief Return the next remote command, if available.
         * @param cmd a reference to a command_t struct where the command
         *   information will be stored.
         * @return true if a remote command is available, false if not.
         */



        virtual bool getRemoteCommand(command_t& cmd) = 0;




    };
};


