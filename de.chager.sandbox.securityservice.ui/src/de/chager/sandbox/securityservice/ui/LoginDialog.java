package de.chager.sandbox.securityservice.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import de.chager.sandbox.securityservice.api.SecurityService;

/**
 * A Login-Dialog for RAP-based Applications
 * 
 * @author Dipl.-Inf. (FH) Christian Hager
 */
public class LoginDialog extends TitleAreaDialog {

	private Text textUser;
	private Text textPassword;
	private Button loginButton;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public LoginDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.NO_TRIM);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		// Set the title
		setTitle("OSGi + Spring Security Integration Demo");

		// Set the message
		setMessage("Please enter your login data!",
				IMessageProvider.INFORMATION);

		// Set the image
		setTitleImage(Activator.getImageDescriptor("icon/lock.png")
				.createImage());

		Composite container = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(2, false));

		Label labelUser = new Label(composite, SWT.NONE);
		labelUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		labelUser.setText("User");

		ModifyListener emptyListener = new EmptyListener();

		textUser = new Text(composite, SWT.BORDER);
		textUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		textUser.setTextLimit(30);
		textUser.addModifyListener(emptyListener);

		Label labelPassword = new Label(composite, SWT.NONE);
		labelPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		labelPassword.setText("Password");

		textPassword = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		textPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		textPassword.setTextLimit(30);
		textPassword.addModifyListener(emptyListener);

		return container;
	}

	@Override
	protected void okPressed() {

		int status = authenticate();

		switch (status) {
		case SecurityService.STATUS_LOGIN_BAD_CREDENTIALS:
			setErrorMessage("Login failed due to bad credentials.");
			break;
		case SecurityService.STATUS_LOGIN_FAILED_UNKOWN:
			setErrorMessage("Login failed for an unknown reason. Please conact an administrator!");
			break;
		case SecurityService.STATUS_LOGIN_DISABLED:
			setErrorMessage("Login failed because of disabled account. Please conact an administrator!");
			break;
		default:
			super.okPressed();
			break;
		}
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		Rectangle displayBounds = Display.getCurrent().getBounds();
		Point size = getShell().getSize();
		int x = (displayBounds.width - size.x) / 2;
		int y = (displayBounds.height - size.y) / 2;
		getShell().setLocation(x, y);
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		loginButton = createButton(parent, IDialogConstants.OK_ID, "Login",
				true);
		loginButton.setEnabled(false);
	}

	private int authenticate() {

		int status = SecurityService.STATUS_LOGIN_FAILED_UNKOWN;

		BundleContext bundleContext = Activator.getDefault().getBundle()
				.getBundleContext();
		ServiceReference ref = bundleContext
				.getServiceReference(SecurityService.class.getName());

		try {
			SecurityService securityService = (SecurityService) bundleContext
					.getService(ref);
			status = securityService.authenticate(textUser.getText(),
					textPassword.getText());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bundleContext.ungetService(ref);
		}

		return status;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 200);
	}

	class EmptyListener implements ModifyListener {

		@Override
		public void modifyText(ModifyEvent event) {
			setErrorMessage(null);
			if (textUser.getText().length() > 0
					&& textPassword.getText().length() > 0) {
				loginButton.setEnabled(true);
			} else {
				loginButton.setEnabled(false);
			}
		}
	}

}
