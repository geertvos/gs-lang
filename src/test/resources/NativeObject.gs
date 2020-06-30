module NativeObject;

// Hacks to work around missing features:
// - the .bytes reference on the string is also a bit hacky

serversocket = native("java.net.ServerSocket","ServerSocket", 8081);

responseheader = "HTTP/1.0 200 OK";
int counter = 0;
while(true) {
	socket = serversocket.accept();
	output = socket.getOutputStream();
	message = responseheader+"\n\n"+"<h1>Hello "+counter+"</h1><h6>Served from the GVM"+"\n\n";
	bytes = message.bytes;
    output.write(bytes);
	output.flush();
	output.close();
	socket.close();
	counter++;
}
native("net.geertvos.gvm.runtime.Runtime", "print", "We are done.");

