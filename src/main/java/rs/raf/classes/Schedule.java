package rs.raf.classes;

import com.sun.java.accessibility.util.GUIInitializedListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;



public class Schedule {

    private String name;
    private Map<Term,ClassLecture> scheduleMap;
    private List<Classroom> classrooms;
    private Date startDate;
    private Date endDate;
    private int startHours;
    private int endHours;

    public Schedule(String name, Map<Term, ClassLecture> scheduleMap, List<Classroom> classrooms, Date startDate, Date endDate, int startHours, int endHours) {
        this.name = name;
        this.scheduleMap = scheduleMap;
        this.classrooms = classrooms;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startHours = startHours;
        this.endHours = endHours;
    }

    public Classroom getClassroomByName(String name){
        for(Classroom c: this.classrooms){
            if(c.getName().equals(name))
                return c;
        }
        return null;
    }

    public Map<Term,ClassLecture> getScheduleMap(){
        return this.scheduleMap;
    }

    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getStartHours() {
        return startHours;
    }

    public int getEndHours() {
        return endHours;
    }
}
