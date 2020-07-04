module MultiThreadingTest;
import System;

thread = fork();
if(thread == true) {
	System.print("I am the new thread.");
} else {
	System.print("I am the existing thread.");
}