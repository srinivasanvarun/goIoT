package info.mourya.goiot.dto;

import java.util.List;

public class DocDetailsObjecct {
	
	String doctorFname;
	String doctorLName;
	String age;
	String mobileNumber;
	String doctorEmail;
	String gender;
    int userId;
    
	StatDetailsObject statDetailsObject;
	
	

	public StatDetailsObject getStatDetailsObject() {
		return statDetailsObject;
	}
	public void setStatDetailsObject(StatDetailsObject statDetailsObject) {
		this.statDetailsObject = statDetailsObject;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getDoctorEmail() {
		return doctorEmail;
	}
	public void setDoctorEmail(String doctorEmail) {
		this.doctorEmail = doctorEmail;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	int role;
	List<PatientDetailsObject> patientDetailsObjectList;
	public String getDoctorFname() {
		return doctorFname;
	}
	public void setDoctorFname(String doctorFname) {
		this.doctorFname = doctorFname;
	}
	public String getDoctorLName() {
		return doctorLName;
	}
	public void setDoctorLName(String doctorLName) {
		this.doctorLName = doctorLName;
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
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public List<PatientDetailsObject> getPatientDetailsObjectList() {
		return patientDetailsObjectList;
	}
	public void setPatientDetailsObjectList(List<PatientDetailsObject> patientDetailsObjectList) {
		this.patientDetailsObjectList = patientDetailsObjectList;
	}
	
	
	
	

}
