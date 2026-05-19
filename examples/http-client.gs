module HttpClient;
import System;
import Http;

client = new Client("localhost", 8080);
client.header("User-Agent", "GScript/1.0");
client.header("Accept", "text/html");

response = client.get("/hello", undef);
print("Status: " + response.statusLine);
print("Content-Type: " + response.headers.get("Content-Type"));
print("Body: " + response.body);

print("");
print("--- POST example ---");
customHeaders = new Headers();
customHeaders.set("X-Custom", "hello");
response = client.post("/echo", "test data", customHeaders);
print("Status: " + response.statusLine);
print("Body: " + response.body);
