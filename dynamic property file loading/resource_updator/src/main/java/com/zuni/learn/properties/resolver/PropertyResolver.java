package com.zuni.learn.properties.resolver;

/**
 * 
 * @author Zuned Ahmed
 *
 */
public interface PropertyResolver {

	String resolveProperty(final Object property);
	boolean requiresFurtherResoltuion(final Object property);
}
