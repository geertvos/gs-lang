module TimeDemo;
import System;
import Time;

start = Time.now();
Time.sleep(100);
elapsed = Time.now() - start;
System.print("Elapsed: " + elapsed + "ms");
