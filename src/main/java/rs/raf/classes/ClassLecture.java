package rs.raf.classes;

import java.util.Date;

public class ClassLecture {
    private String className;
    private String professor;
    private int startTime;
    private int duration;
    private Date date;

    public ClassLecture(String className, String professor, int startTime, int duration, Date date) {
        this.className = className;
        this.professor = professor;
        this.startTime = startTime;
        this.duration = duration;
        this.date = date;
    }
}
