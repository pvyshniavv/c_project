#include <stdio.h>
#include <stdlib.h>
#include <getopt.h>
#include "command.h"
#include "command_parser.h"
#include "error_handler.h"

int main(int argc, char *argv[])
{
    Command cmd;
    handle_error(parse_command(argc, argv, &cmd));
    return 0;
}