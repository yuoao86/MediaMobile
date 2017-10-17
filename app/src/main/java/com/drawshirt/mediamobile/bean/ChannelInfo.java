package com.drawshirt.mediamobile.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChannelInfo implements Serializable {

    public int getVideo_num() {
        return video_num;
    }

    public void setVideo_num(int video_num) {
        this.video_num = video_num;
    }

    public int getBeg() {
        return beg;
    }

    public void setBeg(int beg) {
        this.beg = beg;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public List<BaiDuRecommend> getVideosArr() {
        if (videosArr == null) {
            videosArr = new ArrayList<BaiDuRecommend>();
        }
        return videosArr;
    }

    public void setVideosArr(List<BaiDuRecommend> videosArr) {
        this.videosArr = videosArr;
    }


    private int video_num;
    private int beg;
    private int end;
    private List<BaiDuRecommend> videosArr=new ArrayList<>();


    private List<CurrentConds> curCondsArr=new ArrayList<>();

    public List<CurrentConds> getCurCondsArr() {
        return curCondsArr;
    }

    public void setCurCondsArr(List<CurrentConds> curCondsArr) {
        this.curCondsArr = curCondsArr;
    }

    @Override
    public String toString() {
        return "ChannelInfo{" +
                "video_num=" + video_num +
                ", beg=" + beg +
                ", end=" + end +
                ", videosArr=" + videosArr +
                ", curCondsArr=" + curCondsArr +
                '}';
    }
}
