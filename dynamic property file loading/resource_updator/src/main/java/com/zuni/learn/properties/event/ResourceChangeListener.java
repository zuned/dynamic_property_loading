package com.zuni.learn.properties.event;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;

/**
 * 
 * @author Zuned Ahmed
 *
 */
public class ResourceChangeListener implements FileListener {

	public void fileCreated(FileChangeEvent event) throws Exception {
		// TODO Auto-generated method stub
	}

	public void fileDeleted(FileChangeEvent event) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void fileChanged(FileChangeEvent event) throws Exception {
		System.out.println("ResourceChangeListener.fileChanged()\n\n\n");
	}

}
