package com.zuni.learn.properties.conversion;

import java.lang.reflect.Field;

/**
 * 
 * @author Zuned Ahmed
 */
public interface PropertyConversionService {

	Object convertPropertyForField(final Field field, final Object property);
}
