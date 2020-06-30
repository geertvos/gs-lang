module HashSettest;
import System;

/*
* Example, a HashMap implementation to demonstrate the workings of GScript. 
*
*/



/**
* Constructor method for a HashSet. The number of buckets is a parameter.
*/
HashSet = (buckets)-> {

	this.bucketCount = buckets;

	/**
	* The sentinel object is used to build a linked list.
	*/
	ListElement = ()-> {
		next = undef;
		return this;
	}

	Bucket = (id)-> {
		type = "bucket";
		name = "bucket"+id;
		next = undef;
		return this;
	}

	getBucketNr = (object) -> {
		return object.ref % bucketCount;
	}
	
	findListElement = (object)-> {
		/**
		 * The HashSet is made of a string of Bucket objects, each Bucket points to the next Bucket. 
		 * The Bucket object contain a string of ListElements, the objects that store a reference to a value in the bucket and the next ListElement.
		 */
		bucketNr = getBucketNr(object);
		if(rootBucket == undef) {
			this.rootBucket = new Bucket(0);
		}
		currentBucket = this.rootBucket;
		i=0;
		while(i<bucketNr) {
			i++;
			if(currentBucket.next == undef) {
				currentBucket.next = new Bucket(i);
			} 
			currentBucket = currentBucket.next;
		}
		if(currentBucket.listElement == undef) {
			currentBucket.listElement = new ListElement();
		}
		currentListElement = currentBucket.listElement;
		while(currentListElement.next != undef && currentListElement.value != object ) {
			currentListElement = currentListElement.next;
		}
		return currentListElement;
	};

	add = (object)-> {
		currentListElement = findListElement(object);
		if(currentListElement.value != object) {
			currentListElement.next = new ListElement();
			currentListElement.next.value = object;
			return true;
		}
		return false;
	};

	contains = (object)-> {
		currentListElement = findListElement(object);
		if(currentListElement.value != object) {
			return false;
		}
		return true;
	};

	return this;
};

set = new HashSet(3);
System.print("Added: "+set.add("Geert"));
System.print("Added: "+set.add("Geert"));
//System.print("Contains: "+set.contains("Geert"));
//System.print("Contains: "+set.contains("vos"));
System.print("Done");

