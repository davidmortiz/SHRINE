package net.shrine.webclient.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author clint
 * @date May 15, 2012
 */
public interface QueryGroupsChangedEventHandler extends EventHandler {
	public void handle(final QueryGroupsChangedEvent event);
}