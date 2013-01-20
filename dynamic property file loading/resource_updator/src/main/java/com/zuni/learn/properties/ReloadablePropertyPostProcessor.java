package com.zuni.learn.properties;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.google.common.collect.Maps;
import com.zuni.learn.properties.bean.BeanPropertyHolder;
import com.zuni.learn.properties.conversion.PropertyConversionService;
import com.zuni.learn.properties.event.PropertyModifiedEvent;

/**
 * 
 * @author Zuned Ahmed
 *
 */
@Component
public class ReloadablePropertyPostProcessor extends InstantiationAwareBeanPostProcessorAdapter   implements ApplicationListener<ApplicationEvent>{

	protected static Logger log = LoggerFactory.getLogger(ReloadablePropertyPostProcessor.class);
	
	private final PropertyConversionService propertyConversionService;
	private final ReadablePropertySourcesPlaceholderConfigurer placeholderConfigurer;
	private Map<String, Set<BeanPropertyHolder>> beanPropertySubscriptions = Maps.newHashMap();
	
	public ReloadablePropertyPostProcessor(final ReadablePropertySourcesPlaceholderConfigurer placeholderConfigurer,
			 final PropertyConversionService conversionService) {
		this.placeholderConfigurer = placeholderConfigurer;
		this.propertyConversionService = conversionService;
	}
	
	@PostConstruct
	protected void init() {
		log.info("Registering ReloadablePropertyProcessor for properties file changes");
		registerPropertyReloader();
	}

	private void registerPropertyReloader() {
		this.placeholderConfigurer.startWatching();
	}
	
	@Override
	public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {
		if (log.isDebugEnabled()) {
			log.debug("Setting Reloadable Properties on [{}]", beanName);
		}
		setPropertiesOnBean(bean);
		return true;
	}
	private void setPropertiesOnBean(final Object bean) {
		ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {

			public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {

				final ReloadableProperty annotation = field.getAnnotation(ReloadableProperty.class);
				if (null != annotation) {

					ReflectionUtils.makeAccessible(field);
					validateFieldNotFinal(bean, field);

					final Object property = getProperties().get(annotation.value());
					validatePropertyAvailableOrDefaultSet(bean, field, annotation, property);

					if (null != property) {

						log.info("Attempting to convert and set property [{}] on field [{}] for class [{}] to type [{}]",
								new Object[] { property, field.getName(), bean.getClass()
									.getCanonicalName(), field.getType() });

						final Object convertedProperty = convertPropertyForField(field, annotation.value());

						log.info("Setting field [{}] of class [{}] with value [{}]", new Object[] { field.getName(), bean.getClass()
							.getCanonicalName(), convertedProperty });

						field.set(bean, convertedProperty);

						subscribeBeanToPropertyChangedEvent(annotation.value(), new BeanPropertyHolder(bean, field));
					}
					else {
						log.info("Leaving field [{}] of class [{}] with default value", new Object[] { field.getName(), bean.getClass()
							.getCanonicalName() });
					}
				}
			}
		});
	}
	
	private void subscribeBeanToPropertyChangedEvent(final String property, final BeanPropertyHolder fieldProperty) {
		if (!this.beanPropertySubscriptions.containsKey(property)) {
			this.beanPropertySubscriptions.put(property, new HashSet<BeanPropertyHolder>());
		}
		this.beanPropertySubscriptions.get(property)
			.add(fieldProperty);
	}
	
	private void validatePropertyAvailableOrDefaultSet(final Object bean, final Field field, final ReloadableProperty annotation, final Object property)
			throws IllegalArgumentException, IllegalAccessException {
		if (null == property && fieldDoesNotHaveDefault(field, bean)) {
			throw new BeanInitializationException(String.format("No property found for field annotated with @ReloadableProperty, "
				+ "and no default specified. Property [%s] of class [%s] requires a property named [%s]", field.getName(), bean.getClass()
				.getCanonicalName(), annotation.value()));
		}
	}

	private void validateFieldNotFinal(final Object bean, final Field field) {
		if (Modifier.isFinal(field.getModifiers())) {
			throw new BeanInitializationException(String.format("Unable to set field [%s] of class [%s] as is declared final", field.getName(), bean.getClass()
				.getCanonicalName()));
		}
	}

	private boolean fieldDoesNotHaveDefault(final Field field, final Object value) throws IllegalArgumentException, IllegalAccessException {
		try {
			return (null == field.get(value));
		}
		catch (final NullPointerException e) {
			return true;
		}
	}
	private void handlePropertyChange(final PropertyModifiedEvent event) {
		for (final BeanPropertyHolder bean : this.beanPropertySubscriptions.get(event.getPropertyName())) {
			updateField(bean, event);
		}
	}

	public void updateField(final BeanPropertyHolder holder, final PropertyModifiedEvent event) {
		final Object beanToUpdate = holder.getBean();
		final Field fieldToUpdate = holder.getField();
		final String canonicalName = beanToUpdate.getClass().getCanonicalName();

		final Object convertedProperty = convertPropertyForField(fieldToUpdate, event.getPropertyName());
		try {
			log.info("Reloading property [{}] on field [{}] for class [{}]", new Object[] { event.getPropertyName(), fieldToUpdate.getName(), canonicalName });
			fieldToUpdate.set(beanToUpdate, convertedProperty);
		}
		catch (final IllegalAccessException e) {
			log.error("Unable to reloading property [{}] on field [{}] for class [{}]\n Exception [{}]",
					new Object[] { event.getPropertyName(), fieldToUpdate.getName(), canonicalName, e.getMessage() });
		}
	}
	
	private Object convertPropertyForField(final Field field, final Object property) {
		return this.propertyConversionService.convertPropertyForField(field, resolverProperty(property));
	}
	private Object resolverProperty(final Object property) {
		return this.placeholderConfigurer.resolveProperty(property);
	}

	private Properties getProperties() {
		return this.placeholderConfigurer.getProperties();
	}

	public void onApplicationEvent(ApplicationEvent event) {
		Object object = event.getSource();
		if(object instanceof PropertyModifiedEvent){
			handlePropertyChange((PropertyModifiedEvent)object);
		}
	}
}
