package info.mourya.goiot.dto;

import java.util.List;

public class PatientDetailsObject {
	
	String patientFName;
	String patientLName;
	String age;
	String mobileNumber;
	String gender;
	String doctorName;
	String patientEmail;
	int prescrptionId;
	int userId;
	int doctorUserId;
	int role;
	List<MedDetailsObject> medDetails;
	List<MedDetailsObject> adherenceMedDetails;
	StatDetailsObject statDetailsObject;
	
	
	public List<MedDetailsObject> getAdherenceMedDetails() {
		return adherenceMedDetails;
	}
	public void setAdherenceMedDetails(List<MedDetailsObject> adherenceMedDetails) {
		this.adherenceMedDetails = adherenceMedDetails;
	}
	public StatDetailsObject getStatDetailsObject() {
		return statDetailsObject;
	}
	public void setStatDetailsObject(StatDetailsObject statDetailsObject) {
		this.statDetailsObject = statDetailsObject;
	}
	public String getPatientEmail() {
		return patientEmail;
	}
	public void setPatientEmail(String patientEmail) {
		this.patientEmail = patientEmail;
	}
	public int getDoctorUserId() {
		return doctorUserId;
	}
	public void setDoctorUserId(int doctorUserId) {
		this.doctorUserId = doctorUserId;
	}
	public int getPrescrptionId() {
		return prescrptionId;
	}
	public void setPrescrptionId(int prescrptionId) {
		this.prescrptionId = prescrptionId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public List<MedDetailsObject> getMedDetails() {
		return medDetails;
	}
	public void setMedDetails(List<MedDetailsObject> medDetails) {
		this.medDetails = medDetails;
	}
	
	public String getPatientFName() {
		return patientFName;
	}
	public void setPatientFName(String patientFName) {
		this.patientFName = patientFName;
	}
	public String getPatientLName() {
		return patientLName;
	}
	public void setPatientLName(String patientLName) {
		this.patientLName = patientLName;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getDoctorName() {
		return doctorName;
	}
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	
	

}
