package rs.raf.schedule_management;

import rs.raf.classes.ClassLecture;
import rs.raf.classes.Classroom;
import rs.raf.classes.Term;
import rs.raf.enums.AddOns;
import rs.raf.exceptions.DuplicateAddOnsException;

import java.util.*;

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


            calendar.add(Calendar.DAY_OF_MONTH, 7);



        }
    }

    public static void main(String[] args) {
        // Example usage
//        Date startDate = new Date(2023-1900,8,13);  // Replace with your actual start date
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DAY_OF_MONTH, 0);  // Adding 10 days for example
////        Date toDate = calendar.getTime();
//        Date toDate = new Date(2023-1900,9,13);
//
//        printDates(startDate, toDate);


        String input = "5,projector,whiteboard,pen,pen";
        String[] parts = input.split(",");
        List<String> addonsString = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length)));

        ArrayList<AddOns> addOns = new ArrayList<>();

        for(String s: addonsString){
            if(s.equals("projector"))
                addOns.add(AddOns.PROJECTOR);
            else if (s.equals("whiteboard")) {
                addOns.add(AddOns.WHITEBOARD);
            }
            else if (s.equals("computers")) {
                addOns.add(AddOns.COMPUTERS);
            }
            else if (s.equals("pen")) {
                addOns.add(AddOns.PEN);
            }
        }


        addonstest(addOns.toArray(new AddOns[0]));

    }

    private static void addonstest(AddOns ... addOns){
        List<AddOns> addOnsList = createAddOnsList(addOns);
        System.out.println(addOnsList);
    }

    private static List<AddOns> createAddOnsList(AddOns[] addOns){

        Set<AddOns> uniqueAddOnsSet = new HashSet<>();

        for (AddOns addOn : addOns) {
            if (!uniqueAddOnsSet.add(addOn)) {
                throw new DuplicateAddOnsException("Duplirani dodatak: " + addOn);
            }
        }

        return new ArrayList<>(uniqueAddOnsSet);
    }



}