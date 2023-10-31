package rs.raf.classes;

import java.util.Date;

/**
 * Klasa Term ( termin ) koja sadrzi podatke o konkretnom terminu za predavanje
 * sadrzi ucionicu sa svim njenim podacima, pocetni sat, datum
 * Ima find funkciju koja vraca odredjeni da li termin ima neki od prosledjenih parametara
 */
public class Term {

    private Classroom classroom;
    private int startTime;
    private Date date;

    public Term(Classroom classroom, int startTime, Date date) {
        this.classroom = classroom;
        this.startTime = startTime;
        this.date = date;
    }

    public boolean findTerm(Classroom classroom, int startTime, Date date){
        boolean isClassroomNotNull = (classroom != null);
        boolean isStartTimeValid = (startTime >= 0);
        boolean isDateNotNull = (date != null);

        if(isClassroomNotNull){
            if(!this.classroom.equals(classroom)){
                return false;
            }
        }
        if(isStartTimeValid){
            if(this.startTime != startTime){
                return false;
            }
        }
        if(isDateNotNull){
            if(this.date != date){
                return false;
            }
        }


        return true;
    }
}
