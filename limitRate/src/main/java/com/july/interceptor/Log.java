package com.july.interceptor;

public class Log {
    String title;
    Long timer;

    public Long getTimer() {
        return timer;
    }

    public void setTimer(Long timer) {
        this.timer = timer;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    @Override
    public String toString() {
        return "Log{" +
                "title='" + title + '\'' +
                ", timer=" + timer +
                '}';
    }
}
