/**
 * @file io.h
 * @brief All file manipulations are done in here.
 *
 * @details Function for file deserialization and function for creating output file.
 */

#ifndef IO_H
#define IO_H
#include "../include/graph.h"

/**
 * @brief Function opens file, checks validity (using check_file function), reads data in the file and saves it.
 *
 * @param filename name of the user's file.
 * @param elements structure, into which data from the file is saved.
 * @return 0 or error's id.
 */
int deserialize_file(char *filename, Edge *elements);

/**
 * @brief Create an output file.
 *
 * @param format desired format type of the output file.
 * @param elements structure, containing data, which should be represented in the output file
 * @return File containing graph with coordinates.
 */
FILE *create_output_file(enum file_output_type format, Node elements);

#endif // IO_H