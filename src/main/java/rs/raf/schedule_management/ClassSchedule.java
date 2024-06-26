package rs.raf.schedule_management;

import com.opencsv.CSVWriter;
import rs.raf.classes.ClassLecture;
import rs.raf.classes.Classroom;
import rs.raf.classes.Schedule;
import rs.raf.classes.Term;
import rs.raf.enums.AddOns;
import rs.raf.exceptions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public interface ClassSchedule {

    /**
     * Inicijalizuje novi raspored
     *
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
    default Schedule initializeSchedule(String name,List<Classroom> classrooms, Date startDate, Date toDate, int fromHours, int toHours)
    throws ClassroomListEmptyException,DatesException,HoursException {
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
            // todo ovo sam =
            for(int i = fromHours; i<toHours; i++){
                // for each classroom
                for(Classroom classroom: classrooms){
                    Term term = new Term(classroom,i,calendar.getTime());
                    // creates empty map values
                    initialMap.put(term,null);
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return new Schedule(name,initialMap,classrooms,startDate,toDate,fromHours,toHours);
    }

    /**
     * Kreira novu ucionicu i dodaje je u listu
     *
     * @param classrooms // lista ucionica
     * @param name // naziv ucionice
     * @param capacity // broj mesta u ucionici
     * @param addOns // dodaci koje ucionica ima ( projector, computers, pen )
     * @throws SameNameException ako vec postoji ucionica sa istim imenom
     * @throws LowCapacityException ako je navedeni kapacitet manji od 1
     * @throws DuplicateAddOnsException ako postoje duplicirani dodaci
     */
    default void createClassroom(List<Classroom> classrooms ,String name, int capacity, AddOns ... addOns)
        throws SameNameException,LowCapacityException,DuplicateAddOnsException {

        for(Classroom classroom: classrooms){
            if(classroom.getName().equals(name)){
                throw new SameNameException("Ucionica sa imenom: '" + name + "' vec postoji");
            }
        }

        if(capacity <1){
            throw new LowCapacityException("Kapacitet ucionice mora biti barem 1");
        }

        List<AddOns> addOnsList = createAddOnsList(addOns);

        classrooms.add(new Classroom(name,capacity,addOnsList));
    }

    /**
     * kreira listu dodataka
     *
     * @param addOns // niz dodataka
     * @return lista bez ponavaljanja
     * @throws DuplicateAddOnsException ako ima duplikata
     */
    private List<AddOns> createAddOnsList(AddOns[] addOns){

        if (addOns == null) {
            return Collections.emptyList();
        }

        Set<AddOns> uniqueAddOnsSet = new HashSet<>();

        for (AddOns addOn : addOns) {
            if (!uniqueAddOnsSet.add(addOn)) {
                throw new DuplicateAddOnsException("Duplirani dodatak: " + addOn);
            }
        }

        return new ArrayList<>(uniqueAddOnsSet);
    }


    /**
     * Kreira novi cas
     *
     * @param schedule // raspored nad kojim se radi
     * @param startTime // pocetak predavanja
     * @param duration // trajanje predavanja
     * @param classroomName // naziv ucionice u kojoj je predavanje
     * @param lectureName // naziv predavanja
     * @param professor // ime profesora
     * @param fromDate // datum predavanja ili pocetni datum predavanja za predavanja koja se ponavaljaju
     * @param toDate // datum do kada traju predavanja koja se ponavaljaju ili null
     * @throws DatesException ako su datumi van rasporeda ili su prosledjeni pogresnim redosledom
     * @throws DurationException ako je trajanje manje od 1
     * @throws WrongStartTimeException ako je vreme van radnog vremena
     * @throws InternalError ako je doslo do greske vezane za schedule
     * @throws TermTakenException ako je termin vec zauzet
     * @throws ClassroomDoesntExistException ako ucionica ne postoji
     */
    void createClass(Schedule schedule,int startTime, int duration, String classroomName,
                             String lectureName, String professor, Date fromDate, Date toDate)
            throws DatesException,DurationException,ClassroomDoesntExistException,TermTakenException,WrongStartTimeException, InternalError;

    /**
     * Brise cas iz rasporeda
     *
     * @param fromDate // datum predavanja
     * @param toDate // datum do kog traju predavanja ili null ako je predavanje samo jednog dana
     * @param startTime // pocetak predavanja
     * @param classroomName // naziv ucionice u kojoj je predavanje
     * @param lectureName // naziv predavanja ( opciono )
     * @throws DatesException ako su datumi van rasporeda ili su prosledjeni pogresnim redosledom
     * @throws WrongStartTimeException ako je pocetak predavanja ( startTime ) pogresno unesen
     * @throws WrongDateException ako je datum predavanja ( fromDate ) pogresno unesen
     * @throws WrongLectureNameException ako je naziv predavanja ( lectureName ) pogresno unesen
     * @throws ClassroomDoesntExistException ako ucionica ne postoji
     * @throws ClassLectureDoesntExistException ako predmet sa tim imenom ne postoji
     */
    void removeClass(Schedule schedule,Date fromDate,Date toDate, int startTime, String classroomName, String lectureName)
        throws WrongStartTimeException,DatesException, WrongDateException,WrongLectureNameException, ClassroomDoesntExistException, ClassLectureDoesntExistException;

    /**
     * Premesta cas iz rasporeda
     *
     * @param schedule // raspored nad kojim radimo
     * @param oldFromDate // datum do kog traju predavanja koje hocemo da promenimo ili null ako je predavanje samo jednog dana
     * @param oldToDate // datum do kada  predavanja koji hocemo da promenimo
     * @param oldStartTime // pocetak predavanja koji hocemo da promenimo
     * @param oldClassroomName // naziv ucionice predavanja koji hocemo da promenimo
     * @param lectureName // naziv predavanja ( opciono )
     * @param newFromDate // novi datum predavanja ili null ako ne zelimo da promenimo datum
     * @param newToDate // datum do kada traju predavanja koja se ponavaljaju ili null ako je predavanje samo jednog dana
     * @param newStartTime // novi pocetak predavanja ili null ako ne zelimo da promenimo pocetak
     * @param newClassroomName // nova ucionica za predavanje ili null ako ne zelimo da promenimo ucionicu
     * @throws DatesException ako su datumi van rasporeda ili su prosledjeni pogresnim redosledom
     * @throws WrongStartTimeException ako je vreme van radnog vremena
     * @throws WrongDateException ako je datum predavanja ( fromDate ) pogresno unesen
     * @throws WrongLectureNameException ako je naziv predavanja ( lectureName ) pogresno unesen
     * @throws ClassroomDoesntExistException ako ucionica ne postoji
     * @throws TermTakenException ako je termin vec zauzet
     * @throws WrongClassroomNameException ako je ucionica vezana za termin pogresno unesena
     */
    void rescheduleClass(Schedule schedule, Date oldFromDate,Date oldToDate, int oldStartTime, String oldClassroomName, String lectureName,
                         Date newFromDate,Date newToDate, int newStartTime, String newClassroomName)
        throws DatesException,ClassroomDoesntExistException,WrongStartTimeException,WrongDateException,WrongLectureNameException,WrongClassroomNameException, TermTakenException;



    /**
     * Pretraga za ucionicu po kapacitetu i dodacima
     *
     * @param schedule // raspored nad kojim radimo
     * @param capacity // broj mesta u ucionici
     * @param addOns // dodaci koje ucionica ima ( projector, computers, pen )
     * @return lista ucionica koje se poklapaju sa brojem mesta i dodacima
     * @throws ClassroomDoesntExistException ako ucionica ne postoji
     * @throws LowCapacityException ako je navedeni kapacitet manji od 1
     * @throws DuplicateAddOnsException ako postoje duplicirani dodaci
     */
    default List<Classroom> findClassrooms(Schedule schedule, int capacity, AddOns... addOns)
        throws ClassroomDoesntExistException, DuplicateAddOnsException, LowCapacityException{
        List<Classroom> classroomsToReturn = new ArrayList<>();

        if(capacity < 1){
            throw new LowCapacityException("Kapacitet mora biti minimum 1");
        }

        List<AddOns> addOnsList = createAddOnsList(addOns);

        for(Classroom classroom :schedule.getClassrooms()){
            // trazimo ucionice ne sa tacnim kapacitetom vec sa minimum tim kapacitetom
            if(classroom.getCapacity() >= capacity){
                if(!addOnsList.isEmpty()){
                    // proverava da li ima sve koje trazi ( ako ima neke pored njih proci ce)
                    if(classroom.hasAddOns(addOnsList)){
                        classroomsToReturn.add(classroom);
                    }
                }
                // ako nema addons onda znaci da nema ogranicenja te ubacujemo
                else{
                    classroomsToReturn.add(classroom);
                }
            }
        }
        if(classroomsToReturn.isEmpty()){
            throw new ClassroomDoesntExistException("Ne postoji ni jedna ucionica sa trazenim parametrima");
        }


        return  classroomsToReturn;
    }
    /**
     * Pretraga za ucionicu po dodacima
     *
     * @param schedule // raspored nad kojim radimo
     * @param addOns // dodaci koje ucionica ima ( projector, computers, pen )
     * @return lista ucionica koje se poklapaju sa  dodacima
     * @throws ClassroomDoesntExistException ako ucionica ne postoji
     * @throws DuplicateAddOnsException ako postoje duplicirani dodaci
     */
    default List<Classroom> findClassrooms(Schedule schedule, AddOns... addOns)
            throws ClassroomDoesntExistException, DuplicateAddOnsException{
        List<Classroom> classroomsToReturn = new ArrayList<>();

        List<AddOns> addOnsList = createAddOnsList(addOns);

        for(Classroom classroom :schedule.getClassrooms()){
            if(!addOnsList.isEmpty()){
                // proverava da li ima sve koje trazi ( ako ima neke pored njih proci ce)
                if(classroom.hasAddOns(addOnsList)){
                    classroomsToReturn.add(classroom);
                }
            }
            // ako nema addons onda znaci da nema ogranicenja te ubacujemo
            else{
                classroomsToReturn.add(classroom);
            }
        }
        if(classroomsToReturn.isEmpty()){
            throw new ClassroomDoesntExistException("Ne postoji ni jedna ucionica sa trazenim parametrima");
        }


        return  classroomsToReturn;
    }
    /**
     * Pretraga za ucionicu po kapacitetu
     *
     * @param schedule // raspored nad kojim radimo
     * @param capacity // broj mesta u ucionici
     * @return lista ucionica koje se poklapaju sa brojem mesta
     * @throws ClassroomDoesntExistException ako ucionica ne postoji
     * @throws LowCapacityException ako je navedeni kapacitet manji od 1
     */
    default List<Classroom> findClassrooms(Schedule schedule, int capacity)
            throws ClassroomDoesntExistException, LowCapacityException{
        List<Classroom> classroomsToReturn = new ArrayList<>();

        if(capacity < 1){
            throw new LowCapacityException("Kapacitet mora biti minimum 1");
        }



        for(Classroom classroom :schedule.getClassrooms()){
            // trazimo ucionice ne sa tacnim kapacitetom vec sa minimum tim kapacitetom
            if(classroom.getCapacity() >= capacity){
                classroomsToReturn.add(classroom);
            }
        }
        if(classroomsToReturn.isEmpty()){
            throw new ClassroomDoesntExistException("Ne postoji ni jedna ucionica sa trazenim parametrima");
        }


        return  classroomsToReturn;
    }

    /**
     * Pretraga termina po datumu i trajanju
     *
     * @param schedule // raspored nad kojim radimo
     * @param date // datum trazenog termina
     * @param duration // trajanje trazenog termina ( duzina slobodnog termina ) ( od 12 - 15 za duration 3)
     * @return lista termina koji zadovoljavaju kriterijume pretrage
     * @throws DatesException ako je datum van datuma rasporeda
     * @throws DurationException ako je trajanje manje od 1
     * @throws TermDoesntExistException ako ne postoji ni jedan termin pretrage
     */
    default List<Term> findTerms(Schedule schedule, Date date, int duration)
        throws DatesException,DurationException,TermDoesntExistException{

        if(date.before(schedule.getStartDate() ) || date.after(schedule.getEndDate())){
            throw new DatesException("Datum termina mora biti od: "+ schedule.getStartDate() + " do " + schedule.getEndDate());
        }

        if(duration<1){
            throw new DurationException("Trajanje mora biti minimum 1");
        }

        List<Term> termsToReturn = new ArrayList<>();

        for(Map.Entry<Term,ClassLecture> entry : schedule.getScheduleMap().entrySet()) {
            // checks if the date is this date
            if(entry.getKey().getDate().equals(date)) {
                // goes through the map again and checks if the entry(term) is free
                isTermFreeAndAddToList(schedule, duration, termsToReturn, entry);
            }
        }

        if(termsToReturn.isEmpty()){
            throw new TermDoesntExistException("Ne postoji ni jedan termin datuma: " + date + " za trajanje: "+duration);
        }

        return termsToReturn;
    }

    /**
     * Proverava da li je slobodan termin i dodaje ga u listu
     *
     * @param schedule // raspored nad kojim se radi
     * @param duration // trajanje casa
     * @param termList // lista termina u koju se dodaje validan termin
     * @param entry // jedan element u mapi
     */
    private void isTermFreeAndAddToList(Schedule schedule, int duration, List<Term> termList, Map.Entry<Term, ClassLecture> entry){
        if(entry.getValue()==null){
            boolean isTermValid = entry.getKey().getStartTime() + duration <= schedule.getEndHours();
            // checks if the final time would go out of bounds
            for(int i =1;i<duration;i++){
                if(!isTermValid){
                    break;
                }
                Term nextTerm = new Term(entry.getKey().getClassroom(),entry.getKey().getStartTime()+i,entry.getKey().getDate());
                // finds the next term and checks if it's not taken
                for(Map.Entry<Term,ClassLecture> entry1 : schedule.getScheduleMap().entrySet()) {
                    if(entry1.getKey().getDate().equals(nextTerm.getDate())
                            && entry1.getKey().getClassroom().equals(nextTerm.getClassroom())
                            && entry1.getKey().getStartTime() == nextTerm.getStartTime()){
                        if (entry1.getValue() != null) {
                            isTermValid = false;
                        }
                        break;
                    }
                }
            }
            if(isTermValid){
                termList.add(entry.getKey());
            }
        }
    }


    /**
     * Pretraga termina po datumu, trajanju i nazivu ucionice
     *
     * @param schedule // raspored nad kojim radimo
     * @param date // datum trazenog termina
     * @param duration // trajanje trazenog termina ( duzina slobodnog termina ) ( od 12 - 15 za duration 3)
     * @param isFree // boolean true ili false u zavisnosti da li se traze slobodni termini ili zauzeti respektivno
     * @param classroomName // naziv ucionice za trazeni termin
     * @return lista termina koji zadovoljavaju kriterijume pretrage u zavisnosti od isFree
     * @throws DatesException ako je datum van datuma rasporeda
     * @throws DurationException ako je trajanje manje od 1
     * @throws ClassroomDoesntExistException ako ucionica sa datim imenom ne postoji
     */
    default List<Term> findTerms(Schedule schedule, Date date, int duration, boolean isFree, String classroomName)
        throws DurationException,DatesException,ClassroomDoesntExistException{
        if(date.before(schedule.getStartDate() ) || date.after(schedule.getEndDate())){
            throw new DatesException("Datum termina mora biti od: "+ schedule.getStartDate() + " do " + schedule.getEndDate());
        }

        if(duration<1){
            throw new DurationException("Trajanje mora biti minimum 1");
        }

        Classroom classroom = schedule.getClassroomByName(classroomName);

        if(classroom == null){
            throw new ClassroomDoesntExistException("Ne postoji ucionica sa imenom: "+ classroomName);
        }

        List<Term> termsToReturn = new ArrayList<>();

        // goes through all in map
        for(Map.Entry<Term,ClassLecture> entry : schedule.getScheduleMap().entrySet()){
            // if the term is that date and is one of valid classroms
            if(entry.getKey().getDate().equals(date) && entry.getKey().getClassroom().equals(classroom)) {
                // if it's asked to find free terms
                if(isFree){
                    // goes through the map again and checks if the entry(term) is free
                    isTermFreeAndAddToList(schedule,duration,termsToReturn,entry);
                }
                else{
                    if(entry.getValue() != null){
                        if(entry.getValue().getStartTime() == entry.getKey().getStartTime()
                        && duration == entry.getValue().getDuration())
                            termsToReturn.add(entry.getKey());
                    }
                }
            }
        }
        // todo mozda exception da ne postoji ni jedan tj lista je prazna?
        return termsToReturn;


    }

    /**
     * Pretraga termina po datumu, trajanju, kapacitetu ucionice i dodacima za ucionicu
     *
     * @param schedule // raspored nad kojim radimo
     * @param date // datum trazenog termina
     * @param duration // trajanje trazenog termina ( duzina slobodnog termina ) ( od 12 - 15 za duration 3)
     * @param isFree // boolean true ili false u zavisnosti da li se traze slobodni termini ili zauzeti respektivno
     * @param capacity // broj mesta u ucionici za termin
     * @param addOns // dodaci koje ucionica ima ( projector, computers, pen ) za termin
     * @return lista termina koji zadovoljavaju kriterijume pretrage u zavisnosti od isFree
     * @throws DatesException ako je datum van datuma rasporeda
     * @throws DurationException ako je trajanje manje od 1
     * @throws LowCapacityException ako ne postoji ucionica sa kapacitetom
     * @throws ClassroomDoesntExistException ako ucionica ne postoji
     * @throws DuplicateAddOnsException ako postoje duplicirani dodaci
     */
    default List<Term> findTerms(Schedule schedule, Date date, int duration, boolean isFree, int capacity, AddOns... addOns)
        throws DurationException,DatesException,ClassroomDoesntExistException,LowCapacityException,DuplicateAddOnsException{

        ThrowExceptionIfNeeded(schedule, date, duration, capacity);

        List<AddOns> addOnsList = createAddOnsList(addOns);

        List<Classroom> validClassrooms = new ArrayList<>();

        for(Classroom classroom : schedule.getClassrooms()){
            if(classroom.getCapacity() >= capacity){
                if(classroom.hasAddOns(addOnsList)){
                    validClassrooms.add(classroom);
                }
            }
        }

        if(validClassrooms.isEmpty()){
            throw new ClassroomDoesntExistException("Ne postoji ucionica sa ovim parametrima");
        }

        List<Term> termsToReturn = new ArrayList<>();

        // goes through all in map
        for(Map.Entry<Term,ClassLecture> entry : schedule.getScheduleMap().entrySet()){
            // if the term is that date and is one of valid classrooms
            if(entry.getKey().getDate().equals(date) && validClassrooms.contains(entry.getKey().getClassroom())) {
                // if it's asked to find free terms
                if(isFree){
                    // goes through the map again and checks if the entry(term) is free
                    isTermFreeAndAddToList(schedule, duration, termsToReturn, entry);
                }
                else{
                    if(entry.getValue() != null){
                        if(entry.getValue().getStartTime() == entry.getKey().getStartTime()
                                && duration == entry.getValue().getDuration())
                        termsToReturn.add(entry.getKey());
                    }
                }
            }
        }
        // todo mozda exception da ne postoji ni jedan tj lista je prazna?
        return termsToReturn;
    }

    /**
     * Proverava da li su podaci validni i baca odgovarajuce greske
     *
     * @param schedule // raspored nad kojim se radi
     * @param date // datum
     * @param duration // trajanje
     * @param capacity // kapacitet
     * @throws DatesException ako je datum van rasporeda
     * @throws DurationException ako je trajanje manje od 1
     * @throws LowCapacityException ako je kapacitet manji od 1
     */
    private void ThrowExceptionIfNeeded(Schedule schedule, Date date, int duration, int capacity) {
        if(date.before(schedule.getStartDate() ) || date.after(schedule.getEndDate())){
            throw new DatesException("Datum termina mora biti od: "+ schedule.getStartDate() + " do " + schedule.getEndDate());
        }

        if(duration<1){
            throw new DurationException("Trajanje mora biti minimum 1");
        }

        if(capacity<1){
            throw new LowCapacityException("Kapacitet mora biti minimum 1");
        }
    }

    /**
     * Pretraga termina po datumu, trajanju i kapacitetu ucionice
     *
     * @param schedule // raspored nad kojim radimo
     * @param date // datum trazenog termina
     * @param duration // trajanje trazenog termina ( duzina slobodnog termina ) ( od 12 - 15 za duration 3)
     * @param isFree // boolean true ili false u zavisnosti da li se traze slobodni termini ili zauzeti respektivno
     * @param capacity // broj mesta u ucionici za termin
     * @return lista termina koji zadovoljavaju kriterijume pretrage u zavisnosti od isFree
     * @throws DatesException ako je datum van datuma rasporeda
     * @throws DurationException ako je trajanje manje od 1
     * @throws LowCapacityException ako ne postoji ucionica sa kapacitetom
     * @throws ClassroomDoesntExistException ako ucionica ne postoji
     */
    default List<Term> findTerms(Schedule schedule, Date date, int duration, boolean isFree, int capacity)
        throws DurationException, DatesException, LowCapacityException, ClassroomDoesntExistException{

        ThrowExceptionIfNeeded(schedule, date, duration, capacity);

        List<Classroom> validClassrooms = new ArrayList<>();

        for(Classroom classroom : schedule.getClassrooms()){
            if(classroom.getCapacity() >= capacity){
                validClassrooms.add(classroom);
            }
        }

        if(validClassrooms.isEmpty()){
            throw new ClassroomDoesntExistException("Ne postoji ucionica sa ovim parametrima");
        }

        List<Term> termsToReturn = new ArrayList<>();

        // goes through all in map
        for(Map.Entry<Term,ClassLecture> entry : schedule.getScheduleMap().entrySet()){
            // if the term is that date and is one of valid classrooms
            if(entry.getKey().getDate().equals(date) && validClassrooms.contains(entry.getKey().getClassroom())) {
                // if it's asked to find free terms
                if(isFree){
                    // goes through the map again and checks if the entry(term) is free
                    isTermFreeAndAddToList(schedule, duration, termsToReturn, entry);
                }
                else{
                    if(entry.getValue() != null){
                        if(entry.getValue().getStartTime() == entry.getKey().getStartTime()
                                && duration == entry.getValue().getDuration())
                            termsToReturn.add(entry.getKey());
                    }
                }
            }
        }
        // todo mozda exception da ne postoji ni jedan tj lista je prazna?
        return termsToReturn;
    }

    /**
     * Pretraga termina po dodacima ucionice, datumu i trajanju
     *
     * @param schedule // raspored nad kojim radimo
     * @param date // datum trazenog termina
     * @param duration // trajanje trazenog termina ( duzina slobodnog termina ) ( od 12 - 15 za duration 3)
     * @param isFree // boolean true ili false u zavisnosti da li se traze slobodni termini ili zauzeti respektivno
     * @param addOns // dodaci koje ucionica ima ( projector, computers, pen ) za termin
     * @return lista termina koji zadovoljavaju kriterijume pretrage u zavisnosti od isFree
     * @throws DatesException ako je datum van datuma rasporeda
     * @throws DurationException ako je trajanje manje od 1
     * @throws ClassroomDoesntExistException ako ucionica sa trazenim dodacima ne postoji
     * @throws DuplicateAddOnsException ako postoje duplicirani dodaci
     */
    default List<Term> findTerms(Schedule schedule, Date date, int duration, boolean isFree, AddOns... addOns)
        throws DatesException,DurationException,ClassroomDoesntExistException,DuplicateAddOnsException{

        if(date.before(schedule.getStartDate() ) || date.after(schedule.getEndDate())){
            throw new DatesException("Datum termina mora biti od: "+ schedule.getStartDate() + " do " + schedule.getEndDate());
        }

        if(duration<1){
            throw new DurationException("Trajanje mora biti minimum 1");
        }

        List<AddOns> addOnsList = createAddOnsList(addOns);

        List<Classroom> validClassrooms = new ArrayList<>();

        for(Classroom classroom : schedule.getClassrooms()){
            if(classroom.hasAddOns(addOnsList)){
                validClassrooms.add(classroom);
            }
        }

        if(validClassrooms.isEmpty()){
            throw new ClassroomDoesntExistException("Ne postoji ucionica sa ovim parametrima");
        }

        List<Term> termsToReturn = new ArrayList<>();

        // goes through all in map
        for(Map.Entry<Term,ClassLecture> entry : schedule.getScheduleMap().entrySet()){
                // if the term is that date and is one of valid classrooms
                if(entry.getKey().getDate().equals(date) && validClassrooms.contains(entry.getKey().getClassroom())) {
                    // if it's asked to find free terms
                    if(isFree){
                        // goes through the map again and checks if the entry(term) is free
                        isTermFreeAndAddToList(schedule, duration, termsToReturn, entry);
                    }
                    else{
                        if(entry.getValue() != null){
                            if(entry.getValue().getStartTime() == entry.getKey().getStartTime()
                                    && duration == entry.getValue().getDuration())
                                termsToReturn.add(entry.getKey());
                        }
//                            boolean isTermValid = true;
                            // todo ovo mislim da ne treba kada se trazi zauzeto jer nas ne zanima duzina koliko je zauzeto samo kada
                            // checks if the final time would go out of bounds
//                            if(entry.getKey().getStartTime()+duration > schedule.getEndHours()){
//                                isTermValid = false;
//                            }
//
//                            // goes through the duration
//                            for(int i = 1; i<duration; i++){
//                                // no need to go through all if its already broken
//                                if(isTermValid == false){
//                                    break;
//                                }
//                                // checks if the next time would go out of bounds
//                                if(entry.getKey().getStartTime()+i > schedule.getEndHours()){
//                                    isTermValid = false;
//                                    break;
//                                }
//                                Term nextTerm = new Term(entry.getKey().getClassroom(),entry.getKey().getStartTime()+i,date);
//
//                                // finds the next term and checks if its taken
//                                for(Map.Entry<Term,ClassLecture> entry1 : schedule.getScheduleMap().entrySet()) {
//                                    if(entry1.getKey().isTermTheSame(nextTerm.getClassroom(), nextTerm.getStartTime(), date)){
//                                        if(entry1.getValue()==null){
//                                            isTermValid = false;
//                                            break;
//                                        }
//                                        else{
//                                            break;
//                                        }
//                                    }
//                                }
//                            }

//                            if(isTermValid){
//                                termsToReturn.add(entry.getKey());
//                            }
//                        }
                    }
                }
            }
        // todo mozda exception da ne postoji ni jedan tj lista je prazna?
        return termsToReturn;
    }

    /**
     * Pretraga termina kada je dati profesor zauzet ili slobodan
     *
     * @param schedule // raspored nad kojim radimo
     * @param fromDate // tacan datum ili pocetni datum od kada radimo pretragu
     * @param toDate // null ili zavrsni datum pretrage
     * @param professor // ime profesora za vezane termine
     * @param isFree // boolean true ili false u zavisnosti da li se traze slobodni termini ili zauzeti respektivno
     * @return lista termina koji zadovoljavaju kriterijume pretrage u zavisnosti od isFree
     * @throws ProfessorDoesntExistException ako profesor ne postoji
     * @throws DatesException ako datumi izlaze van datuma rasporeda ili su napisani pogresnim redosledom (fromDate>toDate)
     */
    default List<Term> findTerms(Schedule schedule,Date fromDate, Date toDate , String professor, boolean isFree)
        throws ProfessorDoesntExistException,DatesException{

        if(fromDate.before(schedule.getStartDate() ) || fromDate.after(schedule.getEndDate())){
            throw new DatesException("Datum pocetka mora biti od: "+ schedule.getStartDate() + " do " + schedule.getEndDate());
        }
        if(toDate != null){
            if(toDate.before(schedule.getStartDate()) || toDate.after(schedule.getEndDate())){
                throw new DatesException("Datum zavrsetka mora biti od: "+ schedule.getStartDate() + " do " + schedule.getEndDate());
            }
            if(toDate.before(fromDate)){
                throw new DatesException("Datum zavrsetka mora biti posle datuma pocetka");
            }
        }

        List<Term> notFreeTermList = new ArrayList<>();

        for(Map.Entry<Term,ClassLecture> entry : schedule.getScheduleMap().entrySet()){
            if(entry.getKey().getDate().equals(fromDate)){
                if(entry.getValue()!= null && entry.getValue().getProfessor().equals(professor)){
                    notFreeTermList.add(entry.getKey());
                }
            }
            else if(toDate != null){
                if(entry.getKey().getDate().after(fromDate) && !entry.getKey().getDate().after(toDate)){
                    if(entry.getValue()!= null && entry.getValue().getProfessor().equals(professor)){
                        notFreeTermList.add(entry.getKey());
                    }
                }
            }

        }
        if(notFreeTermList.isEmpty()){
            throw new ProfessorDoesntExistException("Profesor sa imenom: " +professor + " ne postoji");
        }
        if(!isFree){
            // lista zauzetih termina
            return notFreeTermList;
        }
        //////////////////// dovde idu zauzeti

        List<Term> termFreeList = new ArrayList<>();


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);

        Date iterateToDate;
        if(toDate!= null)
            iterateToDate = toDate;
        else {
            iterateToDate = fromDate;
        }

        // for each date
        while (!calendar.getTime().after(iterateToDate)) {
            // if the date is a weekend skip
            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                continue;
            }
            // for each hour
            for(int i = schedule.getStartHours(); i< schedule.getEndHours(); i++){

                boolean sameTerm = false;

                for(Term notFreeTerm : notFreeTermList){

                    if(notFreeTerm.getDate().equals(calendar.getTime()) && notFreeTerm.getStartTime() == i){
                        sameTerm = true;
                        break;
                    }
//                    if(notFreeTerm.isTermTheSame(null,i,calendar.getTime())){
//                        sameTerm = true;
//                        break;
//                    }
                }
                if(sameTerm){
                    continue;
                }

                Term term = new Term(null,i,calendar.getTime());

                termFreeList.add(term);

            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return termFreeList;

    }

    /**
     * Pretraga termina za odredjeni predmet
     *
     * @param schedule // raspored nad kojim radimo
     * @param className // naziv predmeta za vezane termine
     * @return lista termina za dati predmet
     * @throws ClassLectureDoesntExistException ako predavanje sa datim imenom ne postoji
     */
    default List<Term> findTerms(Schedule schedule, String className)
        throws ClassLectureDoesntExistException{

        List<Term> termList = new ArrayList<>();

        for(Map.Entry<Term,ClassLecture> entry : schedule.getScheduleMap().entrySet()){
            if(entry.getValue() != null && entry.getValue().getClassName().equals(className)){
                // todo ovo otkomentarisati ako cemo da vracamo samo prvi sat
                if(entry.getValue().getStartTime() == entry.getKey().getStartTime()){
                    termList.add(entry.getKey());
                }
            }
        }

        if(termList.isEmpty()){
            throw new ClassLectureDoesntExistException("Predavanje sa imenom: " +className + " ne postoji");
        }

        return termList;
    }

    /**
     * Pretraga casa po datumu i vremenu
     *
     * @param schedule // raspored nad kojim radimo
     * @param date // datum za pretragu
     * @param time // vreme za pretragu
     * @return predavanje vezano za to vreme i datum
     * @throws TermIsFreeException ako je trazeni termin slobodan
     * @throws DatesException ako datum van randih dana
     * @throws WrongStartTimeException ako je vreme van radnog vremena
     * @throws TermDoesntExistException ako termin ne postoji (ako je vikend)
     */
    default ClassLecture findClassForTerm(Schedule schedule, Date date, int time )
        throws TermIsFreeException, DatesException, WrongStartTimeException, TermDoesntExistException{

        if(schedule.getStartHours() > time || schedule.getEndHours() <= time){
            throw new WrongStartTimeException("Vreme izlazi iz random vremena");
        }
        if(schedule.getStartDate().after(date) || schedule.getEndDate().before(date)){
            throw new DatesException("Datum je van datuma radnih dana");
        }

        for(Map.Entry<Term,ClassLecture> entry : schedule.getScheduleMap().entrySet()){
            if(entry.getKey().getDate().equals(date) && entry.getKey().getStartTime() == time){
                if(entry.getValue() != null){
                    return entry.getValue();
                }
                else {
                    throw new TermIsFreeException("Ovaj termin je slobodan");
                }
            }
        }
        throw new TermDoesntExistException("Ovaj termin ne postoji (je vikend)");
    }

    /**
     * Eksportuje raspored na lokaciji kao CSV
     *
     * @implNote Koristi openCSV
     * @param schedule // raspored
     * @param filePath // lokacija gde ce se nalaziti exportovani fajl
     * @throws FilePathException ako lokacija za fajl nije dobra
     * @throws ScheduleException ako je raspored prazan
     * @throws RuntimeException ako dodje do greske sa openCSV dependencijem
     */
    void exportCSV(Schedule schedule, String filePath);

    /**
     * Importuje raspored sa lokacije u CSV formatu
     *
     * @implNote Koristi openCSV
     * @param schedule // raspored
     * @param filePath // lokacija gde ce se nalaziti exportovani fajl
     * @throws FilePathException ako lokacija za fajl nije dobra
     * @throws ScheduleException ako je raspored prazan
     * @throws RuntimeException ako dodje do greske sa openCSV dependencijem
     */
    void importCSV(Schedule schedule, String filePath);

    /**
     * Eksportuje raspored na lokaciji kao PDF
     *
     * @implNote Koristi pdfbox
     * @param schedule // raspored
     * @param filePath // lokacija gde ce se nalaziti exportovani fajl
     * @throws FilePathException ako lokacija za fajl nije dobra
     * @throws ScheduleException ako je raspored prazan
     * @throws RuntimeException ako dodje do greske sa openCSV dependencijem
     */
    void exportPDF(Schedule schedule, String filePath);
    /**
     * Importuje raspored sa lokacije u PDF formatu
     *
     * @implNote Koristi pdfbox
     * @param schedule // raspored
     * @param filePath // lokacija gde ce se nalaziti exportovani fajl
     * @throws FilePathException ako lokacija za fajl nije dobra
     * @throws ScheduleException ako je raspored prazan
     * @throws RuntimeException ako dodje do greske sa openCSV dependencijem
     */
    void importPDF(Schedule schedule, String filePath);
    /**
     * Eksportuje raspored na lokaciji kao JSON
     *
     * @implNote Koristi GSON
     * @param schedule // raspored
     * @param filePath // lokacija gde ce se nalaziti exportovani fajl
     * @throws FilePathException ako lokacija za fajl nije dobra
     * @throws ScheduleException ako je raspored prazan
     * @throws RuntimeException ako dodje do greske sa openCSV dependencijem
     */
    void exportJSON(Schedule schedule, String filePath);
    /**
     * Importuje raspored sa lokacije u JSON formatu
     *
     * @implNote Koristi GSON
     * @param schedule // raspored
     * @param filePath // lokacija gde ce se nalaziti exportovani fajl
     * @throws FilePathException ako lokacija za fajl nije dobra
     * @throws ScheduleException ako je raspored prazan
     * @throws RuntimeException ako dodje do greske sa openCSV dependencijem
     */
    void importJSON(Schedule schedule, String filePath);


}
