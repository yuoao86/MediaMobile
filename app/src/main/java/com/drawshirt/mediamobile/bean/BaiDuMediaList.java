package com.drawshirt.mediamobile.bean;

import java.util.List;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class BaiDuMediaList {
    private String name;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<BaiDuRecommend> getHot() {
        return hot;
    }

    public void setHot(List<BaiDuRecommend> hot) {
        this.hot = hot;
    }

    private String tag;
    private List<BaiDuRecommend> hot;

}
