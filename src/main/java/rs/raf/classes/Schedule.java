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

    public Schedule(String name, Map<Term, ClassLecture> scheduleMap, List<Classroom> classrooms) {
        this.name = name;
        this.scheduleMap = scheduleMap;
        this.classrooms = classrooms;
    }
    public void createDates(Date startDate, Date toDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(toDate)) {
            System.out.println(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

}
