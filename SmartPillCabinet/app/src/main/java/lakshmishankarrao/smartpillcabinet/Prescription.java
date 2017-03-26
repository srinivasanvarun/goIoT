package lakshmishankarrao.smartpillcabinet;

/**
 * Created by lakshmi on 3/2/2016.
 */
public class Prescription {

    private String name;
//    private String fileName;
//    private String description;

    public Prescription(String name){//, String fileName, String description) {
        this.name = name;
//        this.fileName = fileName;
//        this.description = description;
    }

    public String getName() {
        return name;
    }

//    public String getFileName() {
//        return fileName;
//    }
//
//    public String getDescription() {
//        return description;
//    }
}
