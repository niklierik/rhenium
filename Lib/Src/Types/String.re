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

    fun Equals(other: Ref<String>): Boolean {
        return CompareRaw(this, other, Culture.DefaultCulture);
    }

    fun Equals(other: Ref<String>, culture: Ref<Culture>): Boolean {
        return CompareRaw(this, other, culture);
    }

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
            culture: Ref<Culture>
        ): I32 
    as Rhenium_Lib_String_Compare_Raw;
}
as Rhenium_Lib_String;
