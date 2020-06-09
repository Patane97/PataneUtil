package com.Patane.util.YAML;

import com.Patane.util.annotations.ClassDescriber;

public abstract class ClassDescribable {
	
	private ClassDescriber classDescriber;
	
	public <A extends ClassDescriber> ClassDescriber getClassInfo() {
		if(classDescriber == null) {
			classDescriber = this.getClass().getAnnotation(ClassDescriber.class);
		}
		return classDescriber;
	}
	
	
	public String className() {
		return getClassInfo().name();
	}
	public String classDesc() {
		return getClassInfo().desc();
	}
}
