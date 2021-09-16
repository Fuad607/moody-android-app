
package com.example.moody.model;

import java.util.List;
public class Result {

    private Integer id;
    private String nickname;
    private List<Integer> moodData = null;
    private List<Integer> relaxedData = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<Integer> getMoodData() {
        return moodData;
    }

    public void setMoodData(List<Integer> moodData) {
        this.moodData = moodData;
    }

    public List<Integer> getRelaxedData() {
        return relaxedData;
    }

    public void setRelaxedData(List<Integer> relaxedData) {
        this.relaxedData = relaxedData;
    }

}
