module NativeObject;

// Hacks to work around missing features:
// - cannot call constructors yet
// - cannot create linefeeds in strings yet
// - the .bytes reference on the string is also a bit hacky

//serversocket = native("net.geertvos.gvm.parser.GVMIntegrationTest", "create");
linefeed = native("net.geertvos.gvm.parser.GVMIntegrationTest", "getLineFeed");
serversocket = native("java.net.ServerSocket","ServerSocket", 8081);

responseheader = "HTTP/1.0 200 OK";
int counter = 0;
while(true) {
	socket = serversocket.accept();
	output = socket.getOutputStream();
	message = responseheader+linefeed+linefeed+"<h1>Hello "+counter+"</h1><h6>Served from the GVM"+linefeed+linefeed;
	bytes = message.bytes;
    output.write(bytes);
	output.flush();
	output.close();
	socket.close();
	counter++;
}
native("net.geertvos.gvm.runtime.Runtime", "print", "We are done.");

