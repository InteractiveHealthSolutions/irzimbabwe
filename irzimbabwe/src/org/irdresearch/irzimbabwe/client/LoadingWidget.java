/**
 * Loading Widget for GWT 
 */

package org.irdresearch.irzimbabwe.client;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class LoadingWidget extends PopupPanel
{
	/**
	 * Initialize loading widget
	 */
	public LoadingWidget ()
	{
		setSize ("100%", "100%");
		setGlassEnabled (true);
		setStyleName ("mainVerticalPanel");
		Image image = new Image ("images/loading.gif");
		image.setAltText ("Loading...");
		setWidget (image);
		image.setSize ("50px", "50px");
	}
}