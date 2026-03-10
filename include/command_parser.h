/**
 * @file command_parser.h
 * @brief Parse function, which gets terminal command.
 *
 * @details Function is being used to get initial data, that is necessary for the program to work.
 * @attention Program highly depends on the order, in which arguments were typed in.
 * Also it heavily depends on the flags and the way user typed them.
 * @note To learn the way to properly type in data, type <u>/program help</u> or read our user's manual
 */

#ifndef FLAG_PARSER_H
#define FLAG_PARSER_H
#include "command.h"

/**
 * @brief Function scans command terminal for file's name, algorithm type and file output type.
 *
 * @details In the process of scanning terminal function detects flags and process them.
 *
 * Valid input will look like this:
 * ./name_of_the_program <file_name> -a <algorithm_type> -t/-b
 *
 * Flags meaning:
 * -a - precedes <algorithm_type>.
 * By default <algorithm_type> is triangulation.
 * -t - output file has txt format.
 * -b - output file has binary format.
 * By default file has text format.
 *
 * @param argc The number of input arguments.
 * @param argv Array of the input arguments.
 * @param cmd Structure elements, that are used for storing data passed in the terminal.
 *
 * @return 0 or error's id.
 */
int parse_command(int argc, char *argv[], Command *cmd);

#endif // FLAG_PARSER_H