package rs.raf.schedule_management;

import rs.raf.classes.ClassLecture;
import rs.raf.classes.Classroom;
import rs.raf.classes.Schedule;
import rs.raf.classes.Term;
import rs.raf.enums.AddOns;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClassSchedule {

    //TODO: dodati konkretan exeption na sve

    /**
     * Inicijalizuje novi raspored
     * @param name // naziv novog rasporeda
     * @param classrooms // lista ucionica skole
     * @param startDate // datum od pocetka rasporeda
     * @param toDate // datum od kraja rasporeda
     * @param fromHours // radni sati od kojih pocinje nastava
     * @param toHours // radni sati do kojih traje nastava
     */
    // TODO: noClassroomException, datesException, hoursException;
    Schedule initializeSchedule(String name,List<Classroom> classrooms, Date startDate, Date toDate, int fromHours, int toHours);

    /**
     * Kreira novu ucionicu
     * @param name // naziv ucionice
     * @param capacity // broj mesta u ucionici
     * @param projector // boolean da li ucionica sadrzi projektor
     * @param computers // boolean da li ucionica sadrzi racunare
     * @param whiteboard // boolean da li ucionica sadrzi pisacu tablu
     * @param pen // boolean da li ucionica sadrzi olovku
     */
    Classroom CreateClassroom(String name, int capacity, boolean projector, boolean computers, boolean whiteboard, boolean pen);

    /**
     * Kreira novu ucionicu
     * @param name // naziv ucionice
     * @param capacity // broj mesta u ucionici
     * @param addOns // dodaci koje ucionica ima ( projector, computers, pen )
     */
    Classroom CreateClassroom(String name, int capacity, AddOns ... addOns);
    // TODO na test-u cuvati ovo u listi
    //  capacityException, mozda previseAdons ili duplirani, sameNameExc


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
