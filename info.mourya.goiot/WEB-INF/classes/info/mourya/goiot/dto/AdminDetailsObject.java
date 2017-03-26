package info.mourya.goiot.dto;

import java.util.List;

public class AdminDetailsObject {
	
	List<PatientDetailsObject> patientDetailsObjectList;
	List<DocDetailsObjecct>    docDetailsObjectList;
	StatDetailsObject statDetailsObject;
	
	public StatDetailsObject getStatDetailsObject() {
		return statDetailsObject;
	}
	public void setStatDetailsObject(StatDetailsObject statDetailsObject) {
		this.statDetailsObject = statDetailsObject;
	}
	public List<PatientDetailsObject> getPatientDetailsObjectList() {
		return patientDetailsObjectList;
	}
	public void setPatientDetailsObjectList(List<PatientDetailsObject> patientDetailsObjectList) {
		this.patientDetailsObjectList = patientDetailsObjectList;
	}
	public List<DocDetailsObjecct> getDocDetailsObjectList() {
		return docDetailsObjectList;
	}
	public void setDocDetailsObjectList(List<DocDetailsObjecct> docDetailsObjectList) {
		this.docDetailsObjectList = docDetailsObjectList;
	}
	
	
	

}
