package info.mourya.goiot.database;

import java.util.List;

import info.mourya.goiot.dto.AddNewUserObject;
import info.mourya.goiot.dto.AdminDetailsObject;
import info.mourya.goiot.dto.DocDetailsObjecct;
import info.mourya.goiot.dto.DocPatientMapObject;
import info.mourya.goiot.dto.LoginObject;
import info.mourya.goiot.dto.MasterMedicineObject;
import info.mourya.goiot.dto.MedDetailsObject;
import info.mourya.goiot.dto.PatientDetailsObject;
import info.mourya.goiot.dto.StatDetailsObject;

public interface DaoInterface {

	int validateLogin(LoginObject loginObject);
	PatientDetailsObject getPatientDetails(Integer userId, int roleId);
	PatientDetailsObject getPatientDetailsForDoc(int userID);
	StatDetailsObject getStatDetailsObject();
	DocDetailsObjecct getDocPatientDetails(Integer userId, int roleId);
	int getEspTriggerDetails(int patientid, int medicineId, String mae);
	String getUsername(int patientid);
	boolean setAdherenceDetails(int medicineId, int patientid, String mae, int update);
	List getalarmDetails(int patientid, String mae);
	PatientDetailsObject getPrescriptionDetails(int patientid, int doctorId);
	List<MasterMedicineObject> getMasterMedicineDetails() ;
	boolean updateMedicineMaster(String medcineName, String comment);
	boolean modifyPrescriptionDetails(int patientid, int doctorId, int medId);
	boolean changeMedicinePrescriptionDetails(MedDetailsObject medDetailsObject, int patientId, int doctorId);
	AdminDetailsObject getAdminDetails(Integer userId, int roleId);
	boolean addNewUserDb(AddNewUserObject addNewUserObject);
	AdminDetailsObject getAdminDetailsDefault(int i, int j);
	AdminDetailsObject getMapDocPatient();
	boolean getMapDocPatientPostReq(DocPatientMapObject docPatientMapObject);
	boolean updateSensorData(int patientid, int medicineId, int sensordata);
	boolean validateAndroidLogin(String patientid, String password);
	List<String> getRefillCheck(int patientid);
	
}
