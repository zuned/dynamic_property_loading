package com.zuni.learn.properties.event;

import java.util.Arrays;

/**
 * 
 * @author Zuned Ahmed
 *
 */
public class PropertyModifiedEvent {

	private final String propertyName;
	private final Object oldValue;
	private final Object newValue;

	public PropertyModifiedEvent(final String propertyName, final Object oldValue, final Object newValue) {
		this.propertyName = propertyName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public String getPropertyName() {
		return this.propertyName;
	}

	public Object getOldValue() {
		return this.oldValue;
	}

	public Object getNewValue() {
		return this.newValue;
	}
	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[]{this.propertyName,this.oldValue, this.newValue});
	}

	@Override
	public boolean equals(final Object object) {
		if (object instanceof PropertyModifiedEvent) {
			final PropertyModifiedEvent that = (PropertyModifiedEvent) object;
			return (this.propertyName == that.propertyName ||this.propertyName!=null && this.propertyName.equals(that.propertyName)) &&
					(this.oldValue == that.oldValue        ||this.oldValue!=null && this.oldValue.equals(that.oldValue)) &&
					(this.newValue == that.newValue    ||this.newValue!=null && this.newValue.equals(that.newValue)) ;
		}
		return false;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append("propertyName")
			.append(this.propertyName)
			.append(", oldValue")
			.append(this.oldValue)
			.append(",newValue")
			.append(this.newValue)
			.toString();
	}
	
}
