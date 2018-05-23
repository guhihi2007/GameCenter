package cn.lt.game.datalayer;

/**
 * Created by Administrator on 2015/11/26.
 */
public class Event {
    public EventId eventId;
    public Object obj;
    public EventContext eventContext = new EventContext(new RequestMode());

    public Event(EventId id, Object o, EventContext eventContext) {
        this.eventId = id;
        this.obj = o;
        if (eventContext != null) {
            this.eventContext = eventContext;
        }
    }

    public boolean isNetEvent() {
        return true;
    }
}
