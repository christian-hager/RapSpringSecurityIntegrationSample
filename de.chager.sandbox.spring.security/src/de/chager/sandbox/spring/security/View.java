package de.chager.sandbox.spring.security;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import de.chager.sandbox.someservice.api.ModelObject;
import de.chager.sandbox.someservice.api.SomeService;

public class View extends ViewPart {
	public static final String ID = "de.chager.sandbox.spring.security.view";
	public static String newline = System.getProperty("line.separator");

	private TableViewer viewer;

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return ((ModelObject) obj).getSomeStringAttribute();
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {

		// Get the Service an load some data
		BundleContext bundleContext = Activator.getDefault().getBundle()
				.getBundleContext();
		ServiceReference ref = bundleContext
				.getServiceReference(SomeService.class.getName());

		try {
			viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
					| SWT.V_SCROLL);
			viewer.setContentProvider(new ArrayContentProvider());
			viewer.setLabelProvider(new ViewLabelProvider());
			SomeService someService = (SomeService) bundleContext
					.getService(ref);
			viewer.setInput(someService.readAllModelObjects().toArray());
		} finally {
			bundleContext.ungetService(ref);
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		if (viewer != null) {
			viewer.getControl().setFocus();
		}
	}
}