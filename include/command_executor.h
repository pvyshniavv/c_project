/**
 * @file command_executor.h
 * @brief Function, which executes command from the terminal
 *
 * @details Function groups all of the functions, that execute commands, together.
 *
 * @note This is the core of the program.
 */

#ifndef COMMAND_EXECUTOR_H
#define COMMAND_EXECUTOR_H
#include "command.h"
#include "io.h"

/**
 * @brief Function executes terminal command
 *
 * @param cmd structure, containing terminal command
 * @return 0 or error's id
 */
int execute_command(Command cmd);

#endif // COMMAND_EXECUTOR_H