package com.zuni.learn.properties.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Zuned Ahmed
 *
 */
@Component
public class SubstitutingPropertyResolver implements PropertyResolver {

	protected static Logger log = LoggerFactory.getLogger(SubstitutingPropertyResolver.class);

	public String resolveProperty(final Object property) {
		final String stringProperty = property.toString();

		// if property is a ${} then substitute it for the property it refers to
		final String resolvedProperty = propertyRequiresSubstitution(stringProperty)
				? stringProperty.substring(2, stringProperty.length() - 1)
				: stringProperty;

		log.info("Property Resolved from [{}] to [{}]", new Object[] { property, resolvedProperty });
		return resolvedProperty;
	}

	public boolean requiresFurtherResoltuion(final Object property) {
		if (null == property) {
			log.info("Property is null");
			return false;
		}
		final boolean propertyRequiresSubstitution = propertyRequiresSubstitution(property.toString());
		if (propertyRequiresSubstitution) {
			log.info("Further resolution required for property value [{}]", new Object[] { property });
		}
		return propertyRequiresSubstitution;
	}

	/**
	 * Tests whether the given property is a ${...} property and therefore requires further resolution
	 */
	private boolean propertyRequiresSubstitution(final String property) {
		if (null != property) {
			return property.startsWith("${") && property.endsWith("}");
		}
		return false;
	}
}
