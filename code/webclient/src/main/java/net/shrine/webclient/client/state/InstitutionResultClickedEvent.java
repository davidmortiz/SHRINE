package net.shrine.webclient.client.state;

import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.Event;
import net.shrine.webclient.client.util.EventUtil;

/**
 * @author Bill Simons
 * @date 9/18/12
 * @link http://cbmi.med.harvard.edu
 * @link http://chip.org
 * <p/>
 * NOTICE: This software comes with NO guarantees whatsoever and is
 * licensed as Lgpl Open Source
 * @link http://www.gnu.org/licenses/lgpl.html
 */
public class InstitutionResultClickedEvent extends GwtEvent<InstitutionResultClickedEventHandler> {
    private static final GwtEvent.Type<InstitutionResultClickedEventHandler> TYPE = EventUtil.eventType();

    private int index;

    public InstitutionResultClickedEvent(final int index) {
        super();
        this.index = index;
    }

    public static Type<InstitutionResultClickedEventHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<InstitutionResultClickedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InstitutionResultClickedEventHandler handler) {
        handler.handle(this);
    }

    public int getIndex() {
        return index;
    }
}