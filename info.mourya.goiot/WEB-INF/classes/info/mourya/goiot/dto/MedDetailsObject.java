package info.mourya.goiot.dto;

public class MedDetailsObject {
	
	String pillName;
	String morning;
	String afternoon;
	String evening;
	int medicineId;
	int prescription_dur;
	int prescription_rem;
	int sensordata;
	
	
	public int getSensordata() {
		return sensordata;
	}
	public void setSensordata(int sensordata) {
		this.sensordata = sensordata;
	}
	public int getPrescription_dur() {
		return prescription_dur;
	}
	public void setPrescription_dur(int prescription_dur) {
		this.prescription_dur = prescription_dur;
	}
	public int getPrescription_rem() {
		return prescription_rem;
	}
	public void setPrescription_rem(int prescription_rem) {
		this.prescription_rem = prescription_rem;
	}
	public int getMedicineId() {
		return medicineId;
	}
	public void setMedicineId(int medicineId) {
		this.medicineId = medicineId;
	}
	public String getPillName() {
		return pillName;
	}
	public void setPillName(String pillName) {
		this.pillName = pillName;
	}
	public String getMorning() {
		return morning;
	}
	public void setMorning(String morning) {
		this.morning = morning;
	}
	public String getAfternoon() {
		return afternoon;
	}
	public void setAfternoon(String afternoon) {
		this.afternoon = afternoon;
	}
	public String getEvening() {
		return evening;
	}
	public void setEvening(String evening) {
		this.evening = evening;
	}
	

}
