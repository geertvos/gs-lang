module Time;

now = () -> {
    return native("gs.system.Time", "now")
};

sleep = (ms) -> {
    native("gs.system.Time", "sleep", ms);
};
