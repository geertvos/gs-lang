module HttpServerExample;
import System;
import Http;

server = new Server(8080);

server.onRequest((request) -> {
    print(request.method + " " + request.path);
    userAgent = request.headers.get("User-Agent");
    if (userAgent != undef) {
        print("  User-Agent: " + userAgent);
    }

    response = new {};

    if (request.path == "/") {
        response.status = "200 OK";
        response.body = "<h1>Home</h1><p><a href='/hello'>Hello</a></p>";
    } else {
        if (request.path == "/hello") {
            response.status = "200 OK";
            response.body = "<h1>Hello!</h1><p><a href='/'>Back</a></p>";
        } else {
            if (request.path == "/echo") {
                response.status = "200 OK";
                response.body = request.body;
            } else {
                response.status = "404 Not Found";
                response.body = "<h1>404</h1><p>Not found.</p>";
            }
        }
    }

    return response
});

print("Server listening on port 8080");
server.start();
