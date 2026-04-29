#pragma once

#include <stdint.h>
#include <uchar.h>
#include <string.h>
#include <stdlib.h>
#include <unicode/ucol.h>
#include <unicode/ucasemap.h>
#include <unicode/utypes.h>

UCollator *_default_collator = NULL;
UCollator *_default_case_map = NULL;

typedef struct
{
    char8_t *data;
    int32_t length;
} Rhenium_Lib_String;

void Rhenium_Lib_String_MakeDirect(char8_t *data, Rhenium_Lib_String *out_target);
void Rhenium_Lib_String_MakeCopy(char8_t *data, Rhenium_Lib_String *out_target);
void Rhenium_Lib_String_MakeCopyOfSlice(char8_t *data, int32_t start, int32_t length, Rhenium_Lib_String *out_target);
bool Rhenium_Lib_String_Equals(Rhenium_Lib_String *self, Rhenium_Lib_String *other);
int32_t Rhenium_Lib_String_Compare(Rhenium_Lib_String *self, Rhenium_Lib_String *other);
int32_t Rhenium_Lib_String_Compare_Raw(char8_t *self, char8_t *other, UCollator *collator);
UCollator *Rhenium_Lib_DefaultCollator();
void Rhenium_Lib_DestroyDefaultCollator();

void Rhenium_Lib_String_MakeDirect(char8_t *data, Rhenium_Lib_String *out_target)
{
    out_target->data = data;
    out_target->length = strlen(data);
}

void Rhenium_Lib_String_MakeCopy(char8_t *data, Rhenium_Lib_String *out_target)
{
    int32_t length = strlen(data);

    out_target->length = length;
    out_target->data = malloc((length + 1) * sizeof(char8_t));
    memcpy(out_target->data, data, length);
    out_target->data[length] = '\0';
}

void Rhenium_Lib_String_MakeCopyOfSlice(char8_t *data, int32_t start, int32_t length, Rhenium_Lib_String *out_target)
{
    out_target->length = length;
    out_target->data = malloc((length + 1) * sizeof(char8_t));
    memcpy(out_target->data, &data[start], length);
    out_target->data[length] = '\0';
}

bool Rhenium_Lib_String_Equals(Rhenium_Lib_String *self, Rhenium_Lib_String *other)
{
    int result = Rhenium_Lib_String_Compare(self, other);
    return result == 0;
}

int32_t Rhenium_Lib_String_Compare(Rhenium_Lib_String *self, Rhenium_Lib_String *other)
{
    int32_t result = Rhenium_Lib_String_Compare_Raw(self->data, other->data, Rhenium_Lib_DefaultCollator());

    return result;
}

int32_t Rhenium_Lib_String_Compare_Raw(char8_t *self, char8_t *other, UCollator *collator)
{
    UErrorCode status = U_ZERO_ERROR;

    ucol_setStrength(collator, UCOL_TERTIARY);
    ucol_setNormalization(collator, UCOL_ON);

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

int32_t Rhenium_Lib_String_ToLower_Raw(const char8_t *src, char8_t *dest, int32_t dest_capacity)
{
    UErrorCode status = U_ZERO_ERROR;

    UCaseMap *csm = ucasemap_open("root", U_FOLD_CASE_DEFAULT, &status);
    if (U_FAILURE(status))
        return -1;

    int32_t result_len = ucasemap_utf8ToLower(csm,
                                              (char *)dest, dest_capacity,
                                              (const char *)src, -1,
                                              &status);

    ucasemap_close(csm);

    if (U_FAILURE(status))
        return -1;
    return result_len;
}

UCollator *Rhenium_Lib_DefaultCollator()
{
    if (_default_collator != NULL)
    {
        return _default_collator;
    }

    UErrorCode status = U_ZERO_ERROR;
    _default_collator = ucol_open("root", &status);
    ucol_setStrength(_default_collator, UCOL_TERTIARY);
    ucol_setNormalization(_default_collator, UCOL_ON);

    return _default_collator;
}

void Rhenium_Lib_DestroyDefaultCollator()
{
    if (_default_collator == NULL)
    {
        return;
    }

    ucol_close(_default_collator);
}

UCaseMap *Rhenium_Lib_DefaultCaseMap()
{
    if (_default_collator != NULL)
    {
        return _default_collator;
    }

    UErrorCode status = U_ZERO_ERROR;
    _default_collator = ucol_open("root", &status);
    ucol_setStrength(_default_collator, UCOL_TERTIARY);
    ucol_setNormalization(_default_collator, UCOL_ON);

    return _default_collator;
}

void Rhenium_Lib_DestroyDefaultCaseMap()
{
    if (_default_collator == NULL)
    {
        return;
    }

    ucol_close(_default_collator);
}
