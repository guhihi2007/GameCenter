package cn.lt.game.datalayer;

/**
 * Created by Administrator on 2015/12/10.
 */
public class EventContext {
    private int totalPages = -1;
    public RequestMode requestMode = new RequestMode();
    public Object[] extras ;

    public EventContext() {

    }

    public EventContext(RequestMode requestMode) {
        if(requestMode!=null){
            this.requestMode = requestMode;
        }
    }

    public EventContext(RequestMode requestMode,Object... extras) {
        if(requestMode!=null) {
            this.requestMode = requestMode;
        }
        this.extras = extras;
    }

    public int getTotalPages() {
        return totalPages;
    }

    void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
