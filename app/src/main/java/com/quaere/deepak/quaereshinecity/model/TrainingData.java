package com.quaere.deepak.quaereshinecity.model;

/**
 * Created by deepak sachan on 10/6/2015.
 */
public class TrainingData {
    private String Trainingname;
    private String Trainingid;
     public TrainingData(String trainingname, String id){
          this.Trainingname = trainingname;
         this.Trainingid = id;
     }
 public  TrainingData(){

}
    public String getTrainingname() {
        return Trainingname;
    }

    public void setTrainingname(String trainingname) {
        Trainingname = trainingname;
    }

    public String getTrainingid() {
        return Trainingid;
    }

    public void setTrainingid(String trainingid) {
        Trainingid = trainingid;
    }
}
