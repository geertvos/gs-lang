module File;

open = (path) -> {
    return native("gs.io.File", "File", path)
};
