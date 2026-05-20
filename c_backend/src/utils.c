#include "utils.h"
#include <string.h>
#include <stdio.h>

int parse_algorithm(const char *name) {
    if (strcmp(name, "alg1") == 0)  return 1;  
    if (strcmp(name, "alg2") == 0) return 2;  
    return -1;
}