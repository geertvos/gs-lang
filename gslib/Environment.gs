module Environment;

get = (key) -> {
    return native("gs.system.Environment", "get", key)
};

set = (key, value) -> {
    native("gs.system.Environment", "set", key, value);
};
