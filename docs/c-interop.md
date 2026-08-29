# C interop

C is bound with `c_decl`, which always links a Rhenium name to a C symbol using `as`:

```
c_decl type I32 as int32_t;

c_decl fun C_Write(text: Ref<String>) as rhenium_lib_console_write;

c_decl const C_EOL: Ref<String> as RHENIUM_LIB_EOL;

c_decl class String(
    #c_field("data")
    using take data: Ptr<Character>,

    #c_field("length")
    length: I32
) {
    property Length(): I32 {
        return length;
    }
}
as Rhenium_Lib_String;
```

## Mapping attributes

| Attribute | Effect |
| --- | --- |
| `#c_field("name")` | maps a parameter or property onto a named C struct field |
| `#c_return_as_pointer_parameter("out_target")` | the C function returns through an out-pointer instead of a return value |

## Headers

Hand-written `.h` files sit beside the `.re` sources they support, and the project opts in with
`"AllowC": true` in `Project.json`.
