package rs.raf.schedule_management;

import rs.raf.classes.ClassLecture;
import rs.raf.classes.Classroom;
import rs.raf.classes.Schedule;
import rs.raf.classes.Term;
import rs.raf.enums.AddOns;
import rs.raf.exceptions.*;

import java.util.*;

public interface ClassSchedule {

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
            for(int i = fromHours; i<=toHours; i++){
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
    default Classroom createClassroom(List<Classroom> classrooms ,String name, int capacity, AddOns ... addOns)
        throws SameNameException,LowCapacityException,DuplicateAddOnsException {

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
     * @throws TermDoesntExistException ako prosledjen termin ne postoji
     * @throws TermTakenException ako je termin vec zauzet
     * @throws ClassroomDoesntExistException ako ucionica ne postoji
     */
    void createClass(Schedule schedule,int startTime, int duration, String classroomName,
                             String lectureName, String professor, Date fromDate, Date toDate)
            throws TermDoesntExistException, TermTakenException, ClassroomDoesntExistException;

    /**
     * Brise cas iz rasporeda
     * @param fromDate // datum predavanja
     * @param toDate // datum do kog traju predavanja ili null ako je predavanje samo jednog dana
     * @param startTime // pocetak predavanja
     * @param classroomName // naziv ucionice u kojoj je predavanje
     * @param lectureName // naziv predavanja ( opciono )
     * @throws TermDoesntExistException ako prosledjen termin ne postoji
     * @throws WrongStartTimeException ako je pocetak predavanja ( startTime ) pogresno unesen
     * @throws WrongDateException ako je datum predavanja ( fromDate ) pogresno unesen
     * @throws WrongLectureNameException ako je naziv predavanja ( lectureName ) pogresno unesen
     * @throws ClassroomDoesntExistException ako ucionica ne postoji
     */
    void RemoveClass(Schedule schedule,Date fromDate,Date toDate, int startTime, String classroomName, String lectureName)
        throws TermDoesntExistException,WrongStartTimeException,WrongDateException,WrongLectureNameException, ClassroomDoesntExistException;

    /**
     * premesta cas iz rasporeda
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
     * @throws TermDoesntExistException ako prosledjen termin ne postoji
     * @throws WrongStartTimeException ako je pocetak predavanja ( startTime ) pogresno unesen
     * @throws WrongDateException ako je datum predavanja ( fromDate ) pogresno unesen
     * @throws WrongLectureNameException ako je naziv predavanja ( lectureName ) pogresno unesen
     * @throws ClassroomDoesntExistException ako ucionica ne postoji
     * @throws TermTakenException ako je termin vec zauzet
     * @throws WrongClassroomNameException ako je ucionica vezana za termin pogresno unesena
     */
    void RescheduleClass(Schedule schedule, Date oldFromDate,Date oldToDate, int oldStartTime, String oldClassroomName, String lectureName,
                         Date newFromDate,Date newToDate, int newStartTime, String newClassroomName)
            throws TermDoesntExistException,TermTakenException,ClassroomDoesntExistException,WrongStartTimeException,
            WrongDateException,WrongLectureNameException,WrongClassroomNameException ;




    /**
     * Pretraga za ucionicu po parametrima
     * @param schedule // raspored nad kojim radimo
     * @param capacity // broj mesta u ucionici ( ako nije bitan parametar proslediti -1 ili 0 )
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

        Set<AddOns> uniqueAddOnsSet = new HashSet<>();

        for (AddOns addOn : addOns) {
            if (!uniqueAddOnsSet.add(addOn)) {
                throw new DuplicateAddOnsException("Duplirani dodatak: " + addOn);
            }
        }
        List<AddOns> addOnsList = new ArrayList<>(uniqueAddOnsSet);


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
     * Pretraga za ucionicu po parametrima
     * @param schedule // raspored nad kojim radimo
     * @param addOns // dodaci koje ucionica ima ( projector, computers, pen )
     * @return lista ucionica koje se poklapaju sa  dodacima
     * @throws ClassroomDoesntExistException ako ucionica ne postoji
     * @throws DuplicateAddOnsException ako postoje duplicirani dodaci
     */
    default List<Classroom> findClassrooms(Schedule schedule, AddOns... addOns)
            throws ClassroomDoesntExistException, DuplicateAddOnsException{
        List<Classroom> classroomsToReturn = new ArrayList<>();

        Set<AddOns> uniqueAddOnsSet = new HashSet<>();

        for (AddOns addOn : addOns) {
            if (!uniqueAddOnsSet.add(addOn)) {
                throw new DuplicateAddOnsException("Duplirani dodatak: " + addOn);
            }
        }
        List<AddOns> addOnsList = new ArrayList<>(uniqueAddOnsSet);


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
     * Pretraga za ucionicu po parametrima
     * @param schedule // raspored nad kojim radimo
     * @param capacity // broj mesta u ucionici ( ako nije bitan parametar proslediti -1 ili 0 )
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
    default List<Term> findTerms(Schedule schedule, Date date, int duration, boolean isFree, int capacity)
        throws DurationException, DatesException, LowCapacityException{

        return null;
    }

    /**
     * Pretraga termina po dodacima ucionice, datumu i duzini termina
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

        Set<AddOns> uniqueAddOnsSet = new HashSet<>();

        for (AddOns addOn : addOns) {
            if (!uniqueAddOnsSet.add(addOn)) {
                throw new DuplicateAddOnsException("Duplirani dodatak: " + addOn);
            }
        }

        List<AddOns> addOnsList = new ArrayList<>(uniqueAddOnsSet);

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

        // goes trough all in map
        for(Map.Entry<Term,ClassLecture> entry : schedule.getScheduleMap().entrySet()){
                // if the term is that date and is one of valid classroms
                if(entry.getKey().getDate().equals(date) && validClassrooms.contains(entry.getKey().getClassroom())) {
                    // if its asked to find free terms
                    if(isFree){
                        // checks if the term is free
                        if(entry.getValue()== null){
                            boolean isTermValid = true;
                            // checks if the final time would go out of bounds
                            if(entry.getKey().getStartTime()+duration > schedule.getEndHours()){
                                isTermValid = false;
                            }

                            // goes through the duration
                            for(int i = 1; i<duration; i++){
                                // no need to go through all if its already broken
                                if(isTermValid == false){
                                    break;
                                }
                                // checks if the next time would go out of bounds
                                if(entry.getKey().getStartTime()+i > schedule.getEndHours()){
                                    isTermValid = false;
                                    break;
                                }
                                Term nextTerm = new Term(entry.getKey().getClassroom(),entry.getKey().getStartTime()+i,date);

                                // finds the next term and checks if its not taken
                                for(Map.Entry<Term,ClassLecture> entry1 : schedule.getScheduleMap().entrySet()) {
                                    if(entry1.getKey().isTermTheSame(nextTerm.getClassroom(), nextTerm.getStartTime(), date)){
                                        if(entry1.getValue()==null){
                                            break;
                                        }
                                        else{
                                            isTermValid = false;
                                            break;
                                        }
                                    }
                                }
                            }

                            if(isTermValid){
                                termsToReturn.add(entry.getKey());
                            }

                        }
                    }
                    else{
                        if(entry.getValue() != null){
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
                                termsToReturn.add(entry.getKey());
//                            }
                        }
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

        List<Term> termList = new ArrayList<>();

        for(Map.Entry<Term,ClassLecture> entry : schedule.getScheduleMap().entrySet()){
            if(entry.getKey().getDate().equals(fromDate)){
                if(entry.getValue().getProfessor().equals(professor)){
//                if(entry.getValue().getStartTime() == entry.getKey().getStartTime()){
                    termList.add(entry.getKey());
//                }
                }
            }
            else if(toDate != null){
                // todo ovde mozda samo after(toDate)
                if((entry.getKey().getDate().after(fromDate) && entry.getKey().getDate().before(toDate))|| entry.getKey().getDate().equals(toDate)){
                    if(entry.getValue().getProfessor().equals(professor)){
//                        if(entry.getValue().getStartTime() == entry.getKey().getStartTime()){
                            termList.add(entry.getKey());
//                        }
                    }
                }
            }

        }
        if(termList.isEmpty()){
            throw new ProfessorDoesntExistException("Profesor sa imenom: " +professor + " ne postoji");
        }
        if(!isFree){
            return termList;
        }

        List<Term> termFreeList = new ArrayList<>();


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);

        // for each date
        while (!calendar.getTime().after(toDate)) {
            // if the date is a weekend skip
            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                continue;
            }
            // for each hour
            for(int i = schedule.getStartHours(); i<= schedule.getEndHours(); i++){

                boolean sameTerm = false;

                for(Term notFreeTerm : termList){
                    if(notFreeTerm.isTermTheSame(null,i,calendar.getTime())){
                        sameTerm = true;
                    }
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
            if(entry.getValue().getClassName().equals(className)){
                // todo ovo mozda izmeniti da radi sa ovim ili izmeniti da se ne vraca termin nego nesto drugo kao 12-14
//                if(entry.getValue().getStartTime() == entry.getKey().getStartTime()){
                    termList.add(entry.getKey());
//                }
            }
        }

        if(termList.isEmpty()){
            throw new ClassLectureDoesntExistException("Predavanje sa imenom: " +className + " ne postoji");
        }

        return termList;
    }





    // TODO ovo prekopirati sa predavanja
    void exportCSV();
    void importCSV();
    void exportFile();
    void importFile();
    void exportJSON();
    void importJSON();


}
