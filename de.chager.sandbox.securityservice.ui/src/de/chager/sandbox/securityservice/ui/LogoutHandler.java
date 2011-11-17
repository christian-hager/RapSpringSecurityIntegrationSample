package de.chager.sandbox.securityservice.ui;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.PhaseEvent;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.swt.widgets.Display;

@SuppressWarnings("restriction")
public class LogoutHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		StringBuffer urlBuffer = new StringBuffer();
		urlBuffer.append(RWT.getRequest().getContextPath());
		urlBuffer.append(RWT.getRequest().getServletPath());
		final String url = RWT.getResponse().encodeURL(urlBuffer.toString());
		final String browserText = MessageFormat.format(
				"parent.window.location.href = \"{0}\";", url); //$NON-NLS-1$

		// Destroy the session immediately if the user explicitly logs out.
		// This automatically leads to Workbench.close (see ShutdownHandler in
		// // Workbench.java)

		RWT.getRequest().getSession().setMaxInactiveInterval(0);

		// Use a Phaselistener to write the redirect to the response. Using a
		// Browser-Widget
		// didn't work reliable, as a subsequent request from the browser ended
		// // in a new session
		// on the server side. The server returned an empty response which lead
		// to a
		// "Request failed: Status code 0" error in the Browser.
		final Display display = Display.getCurrent();
		RWT.getLifeCycle().addPhaseListener(new PhaseListener() {
			private static final long serialVersionUID = 1L;

			public void afterPhase(PhaseEvent event) {
				// It is important to use afterPhase to let all other statements
				// be
				// parsed.
				// Otherwise, disabling the exit dialog confirmation doesn't
				// work.
				if (display == Display.getCurrent()) {
					try {
						final HtmlResponseWriter writer = ContextProvider
								.getStateInfo().getResponseWriter();
						writer.write(browserText);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						RWT.getLifeCycle().removePhaseListener(this);
					}
				}
			}

			public void beforePhase(PhaseEvent event) {
			}

			public PhaseId getPhaseId() {
				return PhaseId.RENDER;
			}
		});
		
		return Status.OK_STATUS;
	}



}
