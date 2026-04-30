#pragma once

#include <stdio.h>
#include "../Environment.h"

void rhenium_lib_console_write(char *text) { printf("%s", text); }

void rhenium_lib_console_write_line() { printf("%s", RHENIUM_LIB_EOL); }
