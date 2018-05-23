package cn.lt.game.datalayer;

/**
 * Created by Administrator on 2015/11/25.
 */
public class RequestEvent extends Event {

    public RequestEvent(EventId id, Object o) {
        super(id, o, new EventContext());
    }

    public RequestEvent(EventId id, Object o, EventContext eventContext) {
        super(id, o, eventContext);
    }
}
