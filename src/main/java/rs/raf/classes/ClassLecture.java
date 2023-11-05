package rs.raf.classes;

import java.util.Date;

public class ClassLecture {
    private String className;
    private String professor;
    private int startTime;
    private int duration;
    private Date startDate;
    private Date endDate;

    public ClassLecture(String className, String professor, int startTime, int duration, Date startDate, Date endDate) {
        this.className = className;
        this.professor = professor;
        this.startTime = startTime;
        this.duration = duration;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
