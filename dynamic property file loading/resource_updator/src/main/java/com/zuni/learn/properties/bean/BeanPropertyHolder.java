package com.zuni.learn.properties.bean;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * 
 * @author Zuned Ahmed
 *
 */
public class BeanPropertyHolder {

	private final Object bean;
	private final Field field;

	public BeanPropertyHolder(Object bean, Field field) {
		this.bean = bean;
		this.field = field;
	}

	public Object getBean() {
		return this.bean;
	}

	public Field getField() {
		return this.field;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[]{this.bean, this.field});
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof BeanPropertyHolder) {
			BeanPropertyHolder that = (BeanPropertyHolder) object;
			return (this.bean ==that.bean ||this.bean!=null&&this.bean.equals(that.bean)) && 
					(this.field ==that.field ||this.field!=null&&this.field.equals(that.field));
		}
		return false;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append("bean")
			.append(this.bean)
			.append("field")
			.append(this.field)
			.toString();
	}

}
