package com.drawshirt.mediamobile.bean;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class PlayHistoryInfo {
    private String title;
    private String image_url;
    private String source_url;
    private long play_time;
    private int is_net=0;
    private String position="";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public long getPlay_time() {
        return play_time;
    }

    public void setPlay_time(long play_time) {
        this.play_time = play_time;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getIs_net() {
        return is_net;
    }

    public void setIs_net(int is_net) {
        this.is_net = is_net;
    }

    @Override
    public String toString() {
        return "PlayHistoryInfo{" +
                "title='" + title + '\'' +
                ", image_url='" + image_url + '\'' +
                ", source_url='" + source_url + '\'' +
                ", play_time=" + play_time +
                ", is_net=" + is_net +
                ", position='" + position + '\'' +
                '}';
    }
}
