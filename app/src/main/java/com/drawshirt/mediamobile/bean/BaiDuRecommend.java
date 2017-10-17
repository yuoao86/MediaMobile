package com.drawshirt.mediamobile.bean;

import java.io.Serializable;

public class BaiDuRecommend implements Serializable{

        //"works_id": "25652",
		//"terminal_type": "0",
		//"title": "独裁者",
		//"url": "",
		//"img_url": "http://a.hiphotos.baidu.com/video/pic/item/2fdda3cc7cd98d10b1dbfa13263fb80e7bec9081.jpg",
		//"duration": "",
		//"update": "",
		//"rating": "76",
		//"tag": "",
		//"works_type": "movie",
		//"label": "",
		//"brief": "丧心病狂的无节操片"

	private String works_id;
	private String terminal_type;
	private String title;
	private String url;
	private String img_url;
	private String duration;
	private String update;
	private String rating;
	private String tag;
	private String works_type;
	private String label;
	private String brief;
	private String play_filter;
	private String actor;
	private String type;
	private String status_day;

	public String getStatus_day() {
		return status_day;
	}

	public void setStatus_day(String status_day) {
		this.status_day = status_day;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWorks_id() {
		return works_id;
	}

	public void setWorks_id(String works_id) {
		this.works_id = works_id;
	}

	public String getTerminal_type() {
		return terminal_type;
	}

	public void setTerminal_type(String terminal_type) {
		this.terminal_type = terminal_type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getWorks_type() {
		return works_type;
	}

	public void setWorks_type(String works_type) {
		this.works_type = works_type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	@Override
	public String toString() {
		return "BaiDuRecommend{" +
				"works_id='" + works_id + '\'' +
				", terminal_type='" + terminal_type + '\'' +
				", title='" + title + '\'' +
				", url='" + url + '\'' +
				", img_url='" + img_url + '\'' +
				", duration='" + duration + '\'' +
				", update='" + update + '\'' +
				", rating='" + rating + '\'' +
				", tag='" + tag + '\'' +
				", works_type='" + works_type + '\'' +
				", label='" + label + '\'' +
				", brief='" + brief + '\'' +
				", play_filter='" + play_filter + '\'' +
				", actor='" + actor + '\'' +
				", type='" + type + '\'' +
				'}';
	}

	public String getPlay_filter() {
		return play_filter;
	}

	public void setPlay_filter(String play_filter) {
		this.play_filter = play_filter;
	}
}
