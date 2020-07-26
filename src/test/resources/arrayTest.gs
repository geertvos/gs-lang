module ArrayTest;
import System;


names = new ["Johny", "Calvin", "Clara"];
System.print(names[0]);
System.print(names[1]);
System.print(names[2]);
names[101] = "Geert";
number = 101;
System.print(names[number]);
System.print(names[101]);
System.print("Array length: "+names.length);

object = new {};
object.name = "MyObject";
System.print(object["name"]);

object2 = new [ "name" => "Catlin", "age" => 38];
System.print(object2.name);
object2.description = "An object with a name, age and a description.";

//Reflection
fields = object2.fields;
for(x=0;x<fields.length;x++){
	System.print(fields[x]);
}

test = new[];
test[1] = new[];
test[1][1] = "geert2";
System.print(test[1][1]);