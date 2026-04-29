c_decl class String(
    #c_field("data")
    using take data: Ptr<Character>,

    #c_field("length")
    length: I32
) {
    init {}

    property Length(): I32 {
        return length;
    }

    property Data(): Ptr<Character> {
        return data;
    }

    c_decl
        fun Equals(other: Ref<String>): Boolean 
    as Rhenium_Lib_String_Equals;
    
    c_decl 
        fun Compare(other: Ref<String>): I32 
    as Rhenium_Lib_String_Compare;


    #c_return_as_pointer_parameter("out_target")
    c_decl
        static fun MakeDirect(
            data: Ptr<Character>
        ): grant String 
    as Rhenium_Lib_String_MakeDirect;
    
    #c_return_as_pointer_parameter("out_target")
    c_decl 
        static fun MakeCopy(
            data: Ptr<Character>
        ): grant String 
    as Rhenium_Lib_String_MakeCopy;

    #c_return_as_pointer_parameter("out_target")
    c_decl 
        static fun MakeCopyOfSlice(
            data: Ptr<Character>, 
            start:I32,
            length: I32
        ): grant String 
    as Rhenium_Lib_String_MakeCopyOfSlice;
    
    c_decl 
        static fun CompareRaw(
            self: Ptr<Character>, 
            other: Ptr<Character>, 
            collator: Ptr<StringCollator>
        ): I32 
    as Rhenium_Lib_String_Compare_Raw;
}
as Rhenium_Lib_String;

c_decl type StringCollator as UCollator;

c_decl property DefaultCollator(): Ref<StringCollator> as Rhenium_Lib_DefaultCollator; 
c_decl fun DestroyDefaultCollator() as Rhenium_Lib_DestroyDefaultCollator; 

delete {
    DestroyDefaultCollator();
}

c_decl type StringCaseMap as UCaseMap;

c_decl property DefaultCaseMap(): Ref<StringCaseMap> as Rhenium_Lib_DefaultCaseMap; 
c_decl fun DestroyDefaultCaseMap() as Rhenium_Lib_DestroyDefaultCaseMap; 

delete {
    DestroyDefaultCollator();
    DestroyDefaultCaseMap();
}