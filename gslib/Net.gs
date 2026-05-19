module Net;

ServerSocket = (port) -> {
    return native("gs.net.ServerSocket", "ServerSocket", port)
};

Socket = (host, port) -> {
    return native("gs.net.Socket", "Socket", host, port)
};

BufferedReader = (inputStream) -> {
    return native("gs.io.BufferedReader", "BufferedReader", inputStream)
};
