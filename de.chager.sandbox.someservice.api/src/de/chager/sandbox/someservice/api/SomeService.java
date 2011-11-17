package de.chager.sandbox.someservice.api;

import java.util.List;

/**
 * A simple service interface for demo purposes.
 * 
 * @author christianhager
 */
public interface SomeService {

	public List<ModelObject> readAllModelObjects();
	
	public void writeModelObject(ModelObject modelObject);
	
}
