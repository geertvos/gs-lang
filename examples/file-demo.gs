module FileDemo;
import System;
import File;

file = File.open("/tmp/gscript-test.txt");
file.write("Hello from GScript!");
System.print("Exists: " + file.exists());
contents = file.read();
System.print("Contents: " + contents);
file.delete();
System.print("Deleted. Exists: " + file.exists());
