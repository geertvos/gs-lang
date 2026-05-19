module WebRouting;
import System;

Router = () -> {
    this.routes = new []

    route = (path, handler) -> {
        entry = new {};
        entry.path = path;
        entry.handler = handler;
        this.routes += entry;
    }

    match = (requestPath) -> {
        for (i = 0; i < this.routes.length; i++) {
            entry = this.routes[i];
            if (entry.path == requestPath) {
                return entry.handler
            }
        }
        return undef
    }

    return this
}

readRequestLine = (inputStream) -> {
    reader = native("java.io.BufferedReader", "BufferedReader", native("java.io.InputStreamReader", "InputStreamReader", inputStream));
    line = reader.readLine();
    return line
}

extractPath = (requestLine) -> {
    path = "";
    started = false;
    done = false;
    for (i = 0; i < requestLine.length; i++) {
        ch = requestLine[i];
        if (started == false && ch == "/") {
            started = true
        }
        if (started == true && done == false) {
            if (ch == " ") {
                done = true
            } else {
                path = path + ch
            }
        }
    }
    return path
}

router = new Router();

router.route("/", (request) -> {
    return "HTTP/1.0 200 OK\n\n<h1>Home</h1><p>Welcome to GScript!</p><ul><li><a href='/hello'>Hello</a></li><li><a href='/about'>About</a></li><li><a href='/time'>Time</a></li></ul>"
});

router.route("/hello", (request) -> {
    return "HTTP/1.0 200 OK\n\n<h1>Hello!</h1><p>Greetings from GScript.</p><p><a href='/'>Back</a></p>"
});

router.route("/about", (request) -> {
    return "HTTP/1.0 200 OK\n\n<h1>About</h1><p>A simple HTTP server written in GScript with URL routing.</p><p><a href='/'>Back</a></p>"
});

notFound = (request) -> {
    return "HTTP/1.0 404 Not Found\n\n<h1>404</h1><p>Page not found.</p><p><a href='/'>Back</a></p>"
};

serversocket = native("java.net.ServerSocket", "ServerSocket", 8080);
System.print("Server listening on port 8080");

while (true) {
    socket = serversocket.accept();
    input = socket.getInputStream();
    requestLine = readRequestLine(input);
    System.print("Request: " + requestLine);

    path = extractPath(requestLine);
    System.print("Path: " + path);

    handler = router.match(path);
    if (handler == undef) {
        handler = notFound
    }
    response = handler(path);

    output = socket.getOutputStream();
    output.write(response.bytes);
    output.flush();
    output.close();
    socket.close();
}
