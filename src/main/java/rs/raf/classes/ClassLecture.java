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

    public String getClassName() {
        return className;
    }

    public String getProfessor() {
        return professor;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        if(endDate==null)
        return "ClassLecture{" +
                "className='" + className + '\'' +
                ", professor='" + professor + '\'' +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", startDate=" + startDate +
                '}';
        return "ClassLecture{" +
                "className='" + className + '\'' +
                ", professor='" + professor + '\'' +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
