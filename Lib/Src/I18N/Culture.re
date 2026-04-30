c_decl
class Culture(    
    #c_field("collator")
    using take collator: StringCollator,

    #c_field("casemap")
    using take casemap: StringCaseMap,
    #c_field("locale")
    locale: Ref<String>
) {
    static using DefaultLocale = "root";
    static using DefaultCulture = CreateCulture(DefaultLocale);

    readonly property Locale: Ref<String> = locale;

    #c_field("ignore_case")
    property IgnoreCase: Boolean = true;
    
    #c_field("ignore_accents")
    property IgnoreAccents: Boolean = true;
}
as Rhenium_Lib_Culture;

c_decl type StringCollator as UCollator;
c_decl type StringCaseMap as UCaseMap;

#c_return_as_pointer_parameter("out_culture")
c_decl 
    fun CreateCulture(culture: Ref<String>): Culture 
as Rhenium_Lib_CreateCulture;
