# GScript Language Reference

GScript is a dynamically typed, object-oriented scripting language that runs on the GVM (Geert Virtual Machine). It features first-class functions, prototype-based objects, a native module plugin system, and concurrency via `fork()`. The runtime is available in both Rust and Java.

## Program Structure

Every program starts with a `module` declaration, followed by optional imports, then statements.

```
module MyProgram;
import System;

System.print("Hello, world!");
```

Modules map to `.gs` files. `import System` loads `System.gs` from the same directory.

## Comments

```
// line comment
/* block comment */
```

## Semicolons

Semicolons are recommended after assignment statements (`=`, `+=`, `-=`) when the next line starts with an expression. They are required as separators inside `for` loops. Inside braces (`if`, `while`, functions) they are generally optional.

## Types

| Type      | Literals / Construction              | Operations                              |
|-----------|--------------------------------------|-----------------------------------------|
| Number    | `0`, `42`, `100`                     | `+ - * / %` `== != < > <= >=`           |
| String    | `"hello"`                            | `+` (concat) `== !=` `[i]` `.length` `.lowercase` `.bytes` `.ref` |
| Boolean   | `true`, `false`                      | `! && \|\|` `== !=`                     |
| Object    | `new {}`, `new Ctor(args)`           | field get/set, `==` `.fields` `.ref`    |
| Array     | `new [1, 2, 3]`, `new []`           | index get/set, `+=` (append), `.length` |
| Map       | `new ["k1" => "v1", "k2" => "v2"]`  | field get/set (same as Object)          |
| Function  | `(args) -> { body }`                 | invoke                                  |
| Undefined | `undef`                              | equality check                          |

Numbers are integers only. Logical operators (`!`, `&&`, `||`) only work on Booleans. Strings support ASCII letters, digits, and common punctuation. When a Number or Boolean appears in string concatenation, it coerces to a String automatically.

## Variables

Assignment creates a variable. No declaration keyword.

```
x = 10
name = "Geert"
flag = true
nothing = undef
```

## Operators

Precedence from highest to lowest:

| Precedence | Operators             | Description           |
|------------|-----------------------|-----------------------|
| 1          | `++ --`               | Postfix inc/dec       |
| 2          | `!`                   | Logical NOT           |
| 3          | `* / %`               | Multiplicative        |
| 4          | `+ -`                 | Additive / concat     |
| 5          | `< > <= >=`           | Relational            |
| 6          | `== !=`               | Equality              |
| 7          | `&&`                  | Logical AND           |
| 8          | `\|\|`                | Logical OR            |
| 9          | `? :`                 | Ternary conditional   |
| 10         | `= += -= *= /=`       | Assignment            |

```
result = (a > b) ? a : b
x += 10
count++
```

## Functions

Functions are values defined with arrow syntax. Parameters go in parentheses before `->`, the body in braces after.

```
add = (a, b) -> {
    return a + b
}
result = add(2, 3)
```

Zero-parameter function:

```
greet = () -> {
    return "hello"
}
```

Functions without an explicit `return` return `undef`.

Functions are first-class and can be passed as arguments or stored in fields:

```
apply = (fn, value) -> {
    return fn(value)
}
double = (x) -> { return x * 2 }
apply(double, 5)
```

Tail recursion is optimized by the compiler:

```
factorial = (n, acc) -> {
    if (n <= 1) {
        return acc
    }
    return factorial(n - 1, acc * n)
}
```

## Objects

Objects are created with `new {}` (empty) or `new` with a constructor function.

### Object literals

```
obj = new {
    name = "Alice"
    greet = () -> {
        System.print("Hi, I am " + this.name)
    }
}
obj.greet()
```

Object literals compile as immediately-invoked anonymous functions. Variables from an enclosing function scope are not accessible inside `new {}`. To create objects with values from an outer scope, create an empty object and set fields explicitly:

```
make = (name, age) -> {
    person = new {};
    person.name = name;
    person.age = age;
    return person
}
```

### Constructor functions

A constructor is a regular function that sets fields on `this` and returns `this`:

```
Person = (name, age) -> {
    this.name = name
    this.age = age

    greet = () -> {
        return "Hi, I am " + this.name
    }

    return this
}

alice = new Person("Alice", 30)
System.print(alice.greet())
```

### Field access

```
obj.name              // dot notation
obj["name"]           // bracket notation (dynamic key)
```

### Chaining

```
obj.child.grandchild.value
obj.getItems()[0]
```

### Built-in object properties

```
obj.fields            // returns Array of field name strings
obj.ref               // returns internal reference number
```

### Overriding methods

```
alice.greet = () -> { return "override" }
```

## Arrays

```
arr = new ["a", "b", "c"]
arr[0]                // "a"
arr.length            // 3
arr[10] = "z"         // auto-expands, gaps are undef
```

Empty array:

```
arr = new []
```

### Appending elements

The `+=` operator appends an element to the end of an array:

```
arr = new [];
arr += "first";
arr += "second";
arr += "third";
arr.length            // 3
arr[0]                // "first"
```

Nested arrays:

```
matrix = new []
matrix[0] = new [1, 2, 3]
matrix[0][1]          // 2
```

## Maps

Maps create Object instances with pre-set key-value pairs:

```
config = new ["host" => "localhost", "port" => 8080]
config.host           // "localhost"
config.port           // 8080
```

## Strings

String literals are enclosed in double quotes.

```
greeting = "Hello, world!"
```

### Built-in string properties

```
name = "GEert"
name.lowercase        // "geert"
name.length           // 5
name[0]               // "G" (character at index as string)
name[4]               // "t"
name.bytes            // byte array (for I/O)
name.ref              // internal reference number
```

### Concatenation

The `+` operator concatenates strings. Numbers and booleans auto-coerce:

```
"value: " + 42        // "value: 42"
"flag: " + true       // "flag: true"
```

## Control Flow

### if / else

```
if (x > 0) {
    System.print("positive")
} else {
    System.print("non-positive")
}
```

Single-statement form:

```
if (done) System.print("done")
```

### while

```
i = 0
while (i < 10) {
    System.print("" + i)
    i++
}
```

### for

```
for (i = 0; i < 10; i++) {
    System.print("" + i)
}
```

### break / continue

```
while (true) {
    if (done) break
    if (skip) continue
    process()
}
```

### Ternary

```
label = (count > 0) ? "has items" : "empty"
```

## Exception Handling

```
try {
    risky()
} catch (e) {
    System.print("Error: " + e.message + " at line " + e.line)
}
```

### throw

Throw a string (becomes the exception message):

```
throw "something went wrong"
```

### Exception object properties

| Property   | Type   | Description                |
|------------|--------|----------------------------|
| `message`  | String | The error/throw message    |
| `line`     | Number | Source line number          |
| `location` | String | Source location identifier  |

## Native Module Plugin System

GScript provides native functionality through a plugin system. Native modules are implemented in the host language (Rust or Java) and registered with the VM at startup. GScript code accesses them through standard library `.gs` files that wrap the `native()` builtin.

User code never calls `native()` directly. Instead, it imports standard library modules (`System`, `Net`, `File`, `Time`, `Environment`, `Http`) that provide clean GScript APIs.

### Architecture

The plugin system consists of three layers:

1. **NativeModule** -- a host-language type that provides a constructor and/or static methods for a single class (e.g. `gs.net.ServerSocket`)
2. **NativeInstance** -- a host-language object returned by a constructor, exposing instance methods (e.g. `accept()`, `close()`)
3. **NativeRegistry** -- a registry that maps class names to modules and dispatches calls

All native modules use the `gs.*` namespace. The registry is created at VM startup and populated by a `register_all()` function that registers every built-in module.

### Writing a Native Module

A native module implements two traits:

**NativeModule** (one per class):

| Method             | Description                                      |
|--------------------|--------------------------------------------------|
| `class_name()`     | Returns the fully qualified name (e.g. `gs.io.File`) |
| `constructor(args)` | Called when method name matches the simple class name |
| `call_static(method, args)` | Called for all other method names          |
| `static_methods()` | Returns descriptors for static methods            |

**NativeInstance** (one per created object):

| Method                | Description                                   |
|-----------------------|-----------------------------------------------|
| `type_name()`         | Returns the type name for debugging           |
| `instance_methods()`  | Returns descriptors for available methods     |
| `call_method(method, args)` | Dispatches a method call on this instance |
| `destroy()`           | Called when the object is garbage collected    |
| `clone_instance()`    | Creates a copy (required by the VM's borrow model) |

### NativeValue

Plugins communicate with the VM through `NativeValue`, never seeing the internal `Value` type:

| Variant      | Description                                |
|--------------|--------------------------------------------|
| `Undefined`  | No value / null                            |
| `Number(i32)` | Integer value                             |
| `Boolean(bool)` | True or false                           |
| `String(String)` | Text value                             |
| `Instance(NativeInstance)` | A new native object to place on the heap |
| `Bytes(Vec<u8>)` | Raw byte data for I/O                 |

### Error Handling

Native modules return `NativeResult` (`Result<NativeValue, NativeError>`). Errors propagate as GScript exceptions, catchable with `try`/`catch`:

```
try {
    socket = new Socket("localhost", 9999)
} catch (e) {
    print("Connection failed: " + e.message)
}
```

Unhandled native errors print the exception message with the module and line number:

```
Unhandled exception 'Failed to connect to localhost:9999: Connection refused' at Net:8
```

### Dispatch Rules

When `native(className, methodName, args...)` is called:

1. The registry looks up the module by `className`
2. If `methodName` equals the simple class name (last segment after `.`), the module's `constructor()` is called
3. Otherwise, `call_static()` is called

Example: `native("gs.net.ServerSocket", "ServerSocket", 8080)` -- "ServerSocket" matches the simple name, so `constructor([Number(8080)])` is called.

### GScript Standard Library Wrapper

Each native module has a corresponding `.gs` wrapper in `gslib/`. These are thin factory functions:

```
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
```

When a module is imported, its top-level names are exported into the importing scope. This means both `Net.ServerSocket(8080)` and `new ServerSocket(8080)` work after `import Net`.

### Registered Native Modules

| Class Name            | Type        | Description                    |
|-----------------------|-------------|--------------------------------|
| `gs.system.Runtime`   | Static only | Console output                 |
| `gs.system.Time`      | Static only | Clock and sleep                |
| `gs.system.Environment` | Static only | Environment variables        |
| `gs.net.ServerSocket` | Constructor | TCP server socket              |
| `gs.net.Socket`       | Constructor | TCP client socket              |
| `gs.io.BufferedReader` | Constructor | Line-oriented stream reader   |
| `gs.io.File`          | Constructor | File system operations         |

### Instance Methods by Type

**ServerSocket** (from `gs.net.ServerSocket`):

| Method     | Args | Returns        | Description                |
|------------|------|----------------|----------------------------|
| `accept()` | 0    | TcpStream      | Waits for a client connection |
| `close()`  | 0    | Undefined      | Closes the server socket   |

**TcpStream** (returned by `ServerSocket.accept()` or `Socket` constructor):

| Method              | Args | Returns       | Description              |
|---------------------|------|---------------|--------------------------|
| `getInputStream()`  | 0    | InputStream   | Returns the input stream |
| `getOutputStream()` | 0    | OutputStream  | Returns the output stream |
| `close()`           | 0    | Undefined     | Closes the connection    |

**OutputStream** (returned by `getOutputStream()`):

| Method    | Args | Returns   | Description                    |
|-----------|------|-----------|--------------------------------|
| `write(bytes)` | 1 | Undefined | Writes bytes to the stream   |
| `flush()`      | 0 | Undefined | Flushes buffered data        |
| `close()`      | 0 | Undefined | Closes the stream            |

**BufferedReader** (from `gs.io.BufferedReader`):

| Method       | Args | Returns          | Description                     |
|--------------|------|------------------|---------------------------------|
| `readLine()` | 0    | String or `undef` | Reads one line, `undef` on EOF |

**File** (from `gs.io.File`):

| Method      | Args | Returns          | Description                    |
|-------------|------|------------------|--------------------------------|
| `read()`    | 0    | String           | Reads the entire file          |
| `write(data)` | 1 | Undefined        | Overwrites the file            |
| `append(data)` | 1 | Undefined       | Appends to the file            |
| `exists()`  | 0    | Boolean          | Checks if the file exists      |
| `delete()`  | 0    | Boolean          | Deletes the file               |

### Cross-Runtime Compatibility

The same `.gs` standard library files work on both the Rust and Java runtimes. Native modules use the `gs.*` namespace in both. Compiled `.gsc` bytecode files are portable between runtimes.

## Concurrency

`fork()` is a built-in keyword (not a regular function) that splits execution into two threads. It must be written exactly as `fork()` -- it cannot be assigned to a variable or passed as a value. Returns `true` in the new thread, `false` in the parent.

```
child = fork()
if (child == true) {
    System.print("child thread")
} else {
    System.print("parent thread")
}
```

Both threads share the same heap and can access the same objects.

## Standard Library

GScript ships with a set of standard library modules in the `gslib/` directory. Each module wraps native functionality in clean GScript APIs. Import them with `import ModuleName`.

### System

Console output.

```
import System;

print("Hello!")              // exported name
System.print("Hello!")       // qualified name
```

| Function      | Description              |
|---------------|--------------------------|
| `print(text)` | Prints text to stdout    |

### Net

TCP networking and stream I/O.

```
import Net;

server = new ServerSocket(8080)
client = server.accept()
input = client.getInputStream()
reader = new BufferedReader(input)
line = reader.readLine()
```

| Function                    | Returns        | Description                    |
|-----------------------------|----------------|--------------------------------|
| `ServerSocket(port)`        | ServerSocket   | Binds a TCP server on `port`   |
| `Socket(host, port)`        | TcpStream      | Connects to a TCP server       |
| `BufferedReader(inputStream)` | BufferedReader | Wraps a stream for line reading |

### File

File system operations.

```
import File;

f = File.open("/tmp/test.txt")
f.write("hello")
contents = f.read()
f.delete()
```

| Function      | Returns | Description                |
|---------------|---------|----------------------------|
| `open(path)`  | File    | Opens a file reference     |

See [Instance Methods by Type](#instance-methods-by-type) for methods on the returned File object.

### Time

Clock and sleep.

```
import Time;

start = Time.now()
Time.sleep(1000)
elapsed = Time.now() - start
```

| Function     | Returns | Description                     |
|--------------|---------|---------------------------------|
| `now()`      | Number  | Current time in epoch millis    |
| `sleep(ms)`  | --      | Pauses execution for `ms` millis |

### Environment

Environment variables.

```
import Environment;

home = Environment.get("HOME")
Environment.set("MY_VAR", "value")
```

| Function           | Returns          | Description                   |
|--------------------|------------------|-------------------------------|
| `get(key)`         | String or `undef` | Gets an environment variable |
| `set(key, value)`  | --               | Sets an environment variable  |

### Http

HTTP/1.0 client and server, built on top of `Net`. Provides `Headers`, `Client`, and `Server` constructors.

#### Headers

A key-value collection for HTTP headers.

```
import Http;

headers = new Headers()
headers.set("Content-Type", "application/json")
headers.set("Accept", "text/html")
value = headers.get("Content-Type")    // "application/json"
```

| Method              | Returns          | Description                          |
|---------------------|------------------|--------------------------------------|
| `set(name, value)`  | Headers (this)   | Sets a header, replacing if exists   |
| `get(name)`         | String or `undef` | Gets a header value by name         |
| `serialize()`       | String           | Formats as HTTP header lines         |

#### Client

An HTTP client that connects to a host and sends requests.

```
client = new Client("localhost", 8080)
client.header("User-Agent", "GScript/1.0")

response = client.get("/hello", undef)
print(response.statusLine)
print(response.body)

postHeaders = new Headers()
postHeaders.set("Content-Type", "text/plain")
response = client.post("/echo", "request body", postHeaders)
```

| Method                              | Returns  | Description                        |
|-------------------------------------|----------|------------------------------------|
| `header(name, value)`               | Client   | Sets a default header for all requests |
| `get(path, headers)`                | Response | Sends a GET request                |
| `post(path, requestBody, headers)`  | Response | Sends a POST request               |
| `request(method, path, body, headers)` | Response | Sends a custom request          |

Pass `undef` for `headers` or `requestBody` when not needed.

**Response object fields:**

| Field        | Type    | Description                     |
|--------------|---------|---------------------------------|
| `statusLine` | String  | Full status line (e.g. `HTTP/1.0 200 OK`) |
| `headers`    | Headers | Response headers                |
| `body`       | String  | Response body                   |

#### Server

An HTTP server that listens for incoming requests and dispatches them to a handler.

```
server = new Server(8080)

server.onRequest((request) -> {
    response = new {}
    response.status = "200 OK"
    response.body = "<h1>Hello</h1>"
    return response
})

server.start()
```

| Method             | Description                                    |
|--------------------|------------------------------------------------|
| `onRequest(fn)`    | Sets the request handler function              |
| `start()`          | Starts the server loop (blocks)                |

**Request object fields:**

| Field     | Type    | Description                           |
|-----------|---------|---------------------------------------|
| `method`  | String  | HTTP method (`GET`, `POST`, etc.)     |
| `path`    | String  | Request path (e.g. `/hello`)          |
| `headers` | Headers | Request headers                       |
| `body`    | String  | Request body (first line, for POST)   |
| `line`    | String  | Full request line                     |

**Response object fields (returned by handler):**

| Field     | Type    | Required | Description                      |
|-----------|---------|----------|----------------------------------|
| `status`  | String  | Yes      | Status code and reason (e.g. `200 OK`) |
| `body`    | String  | Yes      | Response body                    |
| `headers` | Headers | No       | Custom response headers          |

The server automatically adds `Connection: close` and `Content-Type: text/html` (if not set) to response headers.

## Complete Examples

### Hello World

```
module Hello;
import System;

System.print("Hello, world!")
```

### FizzBuzz

```
module FizzBuzz;
import System;

for (i = 1; i <= 100; i++) {
    if (i % 15 == 0) {
        System.print("FizzBuzz")
    } else {
        if (i % 3 == 0) {
            System.print("Fizz")
        } else {
            if (i % 5 == 0) {
                System.print("Buzz")
            } else {
                System.print("" + i)
            }
        }
    }
}
```

### Linked List

```
module LinkedList;
import System;

Node = (value) -> {
    this.value = value
    this.next = undef
    return this
}

List = () -> {
    this.head = undef

    add = (value) -> {
        node = new Node(value)
        if (this.head == undef) {
            this.head = node
        } else {
            current = this.head
            while (current.next != undef) {
                current = current.next
            }
            current.next = node
        }
    }

    print = () -> {
        current = this.head
        while (current != undef) {
            System.print("" + current.value)
            current = current.next
        }
    }

    return this
}

list = new List()
list.add(1)
list.add(2)
list.add(3)
list.print()
```

### HTTP Server

```
module WebServer;
import System;
import Http;

server = new Server(8080)

server.onRequest((request) -> {
    print(request.method + " " + request.path)

    response = new {}
    if (request.path == "/") {
        response.status = "200 OK"
        response.body = "<h1>Home</h1><p><a href='/hello'>Hello</a></p>"
    } else {
        if (request.path == "/hello") {
            response.status = "200 OK"
            response.body = "<h1>Hello!</h1>"
        } else {
            response.status = "404 Not Found"
            response.body = "<h1>404</h1>"
        }
    }
    return response
})

print("Listening on port 8080")
server.start()
```

### HTTP Client

```
module HttpClientDemo;
import System;
import Http;

client = new Client("localhost", 8080)
client.header("User-Agent", "GScript/1.0")

response = client.get("/hello", undef)
print("Status: " + response.statusLine)
print("Body: " + response.body)
```

### File I/O

```
module FileDemo;
import System;
import File;

f = File.open("/tmp/test.txt")
f.write("Hello from GScript!")
print("Exists: " + f.exists())
print("Contents: " + f.read())
f.delete()
```

### Timing

```
module TimeDemo;
import System;
import Time;

start = Time.now()
Time.sleep(1000)
elapsed = Time.now() - start
print("Elapsed: " + elapsed + "ms")
```

### HashSet (data structures)

```
module HashSetDemo;
import System;

HashSet = (buckets) -> {
    this.bucketCount = buckets

    ListElement = () -> {
        next = undef
        return this
    }

    Bucket = (id) -> {
        type = "bucket"
        name = "bucket" + id
        next = undef
        return this
    }

    getBucketNr = (object) -> {
        return object.ref % bucketCount
    }

    findListElement = (object) -> {
        bucketNr = getBucketNr(object)
        if (rootBucket == undef) {
            this.rootBucket = new Bucket(0)
        }
        currentBucket = this.rootBucket
        i = 0
        while (i < bucketNr) {
            i++
            if (currentBucket.next == undef) {
                currentBucket.next = new Bucket(i)
            }
            currentBucket = currentBucket.next
        }
        if (currentBucket.listElement == undef) {
            currentBucket.listElement = new ListElement()
        }
        currentListElement = currentBucket.listElement
        while (currentListElement.next != undef && currentListElement.value != object) {
            currentListElement = currentListElement.next
        }
        return currentListElement
    }

    add = (object) -> {
        currentListElement = findListElement(object)
        if (currentListElement.value != object) {
            currentListElement.next = new ListElement()
            currentListElement.next.value = object
            return true
        }
        return false
    }

    contains = (object) -> {
        currentListElement = findListElement(object)
        if (currentListElement.value != object) {
            return false
        }
        return true
    }

    return this
}

set = new HashSet(10)
set.add("Alice")
set.add("Bob")
set.add("Alice")
System.print("Contains Alice: " + set.contains("Alice"))
System.print("Contains Eve: " + set.contains("Eve"))
```

### Callbacks

```
module CallbackDemo;
import System;

EventEmitter = () -> {
    this.listeners = new []

    on = (callback) -> {
        this.listeners += callback
    }

    emit = (data) -> {
        for (i = 0; i < this.listeners.length; i++) {
            this.listeners[i](data)
        }
    }

    return this
}

emitter = new EventEmitter();
emitter.on((msg) -> { System.print("Listener 1: " + msg) });
emitter.on((msg) -> { System.print("Listener 2: " + msg) });
emitter.emit("hello")
```

## Compiling and Running

### Rust runtime

```bash
# Compile and run directly
gs-lang program.gs

# Compile to bytecode
gs-lang --compile program.gs -o program.gsc

# Run from bytecode
gs-lang --run program.gsc
```

### Java runtime

```bash
# Compile and run directly
java --add-opens java.base/java.lang=ALL-UNNAMED \
  -cp gs-lang.jar:dependencies/* \
  net.geertvos.gvm.compiler.GScript program.gs

# Compile to bytecode
java --add-opens java.base/java.lang=ALL-UNNAMED \
  -cp gs-lang.jar:dependencies/* \
  net.geertvos.gvm.compiler.GScript --compile program.gs -o program.gsc

# Run from bytecode
java --add-opens java.base/java.lang=ALL-UNNAMED \
  -cp gs-lang.jar:dependencies/* \
  net.geertvos.gvm.compiler.GScript --run program.gsc
```

Compiled `.gsc` files are portable between the Rust and Java runtimes.

## Reserved Keywords

The following identifiers are reserved and cannot be used as variable names:

```
module import if else while for return break continue
try catch throw new this native fork true false undef global
```

## Grammar (EBNF)

```
Program          = Module Statements
Module           = "module" Identifier [";" ] Imports
Imports          = { Import }
Import           = "import" Identifier [";"]
Statements       = Statement { Statement }

Statement        = ReturnValue | Return | For | While
                 | IfElse | If | TryCatch | ExprStmt
                 | Break | Continue | Throw | Block

Block            = "{" [ Statements ] "}"
For              = "for" "(" Expr ";" Expr ";" Expr ")" Statement
While            = "while" "(" Expr ")" Statement
If               = "if" "(" Expr ")" Statement
IfElse           = "if" "(" Expr ")" Statement "else" Statement
TryCatch         = "try" Statement "catch" "(" Ident ")" Statement
ReturnValue      = "return" Expr
Return           = "return"
Throw            = "throw" Expr
Break            = "break"
Continue         = "continue"
ExprStmt         = Expr

Expr             = Conditional { AssignOp Conditional }
AssignOp         = "=" | "+=" | "-=" | "*=" | "/="
Conditional      = OrExpr { "?" Expr ":" OrExpr }
OrExpr           = AndExpr { "||" AndExpr }
AndExpr          = EqExpr { "&&" EqExpr }
EqExpr           = RelExpr { ("==" | "!=") RelExpr }
RelExpr          = AddExpr { ("<=" | ">=" | "<" | ">") AddExpr }
AddExpr          = MulExpr { ("+" | "-") MulExpr }
MulExpr          = UnaryExpr { ("*" | "/" | "%") UnaryExpr }
UnaryExpr        = "!" UnaryExpr
                 | Ref PostfixRef { PostfixRef }
                 | Ref PostfixOp
                 | Atom

PostfixRef       = "[" Expr "]" | "(" [ ArgList ] ")"
PostfixOp        = "++" | "--"

Atom             = ObjectDef | FuncDef | CtorCall | NativeCall
                 | Assignment | "fork()" | Number | Boolean
                 | String | ArrayDef | MapDef | "undef" | Ref

Ref              = ("this" | Variable) { "." Ref }
ObjectDef        = "new" "{" [ Statements ] "}"
ArrayDef         = "new" "[" [ ArgList ] "]"
MapDef           = "new" "[" KVPair { "," KVPair } "]"
KVPair           = Expr "=>" Expr
CtorCall         = "new" Variable "(" [ ArgList ] ")" { "(" [ ArgList ] ")" }
FuncDef          = "(" [ Ident { "," Ident } ] ")" "->" "{" [ Statements ] "}"
NativeCall       = "native" "(" [ ArgList ] ")"   (* internal -- used by gslib only *)
Assignment       = Ref "=" Expr
ArgList          = Expr { "," Expr }

Identifier       = Letter { Letter | Digit }
Letter           = "a".."z" | "A".."Z" | "_" | "$"
Digit            = "0".."9"
Number           = Digit { Digit }
String           = '"' { char } '"'
Boolean          = "true" | "false"
```
