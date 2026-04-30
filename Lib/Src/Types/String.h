#pragma once

#include <stdint.h>
#include <uchar.h>
#include <string.h>
#include <stdlib.h>
#include <unicode/ucol.h>
#include <unicode/ucasemap.h>
#include <unicode/utypes.h>
#include "../I18N/Culture.h"

typedef struct
{
    char8_t *data;
    int32_t numberOfBytes;
} Rhenium_Lib_String;

void Rhenium_Lib_String_MakeDirect(char8_t *data, Rhenium_Lib_String *out_target);
void Rhenium_Lib_String_MakeCopy(char8_t *data, Rhenium_Lib_String *out_target);
void Rhenium_Lib_String_MakeCopyOfSlice(char8_t *data, int32_t start, int32_t numberOfBytes, Rhenium_Lib_String *out_target);
bool Rhenium_Lib_String_Equals(Rhenium_Lib_String *self, Rhenium_Lib_String *other);
int32_t Rhenium_Lib_String_Compare(Rhenium_Lib_String *self, Rhenium_Lib_String *other);
int32_t Rhenium_Lib_String_Compare_Raw(char8_t *self, char8_t *other, Rhenium_Lib_Culture *culture);

void Rhenium_Lib_String_MakeDirect(char8_t *data, Rhenium_Lib_String *out_target)
{
    out_target->data = data;
    out_target->numberOfBytes = strlen(data);
}

void Rhenium_Lib_String_MakeCopy(char8_t *data, Rhenium_Lib_String *out_target)
{
    int32_t numberOfBytes = strlen(data);

    out_target->numberOfBytes = numberOfBytes;
    out_target->data = malloc(numberOfBytes * sizeof(char8_t));
    memcpy(out_target->data, data, numberOfBytes);
    out_target->data[numberOfBytes - 1] = '\0';
}

void Rhenium_Lib_String_MakeCopyOfSlice(char8_t *data, int32_t start, int32_t numberOfBytes, Rhenium_Lib_String *out_target)
{
    out_target->numberOfBytes = numberOfBytes;
    out_target->data = malloc(numberOfBytes * sizeof(char8_t));
    memcpy(out_target->data, &data[start], numberOfBytes);
    out_target->data[numberOfBytes - 1] = '\0';
}

int32_t Rhenium_Lib_String_Compare_Raw(char8_t *self, char8_t *other, Rhenium_Lib_Culture *culture)
{
    UErrorCode status = U_ZERO_ERROR;
    UCollator *collator = culture->collator;

    UCollationResult result =
        ucol_strcollUTF8(
            collator,
            (const char *)self,
            -1,
            (const char *)other,
            -1,
            &status);

    switch (result)
    {
    case UCOL_EQUAL:
        return 0;

    case UCOL_LESS:
        return -1;

    case UCOL_GREATER:
        return 1;
    }

    return -1;
}

int32_t Rhenium_Lib_String_ToLower_Raw(const char8_t *src, char8_t *dest, int32_t dest_capacity, Rhenium_Lib_Culture *culture)
{
    UErrorCode status = U_ZERO_ERROR;

    int32_t result_len = ucasemap_utf8ToLower(culture->casemap,
                                              (char *)dest, dest_capacity,
                                              (const char *)src, -1,
                                              &status);

    if (U_FAILURE(status))
    {
        return -1;
    }
    return result_len;
}
