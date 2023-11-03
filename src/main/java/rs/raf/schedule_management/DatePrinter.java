package rs.raf.schedule_management;

import rs.raf.classes.ClassLecture;
import rs.raf.classes.Classroom;
import rs.raf.classes.Term;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DatePrinter {
    public static void printDates(Date startDate, Date toDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(toDate)) {
            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                continue;
            }
//            System.out.println(calendar.getTime()); // ovo su radni dani

            Date entry = calendar.getTime();
            System.out.println(entry);

            for(int time = 8; time<= 20; time++){ // radni sati radno vreme iz schedule
                for(int room = 0; room<8; room ++) { // ucionice to ce biti iz liste clasrooms iz schedule
//                    Term term = new Term(room,time,date)
                    Classroom classroom = new Classroom(Integer.toString(room),room,null); // ucionice (njih cemo imati u schedule kao listu)
                    Term term = new Term(classroom,time,entry);
//                    Term term = new Term(classroom,time,entry);
                    Map<Term, ClassLecture> map = new HashMap<>();
                    map.put(term,null);
                }
            }


            calendar.add(Calendar.DAY_OF_MONTH, 1);



        }
    }

    public static void main(String[] args) {
        // Example usage
        Date startDate = new Date(2023-1900,8,13);  // Replace with your actual start date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 0);  // Adding 10 days for example
//        Date toDate = calendar.getTime();
        Date toDate = new Date(2023-1900,9,13);

        printDates(startDate, toDate);
    }
}