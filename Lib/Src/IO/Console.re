namespace Rhenium.Lib;

class _Console {
    fun Write(): void {

    }
}

using Console = _Console();

c_decl 
    fun C_Write(text: Ref<String>) 
    as rhenium_lib_console_write;

c_decl
    fun C_ReadLine(): Ptr<Character>
    for rhenium_lib_console_read_line;
