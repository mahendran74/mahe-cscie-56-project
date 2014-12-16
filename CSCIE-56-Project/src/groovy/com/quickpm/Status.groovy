package com.quickpm

enum Status {
	PLANNED ('Planned'), 
	IN_PROGRESS ('In Progress'), 
	ON_HOLD ('On Hold'),
	COMPLETE ('Complete'),
	CANCELLED ('Cancelled')
	
	final String value
	
	Status(String value) { this.value = value }
	
	String toString() { value }
	String getKey() { name() }
}
