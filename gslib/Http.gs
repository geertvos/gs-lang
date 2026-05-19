module Http;
import Net;

Headers = () -> {
    this.entries = new [];

    set = (name, value) -> {
        for (i = 0; i < this.entries.length; i++) {
            if (this.entries[i].name == name) {
                this.entries[i].value = value;
                return this
            }
        }
        entry = new {};
        entry.name = name;
        entry.value = value;
        this.entries += entry;
        return this
    };

    get = (name) -> {
        for (i = 0; i < this.entries.length; i++) {
            if (this.entries[i].name == name) {
                return this.entries[i].value
            }
        }
        return undef
    };

    serialize = () -> {
        result = "";
        for (i = 0; i < this.entries.length; i++) {
            result = result + this.entries[i].name + ": " + this.entries[i].value + "\n";
        }
        return result
    };

    return this
};

Client = (host, port) -> {
    this.host = host;
    this.port = port;
    this.defaultHeaders = new Headers();

    header = (name, value) -> {
        this.defaultHeaders.set(name, value);
        return this
    };

    get = (path, headers) -> {
        return this.request("GET", path, undef, headers)
    };

    post = (path, requestBody, headers) -> {
        return this.request("POST", path, requestBody, headers)
    };

    request = (method, path, requestBody, headers) -> {
        socket = new Socket(this.host, this.port);
        output = socket.getOutputStream();

        merged = new Headers();
        merged.set("Host", this.host);
        merged.set("Connection", "close");
        for (i = 0; i < this.defaultHeaders.entries.length; i++) {
            merged.set(this.defaultHeaders.entries[i].name, this.defaultHeaders.entries[i].value);
        }
        if (headers != undef) {
            for (i = 0; i < headers.entries.length; i++) {
                merged.set(headers.entries[i].name, headers.entries[i].value);
            }
        }
        if (requestBody != undef) {
            merged.set("Content-Length", "" + requestBody.length);
        }

        req = method + " " + path + " HTTP/1.0\n";
        req = req + merged.serialize();
        req = req + "\n";
        if (requestBody != undef) {
            req = req + requestBody + "\n";
        }
        output.write(req.bytes);
        output.flush();

        input = socket.getInputStream();
        reader = new BufferedReader(input);

        response = new {};
        statusLine = reader.readLine();
        response.statusLine = statusLine;

        response.headers = new Headers();
        done = false;
        while (done == false) {
            line = reader.readLine();
            if (line == undef || line == "") {
                done = true;
            } else {
                headerName = "";
                headerValue = "";
                inValue = false;
                for (j = 0; j < line.length; j++) {
                    ch = line[j];
                    if (inValue == false && ch == ":") {
                        inValue = true;
                    } else {
                        if (inValue == true) {
                            if (headerValue == "" && ch == " ") {
                                headerValue = "";
                            } else {
                                headerValue = headerValue + ch;
                            }
                        } else {
                            headerName = headerName + ch;
                        }
                    }
                }
                response.headers.set(headerName, headerValue);
            }
        }

        respBody = "";
        done = false;
        while (done == false) {
            line = reader.readLine();
            if (line == undef) {
                done = true;
            } else {
                if (respBody == "") {
                    respBody = line;
                } else {
                    respBody = respBody + "\n" + line;
                }
            }
        }
        response.body = respBody;
        socket.close();
        return response
    };

    return this
};

Server = (port) -> {
    this.socket = new ServerSocket(port);
    this.handler = undef;

    onRequest = (fn) -> {
        this.handler = fn;
    };

    start = () -> {
        while (true) {
            client = this.socket.accept();
            input = client.getInputStream();
            reader = new BufferedReader(input);

            requestLine = reader.readLine();
            if (requestLine != undef) {
                request = new {};
                request.line = requestLine;
                request.method = "";
                request.path = "";
                request.body = "";

                state = 0;
                for (i = 0; i < requestLine.length; i++) {
                    ch = requestLine[i];
                    if (state == 0) {
                        if (ch == " ") {
                            state = 1;
                        } else {
                            request.method = request.method + ch;
                        }
                    } else {
                        if (state == 1) {
                            if (ch == " ") {
                                state = 2;
                            } else {
                                request.path = request.path + ch;
                            }
                        }
                    }
                }

                request.headers = new Headers();
                done = false;
                while (done == false) {
                    line = reader.readLine();
                    if (line == undef || line == "") {
                        done = true;
                    } else {
                        headerName = "";
                        headerValue = "";
                        inValue = false;
                        for (j = 0; j < line.length; j++) {
                            ch = line[j];
                            if (inValue == false && ch == ":") {
                                inValue = true;
                            } else {
                                if (inValue == true) {
                                    if (headerValue == "" && ch == " ") {
                                        headerValue = "";
                                    } else {
                                        headerValue = headerValue + ch;
                                    }
                                } else {
                                    headerName = headerName + ch;
                                }
                            }
                        }
                        request.headers.set(headerName, headerValue);
                    }
                }

                contentLength = request.headers.get("Content-Length");
                if (contentLength != undef && contentLength != "" && contentLength != "0") {
                    line = reader.readLine();
                    if (line != undef) {
                        request.body = line;
                    }
                }

                response = this.handler(request);
                output = client.getOutputStream();

                responseHeaders = new Headers();
                responseHeaders.set("Connection", "close");
                if (response.headers != undef) {
                    for (i = 0; i < response.headers.entries.length; i++) {
                        responseHeaders.set(response.headers.entries[i].name, response.headers.entries[i].value);
                    }
                }
                if (responseHeaders.get("Content-Type") == undef) {
                    responseHeaders.set("Content-Type", "text/html");
                }

                reply = "HTTP/1.0 " + response.status + "\n";
                reply = reply + responseHeaders.serialize();
                reply = reply + "\n";
                reply = reply + response.body;
                output.write(reply.bytes);
                output.flush();
                output.close();
            }
            client.close();
        }
    };

    return this
};
