module System;

print = (text) -> {
    native("gs.system.Runtime", "print", text);
};
