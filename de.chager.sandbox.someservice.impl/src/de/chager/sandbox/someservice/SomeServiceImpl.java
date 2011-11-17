package de.chager.sandbox.someservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import de.chager.sandbox.someservice.api.ModelObject;
import de.chager.sandbox.someservice.api.SomeService;

/**
 * The implementation of the sample service (for demo purposes only).
 * 
 * @author christianhager
 */
public class SomeServiceImpl
		implements SomeService
{
	@Override
	@PreAuthorize( "hasRole('RIGHT_READ_MODELOBJECTS')" )
	public List<ModelObject> readAllModelObjects()
	{
		final List<ModelObject> modelObjects = new ArrayList<ModelObject>();
		modelObjects.add( new ModelObjectImpl( "Some String", 42.42d ) );
		modelObjects.add( new ModelObjectImpl( "Another String", 88.88d ) );
		modelObjects.add( new ModelObjectImpl( "Some other String", 47.11d ) );
		modelObjects.add( new ModelObjectImpl( "Yet another String", 8.15d ) );
		return modelObjects;
	}
	
	@Override
	public void writeModelObject(
			final ModelObject modelObject )
	{
	}
}
