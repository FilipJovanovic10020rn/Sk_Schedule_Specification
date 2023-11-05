package rs.raf.schedule_management;

import rs.raf.classes.ClassLecture;
import rs.raf.classes.Classroom;
import rs.raf.classes.Schedule;
import rs.raf.classes.Term;
import rs.raf.enums.AddOns;

import java.util.*;

public interface ClassSchedule {

    //TODO: dodati konkretan exeption na sve

    /**
     * Inicijalizuje novi raspored
     * @param name // naziv novog rasporeda
     * @param classrooms // lista ucionica skole
     * @param startDate // datum od pocetka rasporeda
     * @param toDate // datum od kraja rasporeda
     * @param fromHours // radni sati od kojih pocinje nastava 0 to 24
     * @param toHours // radni sati do kojih traje nastava
     * @return kreirani raspored
     * @throws ClassroomListEmptyException ako je lista ucionica prazna
     * @throws DatesException ako je startDate veci od toDate
     * @throws HoursException ako je fromHours veci od toHours
     */
    // TODO: noClassroomException, datesException, hoursException;
    default Schedule initializeSchedule(String name,List<Classroom> classrooms, Date startDate, Date toDate, int fromHours, int toHours){
        if(classrooms.isEmpty()){
            throw new ClassroomListEmptyException("Lista ucionica je prazna");
        }
        if(startDate.after(toDate)){
            throw new DatesException("Startni datum : " + startDate+ " mora biti pre zavrsnog datuma: " + toDate);
        }
        if(fromHours >= toHours){
            throw new HoursException("Pocetni sati: " + fromHours+ " mora biti pre zavrsnih sati: " + toHours);
        }

        Map<Term,ClassLecture> initialMap = new HashMap<>();


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        // for each date
        while (!calendar.getTime().after(toDate)) {
            // if the date is a weekend skip
            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                continue;
            }
            // for each hour
            for(int i = fromHours; i<=toHours; i++){
                // for each classroom
                for(Classroom classroom: classrooms){
                    Term term = new Term(classroom,i,calendar.getTime());
                    initialMap.put(term,null);
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return new Schedule(name,initialMap,classrooms);
    }

    /**
     * Kreira novu ucionicu
     * @param classrooms // lista ucionica
     * @param name // naziv ucionice
     * @param capacity // broj mesta u ucionici
     * @param addOns // dodaci koje ucionica ima ( projector, computers, pen )
     * @return kreiranu ucionicu
     * @throws SameNameException ako vec postoji ucionica sa istim imenom
     * @throws LowCapacityException ako je navedeni kapacitet manji od 1
     * @throws DuplicateAddOnsException ako postoje duplicirani dodaci
     */
    default Classroom createClassroom(List<Classroom> classrooms ,String name, int capacity, AddOns ... addOns){

        for(Classroom classroom: classrooms){
            if(classroom.getName().equals(name)){
                throw new SameNameException("Ucionica sa imenom: '" + name + "' vec postoji");
            }
        }

        if(capacity <1){
            throw new LowCapacityException("Kapacitet ucionice mora biti barem 1");
        }


        Set<AddOns> uniqueAddOnsSet = new HashSet<>();

        for (AddOns addOn : addOns) {
            if (!uniqueAddOnsSet.add(addOn)) {
                // Throw an exception if a duplicate add-on is detected
                throw new DuplicateAddOnsException("Duplirani dodatak: " + addOn);
            }
        }

        List<AddOns> addOnsList = new ArrayList<>(uniqueAddOnsSet);


        return new Classroom(name,capacity,addOnsList);
    }


    /**
     * Kreira novi cas
     * @param schedule // raspored nad kojim se radi
     * @param startTime // pocetak predavanja
     * @param duration // trajanje predavanja
     * @param classroomName // naziv ucionice u kojoj je predavanje
     * @param lectureName // naziv predavanja
     * @param professor // ime profesora
     * @param fromDate // datum predavanja ili pocetni datum predavanja za predavanja koja se ponavaljaju
     * @param toDate // datum do kada traju predavanja koja se ponavaljaju ili null
     */
    void createClass(Schedule schedule,int startTime, int duration, String classroomName,
                             String lectureName, String professor, Date fromDate, Date toDate);
    // TODO zauzetTerminExc, nePostojiTermin

    // TODO DODATI FROM I TO DATE
    /**
     * Brise cas iz rasporeda
     * @param date // datum predavanja
     * @param startTime // pocetak predavanja
     * @param classroomName // naziv ucionice u kojoj je predavanje
     * @param lectureName // naziv predavanja ( opciono )
     */
    void RemoveClass(Schedule schedule,Date date, int startTime, String classroomName, String lectureName);
    // TODO nePostojiCas

    // TODO DODATI FROM I TO DATE
    /**
     * premesta cas iz rasporeda
     * @param schedule // raspored nad kojim radimo
     * @param oldDate // datum predavanja koji hocemo da promenimo
     * @param oldStartTime // pocetak predavanja koji hocemo da promenimo
     * @param oldClassroomName // naziv ucionice predavanja koji hocemo da promenimo
     * @param lectureName // naziv predavanja ( opciono )
     * @param newDate // novi datum predavanja ili null ako ne zelimo da promenimo datum
     * @param newStartTime // novi pocetak predavanja ili null ako ne zelimo da promenimo pocetak
     * @param newClassroomName // nova ucionica za predavanje ili null ako ne zelimo da promenimo ucionicu
     */
    void RescheduleClass(Schedule schedule, Date oldDate, int oldStartTime, String oldClassroomName, String lectureName,
                         Date newDate, int newStartTime, String newClassroomName);
    // TODO zauzetTerminExc, nePostojiCas, nePostojiTermin



    /**
     * Pretraga za ucionicu po parametrima
     * @param schedule // raspored nad kojim radimo
     * @param capacity // broj mesta u ucionici ( ako nije bitan parametar proslediti -1 ili 0 )
     * @param addOns // dodaci koje ucionica ima ( projector, computers, pen )
     */
    List<Classroom> findClassrooms(Schedule schedule, int capacity, AddOns ... addOns);
    // TODO noSuchClasroom, addOnsExc, capacityExc
    /**
     * Pretraga za ucionicu po parametrima
     * @param schedule // raspored nad kojim radimo
     * @param addOns // dodaci koje ucionica ima ( projector, computers, pen )
     */
    List<Classroom> findClassrooms(Schedule schedule, AddOns ... addOns);
    // TODO noSuchClasroom, addOnsExc
    /**
     * Pretraga za ucionicu po parametrima
     * @param schedule // raspored nad kojim radimo
     * @param capacity // broj mesta u ucionici ( ako nije bitan parametar proslediti -1 ili 0 )
     */
    List<Classroom> findClassrooms(Schedule schedule, int capacity);
    // TODO noSuchClasroom, capacityExc


    // TODO OVO MOZDA IPAK NE TRBA TRUE ILI FALSE
    //  izmeniti da ne pise po parametrima nego navesti ih lepo
    /**
     * Pretraga termina po parametrima
     * @param schedule // raspored nad kojim radimo
     * @param date // datum trazenog termina
     * @param duration // trajanje trazenog termina ( duzina slobodnog termina ) ( od 12 - 15 za duration 3)
     * @param isFree // boolean true ili false u zavisnosti da li se traze slobodni termini ili zauzeti respektivno
     */
    List<Term> findTerms(Schedule schedule, Date date, int duration, boolean isFree);
    // TODO loseUnetDuration, dateOutOfBounds

    /**
     * Pretraga termina po parametrima
     * @param schedule // raspored nad kojim radimo
     * @param date // datum trazenog termina
     * @param duration // trajanje trazenog termina ( duzina slobodnog termina ) ( od 12 - 15 za duration 3)
     * @param isFree // boolean true ili false u zavisnosti da li se traze slobodni termini ili zauzeti respektivno
     * @param classroomName // naziv ucionice za trazeni termin
     */
    List<Term> findTerms(Schedule schedule, Date date, int duration, boolean isFree, String classroomName);
    // TODO loseUnetDuration, dateOutOfBounds, noSuchClasroom

    /**
     * Pretraga termina po parametrima
     * @param schedule // raspored nad kojim radimo
     * @param date // datum trazenog termina
     * @param duration // trajanje trazenog termina ( duzina slobodnog termina ) ( od 12 - 15 za duration 3)
     * @param isFree // boolean true ili false u zavisnosti da li se traze slobodni termini ili zauzeti respektivno
     * @param capacity // broj mesta u ucionici za termin
     * @param addOns // dodaci koje ucionica ima ( projector, computers, pen ) za termin
     */
    List<Term> findTerms(Schedule schedule, Date date, int duration, boolean isFree, int capacity, AddOns ... addOns);
    // TODO loseUnetDuration, dateOutOfBounds, noSuchClasroom, capacityExc, addOnsExc

    /**
     * Pretraga termina po parametrima
     * @param schedule // raspored nad kojim radimo
     * @param date // datum trazenog termina
     * @param duration // trajanje trazenog termina ( duzina slobodnog termina ) ( od 12 - 15 za duration 3)
     * @param isFree // boolean true ili false u zavisnosti da li se traze slobodni termini ili zauzeti respektivno
     * @param capacity // broj mesta u ucionici za termin
     */
    List<Term> findTerms(Schedule schedule, Date date, int duration, boolean isFree, int capacity);
    // TODO loseUnetDuration, dateOutOfBounds, noSuchClasroom, capacityExc

    /**
     * Pretraga termina po parametrima
     * @param schedule // raspored nad kojim radimo
     * @param date // datum trazenog termina
     * @param duration // trajanje trazenog termina ( duzina slobodnog termina ) ( od 12 - 15 za duration 3)
     * @param isFree // boolean true ili false u zavisnosti da li se traze slobodni termini ili zauzeti respektivno
     * @param addOns // dodaci koje ucionica ima ( projector, computers, pen ) za termin
     */
    List<Term> findTerms(Schedule schedule, Date date, int duration, boolean isFree, AddOns ... addOns);
    // TODO loseUnetDuration, dateOutOfBounds, noSuchClasroom, addOnsExc

    /**
     * Pretraga termina po parametrima
     * @param schedule // raspored nad kojim radimo
     * @param professor // ime profesora za vezane termine
     * @param isFree // boolean true ili false u zavisnosti da li se traze slobodni termini ili zauzeti respektivno
     */
    List<Term> findTerms(Schedule schedule, String professor, boolean isFree);
    // TODO professorDoesntExist

    /**
     * Pretraga termina po parametrima
     * @param schedule // raspored nad kojim radimo
     * @param className // naziv predmeta za vezane termine
     */
    List<Term> findTerms(Schedule schedule, String className);
    // TODO classNameDesntExist





    // TODO ovo prekopirati sa predavanja
    void exportCSV();
    void importCSV();
    void exportFile();
    void importFile();
    void exportJSON();
    void importJSON();


}
