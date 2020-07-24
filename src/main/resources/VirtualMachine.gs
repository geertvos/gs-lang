module VirtualMachine;
import System;

stack = new {
	values = new[255];
	pointer = 0;
	
	push = (o) -> {
		values[pointer] = o;
		pointer++;
	}
	
	pop = () -> {
		pointer--;
		value = values[pointer];
		return value;
	}
}

instructions = new[255];

instructions[0] = 1;
instructions[1] = 2;
instructions[2] = "ADD";
instructions[3] = 7;
instructions[4] = "ADD";
instructions[5] = "PRINT";
instructions[6] = "HALT";

programcounter = 0;

while(true) {
	instruction = instructions[programcounter];
	System.print(">> "+instruction);
	programcounter++;

	if(instruction == "ADD") {
		arg1 = stack.pop();
		arg2 = stack.pop();
		result = arg1 + arg2;
		stack.push(result);
	}
	else if(instruction == "PRINT") {
		value = stack.pop();
		System.print("OUT: "+value);
	}
	else if(instruction == "HALT") {
		System.print("VM in VM exited normal");
		break;
	} else {
		stack.push(instruction);
	}
}
msg = "Virtual Machine has been executed.";
