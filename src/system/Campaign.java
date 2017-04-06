package system;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by mdls8 on 4/3/2017.
 */
public class Campaign implements Serializable{
    private int id;                                 // used strictly for interaction with database
    private String name;
    private Timestamp start;
    private Timestamp end;
    private String startString;
    private String endString;
    private ArrayList<Candidate> candidates;

    public String getStartString() {
        return startString;
    }

    public void setStartString(String startString) {
        this.startString = startString;
    }

    public String getEndString() {
        return endString;
    }

    public void setEndString(String endString) {
        this.endString = endString;
    }

    public String getName() {
        return name;
    }

    public Campaign(String name, Timestamp start, Timestamp end){
        setName(name);
        setStart(start);
        setEnd(end);
        candidates = new ArrayList<>();
    }
    public Campaign(){
        candidates = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Candidate> getCandidates() {
        return candidates;
    }

    public void addCandidate(String name, String description, String imageURL){
        System.out.println(name);
        candidates.add(new Candidate(name, description, imageURL));
    }
    public String toString(){
        String result = getName() + "\n" + getStart() + "\n" + getEnd() + "\n";

        for(Candidate temp : candidates){
            result += temp + "\n";
        }

        return result;
    }

    public Timestamp stringToDate(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

        try{
            java.util.Date date = sdf.parse(dateString);
            return new Timestamp(date.getTime());
        }
        catch (ParseException e){
            e.printStackTrace();
            return null;
        }
    }

    public void convertToDates(){
        setStart(stringToDate(getStartString()));
        setEnd(stringToDate(getEndString()));
    }
}
