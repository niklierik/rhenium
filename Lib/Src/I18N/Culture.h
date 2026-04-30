#pragma once

#include <stdbool.h>
#include <unicode/ucol.h>
#include <unicode/ucasemap.h>
#include "../Types/String.h"

#ifndef RHENIUM_GEN
Rhenium_Lib_String *Rhenium_Lib_Default_CultureName;
#endif

typedef struct
{
    Rhenium_Lib_String *locale;
    UCollator *collator;
    UCaseMap *casemap;
    bool ignore_case;
    bool ignore_accents;
} Rhenium_Lib_Culture;

void Rhenium_Lib_CreateCulture(Rhenium_Lib_String *culture_name, Rhenium_Lib_Culture *out_culture)
{
    UErrorCode status = U_ZERO_ERROR;

    const char *locale = (char *)culture_name->data;
    out_culture->locale = culture_name;

    out_culture->casemap = ucasemap_open(locale, U_FOLD_CASE_DEFAULT, &status);

    out_culture->collator = ucol_open(locale, &status);

    ucol_setStrength(out_culture->collator, UCOL_TERTIARY);
}