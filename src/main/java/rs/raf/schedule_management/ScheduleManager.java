package rs.raf.schedule_management;

public class ScheduleManager {

    /**
     * Sluzi za setovanje trenutnog aktivnog tipa skladista
     */
    private static ClassSchedule classSchedule;

    /**
     * Postavlja konkretnu implementaciju za pravljenje rasporeda
     * @param cs konkretna implementacija interfejsa ClassSchedule
     */
    public static void registerClassScheduler(ClassSchedule cs){
        classSchedule = cs;
    }

    public static ClassSchedule getClassScheduler(){
        return classSchedule;
    }


}
