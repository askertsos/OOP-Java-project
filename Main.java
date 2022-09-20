import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Random;
import java.io.File;

public class Main{
    public static void main(String[] args){
        int Cclass=Integer.parseInt(args[0]);
        int Lj=Integer.parseInt(args[1]);
        int Ls=Integer.parseInt(args[2]);
        int Lt=Integer.parseInt(args[3]);
        int N=Integer.parseInt(args[4]);
        School myschool=new School(Cclass,Lj,Ls,Lt);
        myschool.placePeopleOnClasses();
        myschool.beginClasses(N);
        myschool.empty();
        // myschool.print();
    }
}

abstract class Area{
    protected String name;
    public String getName(){return name;}
    public void enter(Student s,String nameofplace){System.out.println(s.getName()+" enters "+nameofplace+"!");}
    public void exit(Student s,String nameofplace){System.out.println(s.getName()+" exits "+nameofplace+"!");}
}

class SchoolYard extends Area{
    SchoolYard(){
        name="schoolyard";
    }
}

class Corridor extends Area{
    Corridor(){
        name="corridor";
    }
}

class Staircase extends Area{
    Staircase(){
        name="staircase";
    }
}

class Classroom extends Area{
    private int Cclass;
    private int Cnow;
    private int Ls; //Poso au3anetai to L ka8e fora gia tous ma8htes
    private int Lt; //Poso au3anetai to L ka8e fora gia tous ka8hghtes
    private Student[] students;
    private Teacher teacher;
    Classroom(int c,int Lst,int Lte){
        Cnow=0;
        Cclass=c;
        Ls=Lst;
        Lt=Lte;
        students=new Student[Cclass];
        for(int i=0;i<Cclass;i++) students[i]=new Student();
        teacher=new Teacher();
    }
    public void print(){
        int i=-1;
        while(++i<Cnow) students[i].print();
        System.out.println("\nTeacher is : "); teacher.print();
    }
    Student getStudent(){
        students[--Cnow].empty();
        return students[Cnow--]; //Meiwnw to Cnow gia na diagrapsw ton ma8hth
    }
    void emptyTeacher(){
        teacher.empty();
    }
    public void operate(int N){
        for(int i=0;i<Cnow;i++){
            students[i].makeTired(N,Ls);
        }
        teacher.makeTired(N,Lt);
    }
    void setTeacher(Teacher T){teacher=T;}
    void enter(Student s){
        System.out.println(s.getName()+" enters classroom!");
        int i=-1;
        while(++i < Cnow); //Briskw thn 8esh sthn opoia 8a mpei o neos ma8hths
        students[i]=s;
        Cnow++;
    }
}

class Floor extends Area{
    private Corridor corridor;
    private Classroom classes[];
    Floor(int c,int Lj,int Ls,int Lt){
        corridor=new Corridor();
        classes=new Classroom[6];
        for(int i=0;i<3;i++){
            classes[i]=new Classroom(c,Lj,Lt);
            classes[i+3]=new Classroom(c,Ls,Lt);
        }
    }
    Corridor getCorridor(){return corridor;}
    public void print(){
        for(int i=0;i<6;i++){
            System.out.println("\nClass "+(i+1)+" consists of :");
            classes[i].print();
        }
    }
    public void operate(int N){for(int i=0;i<6;i++) classes[i].operate(N);}
    public Classroom getClass(int i){return classes[i];}
    public void enter(Student s){
        System.out.println(s.getName()+" enters floor number "+s.getFloornum()+"!");
        corridor.enter(s,corridor.getName());
        corridor.exit(s,corridor.getName());
        classes[s.getClassnum()-1].enter(s);
    }
}

class School extends Area{
    private int realnumofstudents;
    private Student[] students;
    private Teacher[] teachers;
    private SchoolYard yard;
    private Staircase stairs;
    private Floor floors[];
    School(int Cclass,int Lj,int Ls,int Lt){
        realnumofstudents=0;
        students=new Student[Cclass*18]; //Arxikopoihsh pinaka ma8htwn
        try{
            File StudIn = new File("Students.txt");
            Scanner consoleS=new Scanner(StudIn);
            String name;
            int fl,cl;
            while(consoleS.hasNextLine()){
                name=consoleS.next();
                fl=consoleS.nextInt();
                cl=consoleS.nextInt();
                students[realnumofstudents++]=new Student(name,fl,cl);
            }
            consoleS.close();
        } catch (FileNotFoundException e){System.out.println("ERROR");}

        teachers=new Teacher[18]; //Arxikopoihsh pinaka ka8hghtwn
        int i=0;
        try{
            File TeachIn = new File("Teachers.txt");
            Scanner consoleT=new Scanner(TeachIn);
            String name;
            int fl,cl;
            while(consoleT.hasNextLine()){
                name=consoleT.next();
                fl=consoleT.nextInt();
                cl=consoleT.nextInt();
                teachers[i++]=new Teacher(name, fl, cl);
            }
            consoleT.close();
        } catch (FileNotFoundException e){System.out.println("ERROR");}

        yard=new SchoolYard();
        stairs=new Staircase();
        floors=new Floor[3];
        for(i=0;i<3;i++) floors[i]=new Floor(Cclass,Lj,Ls,Lt);
        for(i=0;i<18;i++) floors[teachers[i].getFloornum()-1].getClass(teachers[i].getClassnum()-1).setTeacher(teachers[i]);
    }

    void placePeopleOnClasses(){
        Random rand = new Random();
        int i,StudentsIn=0,TeachersIn=0;
        while(StudentsIn < realnumofstudents || TeachersIn < 18){
            i=rand.nextInt(2);
            if(i == 0 && StudentsIn < realnumofstudents){ //Ama tuxei zugos ari8mos bazw ma8hth sto sxoleio
                i=rand.nextInt(realnumofstudents);
                while(students[i].getIsInClass() == true){ //8a epile3w na balw sto sxoleio enan tuxaio ma8hth pou den exei hdh mpei
                    i=rand.nextInt(realnumofstudents);
                }
                enter(students[i]);
                students[i].place();
                StudentsIn++;
            }
            else if(TeachersIn < 18){//Alliws bazw ka8hghth
                i=rand.nextInt(18);
                while(teachers[i].getIsInClass() == true){ //8a epile3w na balw stthn ta3h enan tuxaio ka8hghth pou den einai hdh ekei
                    i=rand.nextInt(18);
                }
                teachers[i].place();
                TeachersIn++;
            }
        }
    }

    void enter(Student s){
        enter(s,"school");
        yard.enter(s,yard.getName());
        yard.exit(s,yard.getName());
        stairs.enter(s,stairs.getName());
        stairs.exit(s,stairs.getName());
        floors[s.getFloornum()-1].enter(s);
    }

    void beginClasses(int N){
        System.out.println("\nOperating school for " + N + " hours!\n");
        for(int i=0;i<3;i++) floors[i].operate(N);
        for(int i=0;i<3;i++){
            System.out.println("\nFloor "+(i+1)+" consists of :");
            floors[i].print();
        }
    }

    void empty(){
        Student temps;
        for(int i=0;i<3;i++){
            for(int j=0;j<6;j++){
                temps=floors[i].getClass(j).getStudent();//Prwta bgazw tous ma8htes
                floors[i].getCorridor().enter(temps,floors[i].getCorridor().getName());
                floors[i].getCorridor().exit(temps,floors[i].getCorridor().getName());
                stairs.enter(temps,stairs.getName());
                stairs.exit(temps,stairs.getName());
                yard.enter(temps,yard.getName());
                yard.exit(temps,yard.getName());
            }
        }
        for(int i=0;i<3;i++){
            for(int j=0;j<6;j++){
                floors[i].getClass(j).emptyTeacher();//Prwta bgazw tous ma8htes
            }
        }
    }

    void print(){
        int z;
        System.out.println("\nSchool life consists of:");
        for(int i=0;i<3;i++){
            z=i+1;
            System.out.println("\nFloor number " + z + " contains:");
            for(int j=0;j<6;j++){
                z=j+1;
                System.out.println("\nPeople in class " + z + " are:");
                floors[i].getClass(j).print();
                System.out.println();
            }
        }
    }

}

abstract class Person{
    protected String name;
    protected int numfl;
    protected int numcl;
    protected int Ctired;
    protected boolean isInClass;
    void makeTired(int N,int L){
        Ctired=N*L;
    }
    void place(){isInClass=true;}
    public String getName(){return name;}
    int getFloornum(){return numfl;}
    int getClassnum(){return numcl;}
    boolean getIsInClass(){return isInClass;}
}

class Student extends Person{
    Student(){}
    Student(String nam,int fl,int cl){
        System.out.println("A student to be created!");
        name=nam;
        numfl=fl;
        numcl=cl;
        isInClass=false;
        Ctired=0;
        System.out.println("Name : "+ name +"\nFloor number : "+numfl +"\nClass number : "+numcl+"\nCtired is: "+Ctired+"\n");
    }
    void empty(){
        System.out.println(name+" starts exiting!");
        isInClass=false;
    }
    public void print(){System.out.println(name+"\t"+Ctired);}
}

class Teacher extends Person{   
    Teacher(){}
    Teacher(String nam,int fl,int cl){
        System.out.println("A teacher to be created!");
        name=nam;
        numfl=fl;
        numcl=cl;
        isInClass=false;
        Ctired=0;
        System.out.println("Name : "+ name +"\nFloor number : "+numfl +"\nClass number : "+numcl+"\nCtired is: "+Ctired+"\n");
    }
    void empty(){
        System.out.println("Teacher "+name+" is out!");
        isInClass=false;
    }
    public void print(){System.out.println(name+"\t"+Ctired);}
    public void place(){
        System.out.println("Teacher "+name+" has entered their class!");
        isInClass=true;
    }
}