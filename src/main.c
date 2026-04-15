#include <stdio.h>
#include <stdlib.h>
#include <getopt.h>
#include "command.h"
#include "command_parser.h"
#include "error_handler.h"
#include "command_executor.h"

int main(int argc, char *argv[])
{
    Command cmd;

    if(parse_command(argc, argv, &cmd) != 0) {
        return 1;
    }

    int result = execute_command(cmd);
    return result;
}