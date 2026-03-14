#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <getopt.h>
#include "../include/command.h"
#include "../include/command_parser.h"
#include "../include/error_handler.h"

int main(int argc, char *argv[])
{
    parse_command(argc, argv, Command cmd);
    return 0;
}