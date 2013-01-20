package com.zuni.learn.properties;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.zuni.learn.properties.event.PropertyModifiedEvent;
import com.zuni.learn.properties.resolver.PropertyResolver;


/**
 * 
 * @author Zuned Ahmed
 *
 */
public class ReadablePropertySourcesPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer implements ApplicationEventPublisherAware {

	protected static Logger log = LoggerFactory.getLogger(ReadablePropertySourcesPlaceholderConfigurer.class);

	private Properties properties;
	private Resource[] locations;
	
	protected ApplicationEventPublisher applicationEventPublisher;
	
	/**
	 * Overridden from PropertySourcesPlaceholderConfigurer
	 */
	@Override
	protected void loadProperties(final Properties props) throws IOException {
		super.loadProperties(props);
		this.properties = props;
	}
	
	/**
	 * Overridden from PropertySourcesPlaceholderConfigurer
	 */
	@Override
	public void setLocations(final Resource[] locations) {
		super.setLocations(locations);
		this.locations = locations;
	}
	
	public Properties getProperties() {
		return properties;
	}

	private final PropertyResolver propertyResolver;
	
	public ReadablePropertySourcesPlaceholderConfigurer(final PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
	}
	
	public void onResourceChanged(Resource resource) {
		try {
			final Properties reloadedProperties = PropertiesLoaderUtils.loadProperties(resource);
			for (final String property : this.properties.stringPropertyNames()) {

				final String oldValue = this.properties.getProperty(property);
				final String newValue = reloadedProperties.getProperty(property);

				if (propertyExistsAndNotNull(property, newValue) && propertyChange(oldValue, newValue)) {

					// Update locally stored copy of properties
					this.properties.setProperty(property, newValue);

					// Post change event to notify any potential listeners
					this.applicationEventPublisher.publishEvent(new ApplicationEvent(new PropertyModifiedEvent(property, oldValue, newValue)){});
				}
			}
		}
		catch (final IOException e) {
			log.error("Failed to reload properties file once change", e);
		}		
	}

	public Object resolveProperty(final Object property) {
		final Object resolvedPropertyValue = this.properties.get(this.propertyResolver.resolveProperty(property));
		if (this.propertyResolver.requiresFurtherResoltuion(resolvedPropertyValue)) {
			return resolveProperty(resolvedPropertyValue);
		}
		return resolvedPropertyValue;
	}
	
	private boolean propertyChange(final String oldValue, final String newValue) {
		return null == oldValue || !oldValue.equals(newValue);
	}

	private boolean propertyExistsAndNotNull(final String property, final String newValue) {
		return this.properties.containsKey(property) && null != newValue;
	}
	
	public void startWatching() {
		if (null == this.applicationEventPublisher) {
			throw new BeanInitializationException("Event bus not setup, you should not be calling this method...!");
		}
		try {
			for(Resource resource : locations){
				FileObject file=null;
				try {
					file = VFS.getManager().toFileObject(resource.getFile());
				} catch (Exception e) {
					log.error("unable to create file object for resource "+resource);
					continue;
				}
				DefaultFileMonitor fm = new DefaultFileMonitor(new ResourceChangeListener(resource));
				fm.setDelay(5000);
				fm.setRecursive(false);
				fm.addFile(file); 
				fm.start();
			}
		}
		catch (final Exception e) {
			log.error("Unable to start properties file watcher", e);
		}
	}
	
	public class ResourceChangeListener implements FileListener {
		final Resource resource;
		
		ResourceChangeListener(Resource resource) {
			this.resource = resource;
		}

		public void fileCreated(FileChangeEvent event) throws Exception {
			// TODO Auto-generated method stub
		}

		public void fileDeleted(FileChangeEvent event) throws Exception {
			// TODO Auto-generated method stub
		}

		public void fileChanged(FileChangeEvent event) throws Exception {
			System.out.println("ResourceChangeListener.fileChanged()\n\n\n");
			onResourceChanged(resource);
		}

	}

	public void setApplicationEventPublisher( ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
}
