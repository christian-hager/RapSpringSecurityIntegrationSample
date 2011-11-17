package de.chager.sandbox.spring.security;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import de.chager.sandbox.securityservice.api.SecurityService;
import de.chager.sandbox.securityservice.ui.Activator;
import de.chager.sandbox.securityservice.ui.LoginDialog;

/**
 * This class controls all aspects of the application's execution and is
 * contributed through the plugin.xml.
 */
public class Application implements IEntryPoint {

	public int createUI() {

		// initialize the security service
		BundleContext bundleContext = Activator.getDefault().getBundle()
				.getBundleContext();
		ServiceReference ref = bundleContext
				.getServiceReference(SecurityService.class.getName());

		try {
			SecurityService securityService = (SecurityService) bundleContext
					.getService(ref);
			 securityService.initDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bundleContext.ungetService(ref);
		}

		Display display = PlatformUI.createDisplay();
		LoginDialog loginDialog = new LoginDialog(new Shell());
		loginDialog.open();
		WorkbenchAdvisor advisor = new ApplicationWorkbenchAdvisor();
		return PlatformUI.createAndRunWorkbench(display, advisor);
	}

}
