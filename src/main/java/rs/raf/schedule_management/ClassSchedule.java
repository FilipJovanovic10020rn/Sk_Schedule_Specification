package rs.raf.schedule_management;

import rs.raf.classes.ClassLecture;
import rs.raf.classes.Classroom;
import rs.raf.enums.AddOns;

import java.util.Date;
import java.util.Optional;

public interface ClassSchedule {

    //TODO: dodati konkretan exeption na sve

    /**
     * Inicijalizuje novi raspored
     * @param name // naziv novog rasporeda
     * @param startDate // datum od pocetka rasporeda
     * @param toDate // datum od kraja rasporeda
     * @param fromHours // radni sati od kojih pocinje nastava
     * @param toHours // radni sati do kojih traje nastava
     */
    // TODO: promeniti da ovo vraca objekat klase a ne da je void
    //TODO: Dodati novu klasu Termin koja ima Vreme, Datum, Ucionicu i ona je kljuc za mapu
    //  objekat Schedule treba da bude mapa u mapi Map<Datum, Map<Vreme (mozda ovde da se doda i ucionica?) , Cas (podaci o predavanju)>>
    void initializeSchedule(String name, Date startDate, Date toDate, int fromHours, int toHours);


    // TODO: odabrati jednu od dve ja sam vise za 2. ali dogovor

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

    //TODO: mozda dodati edit classroom ili remove



    // TODO: mozda dodati grupe koje slusaju i tip predavanja ( vezbe ili pred )
    // TODO: mozda ubaciti i sceddule mapu ako radimo da bude void
    //  ako ne onda pri kreiranju moramo pozvati ili novu metodu add classToSchedule koja ce da primi taj i da provali gde i kako da ga doda
    //  ili da kazemo da se to radi u test aplikaciji sto mislim da nije dobro
    //
    /**
     * Kreira novi cas
     * @param startTime // pocetak predavanja
     * @param endTime // kraj predavanja  ( ovde mozemo da stavimo mozda i trajanje? )
     * @param classroomName // naziv ucionice u kojoj je predavanje
     * @param lectureName // naziv predavanja ( opciono )
     * @param professor // ime profesora ( opciono )
     * @param fromDate // datum predavanja ili pocetni datum predavanja za predavanja koja se ponavaljaju
     * @param toDate // datum do kada traju predavanja koja se ponavaljaju ili null
     */
    ClassLecture createClass(String startTime, String endTime, String classroomName,
                          Optional<String> lectureName, Optional<String> professor, Date fromDate, Date toDate);

    // TODO iznad optional jer je tako nesto spomenula ali moze sta god
    ClassLecture createClass(String startTime, String endTime, String classroomName,
                             String lectureName, String professor, Date fromDate, Date toDate);



    /**
     * Brise cas iz rasporeda
     * @param date // datum predavanja
     * @param startTime // pocetak predavanja
     * @param classroomName // naziv ucionice u kojoj je predavanje
     * @param lectureName // naziv predavanja ( opciono )
     */
    void RemoveClass(Date date, int startTime, String classroomName, String lectureName); // TODO mozda lecture name da bude sigurnije

    /**
     * Brise cas iz rasporeda
     * @param oldDate // datum predavanja koji hocemo da promenimo
     * @param oldStartTime // pocetak predavanja koji hocemo da promenimo
     * @param oldClassroomName // naziv ucionice predavanja koji hocemo da promenimo
     * @param lectureName // naziv predavanja ( opciono )
     * @param newDate // novi datum predavanja ili null ako ne zelimo da promenimo datum
     * @param newStartTime // novi pocetak predavanja ili null ako ne zelimo da promenimo pocetak
     * @param newClassroomName // nova ucionica za predavanje ili null ako ne zelimo da promenimo ucionicu
     */
    // TODO mozda staviti da budu opcione stvari za novo ako je null ne menja se
    void RescheduleClass(Date oldDate, int oldStartTime, String oldClassroomName, String lectureName,
                         Date newDate, int newStartTime, String newClassroomName);


    // TODO ovo prekopirati sa predavanja
    void exportCSV();
    void importCSV();
    void exportFile();
    void importFile();
    void exportJSON();
    void importJSON();


}
