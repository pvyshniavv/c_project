/**
 * @file error_handler.h
 * @brief Errors' constants, their definition and function(handle_error) that acts accordingly to the error type.
 *
 * @details Every error that can be present in our program is listed here.
 * Every error has its unique id (e.g 1, 2, 25).
 * IDs from 1-10 correspond to general and common errors.
 * IDs that start from digit 2 correspond to errors related to files.
 *
 * @attention As the project grows, more errors appear. When they do, make sure that you use this list and add every single error here.
 * When you add new error constants, don't forget to add description to them.
 * @warning NEVER USE RAW NUMBERS AS ERROR TYPES!
 */

#ifndef ERROR_HANDLER_H
#define ERROR_HANDLER_H

/**
 * @name Standard Error Codes.
 * @brief Initializing Errors' IDs definition for functions that can fail.
 *
 * @{
 */
#define ERROR_INVALID_PARAMETER (1) ///< Invalid argument passed.
#define ERROR (2)                   ///< to be continued.
/** @} */

void handle_error(int error_id);

#endif // ERROR_HANDLER_H