package lakshmishankarrao.smartpillcabinet;

/**
 * Created by lakshmi on 3/1/2017.
 */
public class CredentialInfo {
    String patient_id;
    String password;
    int alarm_initiated;
    String choice;
    String patient_name;

    public CredentialInfo(String patient_id, String password, int alarm_initiated, String choice,
                          String patient_name){
        this.patient_id = patient_id;
        this.password = password;
        this.alarm_initiated = alarm_initiated;
        this.choice = choice;
        this.patient_name = patient_name;
    }
}
