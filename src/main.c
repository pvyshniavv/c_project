#include <stdio.h>
#include <stdlib.h>
#include <getopt.h>
#include "../include/command.h"
#include "../include/command_parser.h"
#include "../include/error_handler.h"

int main(int argc, char *argv[])
{
    Command cmd;
    parse_command(argc, argv, &cmd);
    return 0;
}