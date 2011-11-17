package de.chager.sandbox.someservice;

import de.chager.sandbox.someservice.api.ModelObject;

/**
 * A sample model object (for demo purposes only).
 * 
 * @author christianhager
 */
public class ModelObjectImpl implements ModelObject {

	public ModelObjectImpl(String someStringAttribute,
			Double someDoubleAttribute) {
		super();
		this.someStringAttribute = someStringAttribute;
		this.someDoubleAttribute = someDoubleAttribute;
	}

	private String someStringAttribute;

	private Double someDoubleAttribute;

	/* (non-Javadoc)
	 * @see de.chager.sandbox.someservice.IModelObject#getSomeStringAttribute()
	 */
	@Override
	public String getSomeStringAttribute() {
		return someStringAttribute;
	}

	/* (non-Javadoc)
	 * @see de.chager.sandbox.someservice.IModelObject#setSomeStringAttribute(java.lang.String)
	 */
	@Override
	public void setSomeStringAttribute(String someStringAttribute) {
		this.someStringAttribute = someStringAttribute;
	}

	/* (non-Javadoc)
	 * @see de.chager.sandbox.someservice.IModelObject#getSomeDoubleAttribute()
	 */
	@Override
	public Double getSomeDoubleAttribute() {
		return someDoubleAttribute;
	}

	/* (non-Javadoc)
	 * @see de.chager.sandbox.someservice.IModelObject#setSomeDoubleAttribute(java.lang.Double)
	 */
	@Override
	public void setSomeDoubleAttribute(Double someDoubleAttribute) {
		this.someDoubleAttribute = someDoubleAttribute;
	}

}
