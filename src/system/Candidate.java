package system;

import java.io.Serializable;

/**
 * Created by mdls8 on 4/3/2017.
 */
public class Candidate implements Serializable {
    private String name;
    private String description;
    private String img;

    public Candidate(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Candidate(String name, String description, String img) {
        this.name = name;
        this.description = description;
        this.img = img;
    }

    public Candidate(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String toString(){
        return getName() + " " + getDescription() + "\n";
    }
}
