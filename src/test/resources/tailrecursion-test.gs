module TailRecursionTest;
import System;

System.print("Defining function");
foo = (a,b) -> {
  if (b == 1) {
    return a;
  } else {
    return foo(a*a + a, b - 1);
  }
}
System.print("Value is: "+foo(2,13));
System.print("Defining done: "+foo);
