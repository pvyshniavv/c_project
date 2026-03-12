/**
 * @file command.h
 * @brief Structure, which contain data gathered from terminal and enums, which contain a list of format types and algorithms.
 *
 * @details Structure Command is being used for storing information, passed into the terminal.
 * Enum is being used as a compact storage for algorithms and format types.
 *
 * @warning Although you can widen enum/structure, NEVER modify existing parameters niether in enums nor in the structure!
 */

#ifndef COMMAND_H
#define COMMAND_H

/**
 * @brief Set of algorithms' types.
 *
 */
enum algorithm_type
{
    TRIANGULATION, ///< Used for highlighting chosen algorithm type, in our case triangulation algorithm.
    SPECTRAL       ///< Used for highlighting chosen algorithm type, in our case spectral algorithm.
};

/**
 * @brief Set of file format types.
 *
 */
enum file_output_type
{
    TEXT,  ///< Used for highlighting chosen format type, in our case txt format.
    BINARY ///< Used for highlighting chosen format type, in our case binary format.
};

/**
 * @brief Structure of three key elements, that are used for storing data passed in the terminal.
 *
 */
typedef struct
{
    char *input_file_name_or_path;        ///< Used for storing input file's name/path.
    char *output_file_name_or_path;       ///< Used for storing output file's name/path.
    enum algorithm_type chosen_algorithm; ///< Used for storing algorithm's type.
    enum file_output_type chosen_format;  ///< Used for storing desired format of the output file.
} Command;

#endif // COMMAND_H