package rs.raf.classes;

import java.util.Date;

public class Implementation1 {

    //    static {
//        ScheduleManager.registerClassScheduler(new Implementation1());
//    }
//    private Map<Term,ClassLecture> scheduleMap;

    void createClass(Schedule schedule, int startTime, int duration, String classroomName,
                     String lectureName, String professor, Date fromDate, Date toDate) {
        Term termin = new Term(schedule.getClassroomByName(classroomName),startTime,fromDate);

        if(schedule.getScheduleMap().get(termin)==null){
            for(int i = 1;i<duration; i++){
                Term termin2 = new Term(schedule.getClassroomByName(classroomName),startTime+i,fromDate);
                if(schedule.getScheduleMap().get(termin2)==null)
                    continue;
                else{
                    throw new SlotTakenExeption("Trazeni termin se preklapa sa postjecim");
                }
                ClassLecture cl = new ClassLecture(lectureName, professor, startTime, duration, fromDate, toDate);
                schedule.getScheduleMap().put(termin,cl);
            }
        }
    }

    void RemoveClass(Schedule schedule,Date date, int startTime, String classroomName, String lectureName){
        Term termin = new Term(schedule.getClassroomByName(classroomName),startTime,date);
        if(!schedule.getScheduleMap().containsKey(termin))
        {
            throw new TermDoesntExistException("Termin ne postoji");
        }

        ClassLecture cl = schedule.getScheduleMap().get(termin);
        int duration = cl.getDuration();

        if(cl.getStartTime()!=startTime && cl.getStartDate()!=date)
        {
            throw new WrongStartTimeException("Uneti podaci se napoklapaju sa bazom. (Vervoatno ste vreme pogresno uneli");
        }
        if(cl.getStartDate()!=date)
        {
            throw new WrongDateException("Uneti podaci se napoklapaju sa bazom. (Vervoatno ste datum pogresno uneli");
        }
        if(cl.getClassName().equals(lectureName))
        {
            throw new WrongNameException("Uneti podaci se napoklapaju sa bazom. (Vervoatno ste ime pogresno uneli");
        }

        for(int i = 1; i< duration; i++){
            schedule.getScheduleMap().remove(termin);
            termin = new Term(schedule.getClassroomByName(classroomName),startTime+i,date);
        }
    }

    void RescheduleClass(Schedule schedule, Date oldDate, int oldStartTime, String oldClassroomName, String lectureName,
                         Date newDate, int newStartTime, String newClassroomName){
        Term termin = new Term(schedule.getClassroomByName(oldClassroomName),oldStartTime,oldDate);
        if(!schedule.getScheduleMap().containsKey(termin))
        {
            throw new TermDoesntExistException("Termin ne postoji");
        }

        ClassLecture cl = schedule.getScheduleMap().get(termin);
        int duration = cl.getDuration();

        if(cl.getStartTime()!=oldStartTime && cl.getStartDate()!=oldDate)
        {
            throw new WrongStartTimeException("Uneti podaci se napoklapaju sa bazom. (Vervoatno ste vreme pogresno uneli");
        }
        if(cl.getStartDate()!=oldDate)
        {
            throw new WrongDateException("Uneti podaci se napoklapaju sa bazom. (Vervoatno ste datum pogresno uneli");
        }
        if(cl.getClassName().equals(lectureName))
        {
            throw new WrongNameException("Uneti podaci se napoklapaju sa bazom. (Vervoatno ste ime pogresno uneli");
        }


        termin = new Term(schedule.getClassroomByName(oldClassroomName),newStartTime,newDate);

        if(schedule.getScheduleMap().get(termin)==null){
            for(int i = 1;i<duration; i++){
                Term termin2 = new Term(schedule.getClassroomByName(oldClassroomName),newStartTime+i,newDate);
                if(schedule.getScheduleMap().get(termin2)==null)
                    continue;
                else{
                    throw new SlotTakenExeption("Trazeni termin se preklapa sa postjecim");
                }
                ClassLecture cl2 = new ClassLecture(lectureName, cl.getProfessor(), newStartTime, duration, newDate, cl.getEndDate());
                schedule.getScheduleMap().put(termin,cl);
            }
        }

    }
}
