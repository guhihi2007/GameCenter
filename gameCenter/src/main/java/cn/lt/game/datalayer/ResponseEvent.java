package cn.lt.game.datalayer;

/**
 * Created by Administrator on 2015/11/26.
 */
public class ResponseEvent extends Event{
    public ResponseStatus status = null;
    public Object params;

    public ResponseEvent(EventId id, Object o, EventContext eventContext, ResponseStatus status,Object params) {
        super(id, o, eventContext);
        this.status = status;
        this.params = params;
    }
}
