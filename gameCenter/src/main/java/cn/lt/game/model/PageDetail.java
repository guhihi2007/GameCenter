package cn.lt.game.model;

public class PageDetail implements Cloneable {

    public String desc = "";            // 页面描述，如“精选页面”
    public Class<?> activityClass; // Activity类型，如:IndexActivity.class
    public boolean needParam = false;  // 调用Activity是否需要参数，此参数由后台传过来，如游戏ID等，true为需要
    public String value = "";       //内部参数
    public String value2 = "";       //内部参数2
    public String key = "";   // 内部参数名称
    public String key2 = "";   // 内部参数名称
    public String id = "";
    public String url = "";

    public static class Builder {
        private PageDetail pageDetail;

        public Builder() {
            this.pageDetail = new PageDetail();
        }

        /***
         * 是否需要内部参数
         * @param needParam
         * @return
         */
        public Builder setNeedParam(boolean needParam) {
            pageDetail.needParam = needParam;
            return this;
        }

        public Builder setId(String id) {
            pageDetail.id = id;
            return this;
        }

        public Builder setDesc(String desc) {
            pageDetail.desc = desc;
            return this;
        }

        public Builder setClass(Class<?> clazz) {
            pageDetail.activityClass = clazz;
            return this;
        }

        public Builder setValue(String value) {
            pageDetail.value = value;
            return this;
        }

        public Builder setValue2(String value) {
            pageDetail.value2 = value;
            return this;
        }

        public Builder setKey(String key) {
            pageDetail.key = key;
            return this;
        }

        public Builder setKey2(String key) {
            pageDetail.key2 = key;
            return this;
        }

        public PageDetail build() {
            return pageDetail;
        }
    }

    public Object clone() {
        PageDetail o = new PageDetail();
        try {
            o = (PageDetail) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
