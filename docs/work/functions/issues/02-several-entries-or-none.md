# 02: Several entries, or none

**What to build:** a file may hold any number of uniquely named entries. The command line takes the
source path and an optional entry name; the driver picks the entry `main` calls, or stops with a message
when it cannot. Every entry is checked and emitted. See the [spec](../spec.md).

**Blocked by:** 01

**Status:** resolved

- [x] two entries with the same name: `entry 'Main' is already declared at 1:1.`
- [x] one entry runs without naming it
- [x] several entries and no name: `several entries in <path>, choose one: A, B.`
- [x] an unknown name: `no entry named 'X' in <path>.`
- [x] no entry: `nothing to run: no entry in <path>.`, after the file is fully checked, with nothing written
