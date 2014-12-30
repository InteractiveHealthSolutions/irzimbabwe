/**
 * Interface implemented by all Forms 
 */

package org.irdresearch.irzimbabwe.client;

import com.google.gwt.event.dom.client.ClickEvent;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public interface IForm
{
	void clearUp ();

	boolean validate ();

	void saveData ();

	void updateData ();

	void deleteData ();

	void fillData ();

	void setCurrent ();

	void setRights (String menuName);

	void onClick (ClickEvent event);
}
