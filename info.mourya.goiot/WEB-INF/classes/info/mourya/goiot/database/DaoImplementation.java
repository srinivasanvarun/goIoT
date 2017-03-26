package info.mourya.goiot.database;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;






import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

public class DaoImplementation implements DaoInterface {
	
	public int validateLogin(LoginObject loginObject){
		
		
		
		int roleId = 0;
		String password = null;
		Connection con=null;
		Integer userId=Integer.valueOf(loginObject.getUserId());
		try {
			con = Utility.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT  roleId FROM user WHERE USER_ID="+userId);
			ResultSet rs =  ps.executeQuery();
			
			while(rs.next()){
				roleId=rs.getInt("roleId");
			}
		
			if(roleId>0)
			{  
			    PreparedStatement ps2 = con.prepareStatement("SELECT  * FROM authentication WHERE USERID="+userId);
			    ResultSet rs2 =  ps2.executeQuery();
			    
			    while(rs2.next()){
					password=rs2.getString("Password");
					
				}
			    
			    if(!password.contains(loginObject.getPassword()))
			     { 
			    	roleId=-1;
			     
			     }
			}
			
			
		} catch (SQLException e) {
			System.out.println("SQL ERROR"+e);
		}
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		
		return roleId;
		
		
	}

	public PatientDetailsObject getPatientDetails(Integer userId, int roleId){
		
		
		String doctorName;
		PatientDetailsObject patientDetailsObject = new PatientDetailsObject();
		Connection con = null;
		try {
			con = Utility.getConnection();
			PreparedStatement ps = con.prepareStatement("select B.LastName,A.doctor_userid,A.prescription_ID from prescription A inner join user B on "
					                                    + "A.doctor_userid=B.user_ID where A.status='a' and A.patient_userid="+userId);
			ResultSet rs =  ps.executeQuery();
			while(rs .next()){
				
				doctorName=rs.getString("LastName");
				patientDetailsObject.setDoctorName(doctorName);
				patientDetailsObject.setPrescrptionId(rs.getInt("prescription_ID"));
			}
			
			      PreparedStatement ps2 = con.prepareStatement("select FirstName, LastName, Age, Gender, mobile, Roleid from user where user_ID="+userId);
                  ResultSet rs2 =  ps2.executeQuery();

                  while(rs2.next())
      			{
                	  patientDetailsObject.setPatientFName(rs2.getString("FirstName"));
                	  patientDetailsObject.setPatientLName(rs2.getString("LastName"));
                	  patientDetailsObject.setAge(rs2.getString("Age"));
                	  patientDetailsObject.setGender(rs2.getString("Gender"));
                	  patientDetailsObject.setMobileNumber(rs2.getString("mobile"));
                	  patientDetailsObject.setRole(rs2.getInt("Roleid"));
      				
      				
      			}   
			
			
			
			PreparedStatement ps3 = con.prepareStatement("select A.FirstName,A.Age,B.prescription_ID,B.doctor_userid,C.medicineid,C.morning,C.afternoon,C.evening,C.sensordata,D.medicinename from user A "
					                                    + " inner join prescription B on "
                                                        + " A.user_ID=B.patient_userid inner join medicinedetails C on B.prescription_ID=C.prescriptionid inner join medicinemaster D on D.medicinemaster_ID=C.medicineid where B.status='a' and A.user_ID="+userId);
			ResultSet rs3 =  ps3.executeQuery();
			
			ArrayList<MedDetailsObject> medDetailsObjectList= new ArrayList<MedDetailsObject>();
			
			while(rs3.next())
			{
				MedDetailsObject medDetailsObject = new MedDetailsObject();
				medDetailsObject.setPillName(rs3.getString("medicinename"));
				medDetailsObject.setMorning(rs3.getString("morning"));
				medDetailsObject.setAfternoon(rs3.getString("afternoon"));
				medDetailsObject.setEvening(rs3.getString("evening"));
				medDetailsObject.setMedicineId(rs3.getInt("medicineid"));
				medDetailsObject.setSensordata(rs3.getInt("sensordata"));
				medDetailsObjectList.add(medDetailsObject);
				
			}
			
			patientDetailsObject.setMedDetails(medDetailsObjectList);
			
			  PreparedStatement ps4 = con.prepareStatement("select A.FirstName,A.Age,B.prescription_ID,B.doctor_userid,C.medicineid,C.morning,C.afternoon,C.evening,D.medicinename from user A "
                    + " inner join prescription B on "
                    + " A.user_ID=B.patient_userid inner join adherencedetails C on B.prescription_ID=C.prescriptionid inner join medicinemaster D on D.medicinemaster_ID=C.medicineid where B.status='a' and C.intakedate=CURDATE() and A.user_ID="+userId);
                         ResultSet rs4 =  ps4.executeQuery();

               ArrayList<MedDetailsObject> medAdhereceDetailsObjectList= new ArrayList<MedDetailsObject>();
             

                      while(rs4.next())
                           {
                    	 ;
                        MedDetailsObject medDetailsObject = new MedDetailsObject();
                        medDetailsObject.setPillName(rs4.getString("medicinename"));
                        medDetailsObject.setMorning(rs4.getString("morning"));
                        medDetailsObject.setAfternoon(rs4.getString("afternoon"));
                        medDetailsObject.setEvening(rs4.getString("evening"));
                        medDetailsObject.setMedicineId(rs4.getInt("medicineid"));
                        medAdhereceDetailsObjectList.add(medDetailsObject);

                           }

                        patientDetailsObject.setAdherenceMedDetails(medAdhereceDetailsObjectList);
			
			
			           StatDetailsObject statDetailsObject=new StatDetailsObject();
			           statDetailsObject=getStatDetailsObject();
		 
			
			            patientDetailsObject.setStatDetailsObject(statDetailsObject);
			
			
			
		} catch (SQLException e) {
			System.out.println("error");
		}
		
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
			
		return patientDetailsObject;
		
		
	}
	
public PatientDetailsObject getPatientDetailsForDoc(int userID) {
		
		Integer userId=userID;
		String doctorName;
		PatientDetailsObject patientDetailsObject = new PatientDetailsObject();
		Connection con=null;
		try {
			con = Utility.getConnection();
			PreparedStatement ps = con.prepareStatement("select B.LastName,A.doctor_userid,A.prescription_ID from prescription A inner join user B on "
					                                    + "A.doctor_userid=B.user_ID where A.status='a' and A.patient_userid="+userId);
			ResultSet rs =  ps.executeQuery();
			while(rs .next()){
				
				doctorName=rs.getString("LastName");
				patientDetailsObject.setDoctorName(doctorName);
				patientDetailsObject.setPrescrptionId(rs.getInt("prescription_ID"));
			}
			
			      PreparedStatement ps2 = con.prepareStatement("select FirstName, LastName, Age, Gender, mobile, Roleid from user  where user_ID="+userId);
                  ResultSet rs2 =  ps2.executeQuery();

                  while(rs2.next())
      			{     patientDetailsObject.setUserId(userId);
                	  patientDetailsObject.setPatientFName(rs2.getString("FirstName"));
                	  patientDetailsObject.setPatientLName(rs2.getString("LastName"));
                	  patientDetailsObject.setAge(rs2.getString("Age"));
                	  patientDetailsObject.setGender(rs2.getString("Gender"));
                	  patientDetailsObject.setMobileNumber(rs2.getString("mobile"));
                	  patientDetailsObject.setRole(rs2.getInt("Roleid"));
      				
      				
      			}   
			
			
			
			PreparedStatement ps3 = con.prepareStatement("select A.FirstName,A.Age,B.prescription_ID,B.doctor_userid,C.medicineid,C.morning,C.afternoon,C.evening,D.medicinename from user A "
					                                    + " inner join prescription B on "
                                                        + " A.user_ID=B.patient_userid inner join medicinedetails C on B.prescription_ID=C.prescriptionid inner join medicinemaster D on D.medicinemaster_ID=C.medicineid where B.status='a' and A.user_ID="+userId);
			ResultSet rs3 =  ps3.executeQuery();
			
			ArrayList<MedDetailsObject> medDetailsObjectList= new ArrayList<MedDetailsObject>();
			
			while(rs3.next())
			{
				MedDetailsObject medDetailsObject = new MedDetailsObject();
				medDetailsObject.setPillName(rs3.getString("medicinename"));
				medDetailsObject.setMorning(rs3.getString("morning"));
				medDetailsObject.setAfternoon(rs3.getString("afternoon"));
				medDetailsObject.setEvening(rs3.getString("evening"));
				medDetailsObject.setMedicineId(rs3.getInt("medicineid"));
				medDetailsObjectList.add(medDetailsObject);
				
			}
			
			patientDetailsObject.setMedDetails(medDetailsObjectList);
			
			
			
			
			
		} catch (SQLException e) {
			System.out.println("SQL ERROR"+e);
		}
		
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
			
			
		return patientDetailsObject;
		
		
	}
    
    public StatDetailsObject getStatDetailsObject()
    
    {
    	StatDetailsObject statDetailsObject = new StatDetailsObject();
    	Connection con=null;
    	
    	try {
			con=Utility.getConnection();
			PreparedStatement ps3 = con.prepareStatement("select count(*) as patientcount from user where Roleid=101");
			ResultSet rs3 =  ps3.executeQuery();
			while(rs3.next())
			{
				statDetailsObject.setPatientCount(rs3.getInt("patientcount"));
				
			}
			
			PreparedStatement ps4 = con.prepareStatement("select count(*) as doctorcount from user where Roleid=100");
			
			ResultSet rs4 =  ps4.executeQuery();
			while(rs4.next())
			{
				statDetailsObject.setDocCount(rs4.getInt("doctorcount"));
				
			}
			
            PreparedStatement ps5 = con.prepareStatement("select count(*) as medicinecount from medicinemaster");
			
			ResultSet rs5 =  ps5.executeQuery();
			while(rs5.next())
			{
				statDetailsObject.setMedCount(rs5.getInt("medicinecount"));
				
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
    	
    	
    	return statDetailsObject;
    }

	public DocDetailsObjecct getDocPatientDetails(Integer userId, int roleId){
		
		
		ArrayList<PatientDetailsObject> patientDetailsObjectList= new ArrayList<PatientDetailsObject>();
	
		DocDetailsObjecct docDetailsObject = new DocDetailsObjecct();
		Connection con = null;
		
		try {
			
			con = Utility.getConnection();
			PreparedStatement ps = con.prepareStatement("select FirstName, LastName, Age, Gender, mobile, Roleid from user where user_ID="+userId);
			ResultSet rs =  ps.executeQuery();
			while(rs .next()){
				docDetailsObject.setUserId(userId);
				docDetailsObject.setDoctorFname(rs.getString("FirstName"));
				docDetailsObject.setDoctorLName(rs.getString("LastName"));
				docDetailsObject.setMobileNumber(rs.getString("mobile"));
				docDetailsObject.setAge(rs.getString("Age"));
				docDetailsObject.setRole(roleId);
				
				
			}
			
			
			
			PreparedStatement ps2 = con.prepareStatement("select * from prescription where status='a' and doctor_userid="+userId);
			ResultSet rs2 =  ps2.executeQuery();
			while(rs2 .next()){
				PatientDetailsObject patientDetailsObject=new PatientDetailsObject();
				
				patientDetailsObject=getPatientDetailsForDoc(rs2.getInt("patient_userid"));
			
			//	System.out.println(patientDetailsObject.getPatientFName());
				patientDetailsObjectList.add(patientDetailsObject);
			}
			
			StatDetailsObject statDetailsObject=new StatDetailsObject();
			statDetailsObject=getStatDetailsObject();
		
			
			docDetailsObject.setStatDetailsObject(statDetailsObject);
			docDetailsObject.setPatientDetailsObjectList(patientDetailsObjectList);
			
		} catch (SQLException e) {
			System.out.println("SQL ERROR"+e);
		}
			
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
    	
		return docDetailsObject;
		
		
	}

	public int getEspTriggerDetails(int patientid, int medicineId, String mae) {
		
		int trigger=-1;
		Connection con = null;
		try {
			con = Utility.getConnection();
			
			if(mae.equalsIgnoreCase("m"))
			{
				PreparedStatement ps = con.prepareStatement("select morning from medicinedetails A inner join prescription B on A.prescriptionid=B.prescription_ID where B.status='a' and A.medicineid="
			                                                 +medicineId+" and B.patient_userid="+patientid);
				ResultSet rs =  ps.executeQuery();
				while(rs.next()){
					trigger=rs.getInt("morning");
				}
			}
			
			if(mae.equalsIgnoreCase("a"))
			{
				PreparedStatement ps = con.prepareStatement("select afternoon from medicinedetails A inner join prescription B on A.prescriptionid=B.prescription_ID where B.status='a' and  A.medicineid="
			                                                 +medicineId+" and B.patient_userid="+patientid);
				ResultSet rs =  ps.executeQuery();
				while(rs.next()){
					trigger=rs.getInt("afternoon");
				}
			}
			
			if(mae.equalsIgnoreCase("e"))
			{
				PreparedStatement ps = con.prepareStatement("select evening from medicinedetails A inner join prescription B on A.prescriptionid=B.prescription_ID where B.status='a' and  A.medicineid="
                                                             +medicineId+" and B.patient_userid="+patientid);
				ResultSet rs =  ps.executeQuery();
				while(rs.next()){
					trigger=rs.getInt("evening");
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return trigger;
		
	}

	public String getUsername(int patientid) {
		String userName = null;
		
		try{
		Connection con = Utility.getConnection();
		PreparedStatement ps = con.prepareStatement("SELECT  LastName FROM user WHERE USER_ID="+patientid);
		ResultSet rs =  ps.executeQuery();
		while(rs.next())
		{
			userName=rs.getString("LastName");
		}
	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userName;
	}

	public PatientDetailsObject getDetailsforLCD(int patientid) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean setAdherenceDetails(int medicineId, int patientid, String mae, int update) {
		
		boolean success=false; 
		Connection con = null;
		
		try {
			con = Utility.getConnection();
		
		if(mae.equalsIgnoreCase("m")){
			
			PreparedStatement ps = con.prepareStatement("update adherencedetails SET morning="+update+", m_time=now() where  patientid="+ patientid
				      + " and medicineid="+medicineId+" and intakedate=CURDATE()");
			int check=ps.executeUpdate();
			if(check>0){
				success=true;
			}
			
		}
		
        if(mae.equalsIgnoreCase("a")){
			
        	PreparedStatement ps = con.prepareStatement("update adherencedetails SET afternoon="+update+", a_time=now() where  patientid="+ patientid
				      + " and medicineid="+medicineId+" and intakedate=CURDATE()");
        ;
        	int check=ps.executeUpdate();
			if(check>0){
				success=true;
			}
		}


         if(mae.equalsIgnoreCase("e")){
	          
        	 PreparedStatement ps = con.prepareStatement("update adherencedetails SET evening="+update+", e_time=now() where  patientid="+ patientid
				      + " and medicineid="+medicineId+" and intakedate=CURDATE()");
        	
        	 int check=ps.executeUpdate();
 			if(check>0){
 				success=true;
 			}
	
                }
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return success;
	}

	public List getalarmDetails(int patientid, String mae) {
		List<String> medicineDetailsNTList=new ArrayList<String>();
		//PatientDetailsObject patientDetailsObject= new PatientDetailsObject();
		
		 Connection con=null;
		try{
			con = Utility.getConnection();
			
		if(mae.equalsIgnoreCase("m")){
			List<Integer> medListCompulsary= new ArrayList<Integer>();
			List<Integer> medListtaken= new ArrayList<Integer>();
			
			PreparedStatement ps = con.prepareStatement("select A.medicineid from medicinedetails A "
					   + " inner join prescription B on A.prescriptionid=B.prescription_ID  where B.status='a' and B.patient_userid="+patientid
					   + " and morning=1 ");
			ResultSet rs =  ps.executeQuery();
			System.out.println( " morning ");
			while(rs.next()){
				medListCompulsary.add(rs.getInt("medicineid"));
				
			}
			if(medListCompulsary.size()>0)
			{
				
			PreparedStatement ps2 = con.prepareStatement("select medicineid from adherencedetails where morning=1 and intakedate=CURDATE() "
					+ "and patientid="+patientid);
			ResultSet rs2 =  ps2.executeQuery();
			
			while(rs2.next()){
				medListtaken.add(rs2.getInt("medicineid"));
				
			}
			medListCompulsary.removeAll(medListtaken);
			
			  if(medListCompulsary.size()>0)
			       { for(int i=0;i<medListCompulsary.size();i++)
			           {
			    	    PreparedStatement ps3 = con.prepareStatement("select medicinename from medicinemaster where medicinemaster_ID="
			                                                    +medListCompulsary.get(i));
						ResultSet rs3 =  ps3.executeQuery();
						
						while(rs3.next())
						{
							medicineDetailsNTList.add(rs3.getString("medicinename"));	
						}
			       
			          }
			       }
			  else{
				  medicineDetailsNTList.add("1");
			      }
			         
	       }else{
	    	  
	    	   medicineDetailsNTList.add("1");
	       }
			
			
		}
		
		else if(mae.equalsIgnoreCase("a")){
        	
        	List<Integer> medListCompulsary= new ArrayList<Integer>();
			List<Integer> medListtaken= new ArrayList<Integer>();
			
			PreparedStatement ps = con.prepareStatement("select A.medicineid from medicinedetails A "
					   + " inner join prescription B on A.prescriptionid=B.prescription_ID where B.status='a' and B.patient_userid="+patientid
					   + " and afternoon=1 ");
			ResultSet rs =  ps.executeQuery();
			//System.out.println( " afternoon ");
			while(rs.next()){
				//System.out.println(" medlist compulsary inside");
				medListCompulsary.add(rs.getInt("medicineid"));
				
			}
			//System.out.println(medListCompulsary.size() + " medListCompulsary.size ");
			if(medListCompulsary.size()>0)
			{
				
			PreparedStatement ps2 = con.prepareStatement("select medicineid from adherencedetails where afternoon=1 and intakedate=CURDATE() "
					+ "and patientid="+patientid);
			ResultSet rs2 =  ps2.executeQuery();
			
			while(rs2.next()){
				medListtaken.add(rs2.getInt("medicineid"));
				
			}
			medListCompulsary.removeAll(medListtaken);
			
			  if(medListCompulsary.size()>0)
			       { for(int i=0;i<medListCompulsary.size();i++)
			           {
			    	    PreparedStatement ps3 = con.prepareStatement("select medicinename from medicinemaster where medicinemaster_ID="
			                                                    +medListCompulsary.get(i));
						ResultSet rs3 =  ps3.executeQuery();
						
						while(rs3.next())
						{
							medicineDetailsNTList.add(rs3.getString("medicinename"));	
						}
			       
			          }
			       }
			  else{
				  medicineDetailsNTList.add("1");
			      }
			         
	       }else{
	    	//   System.out.println( " afternoon  else");
	    	   medicineDetailsNTList.add("1");
	       }
			
			
			
		}
        
		else  if(mae.equalsIgnoreCase("e")){
        	
        	
        	List<Integer> medListCompulsary= new ArrayList<Integer>();
			List<Integer> medListtaken= new ArrayList<Integer>();
			
			PreparedStatement ps = con.prepareStatement("select A.medicineid from medicinedetails A "
					   + " inner join prescription B on A.prescriptionid=B.prescription_ID where  B.status='a' and B.patient_userid="+patientid
					   + " and evening=1 ");
			ResultSet rs =  ps.executeQuery();
			while(rs.next()){
				medListCompulsary.add(rs.getInt("medicineid"));
				
			}
			 System.out.println( " evening"); 
			if(medListCompulsary.size()>0)
			{
				
			PreparedStatement ps2 = con.prepareStatement("select medicineid from adherencedetails where evening=1 and intakedate=CURDATE() "
					+ "and patientid="+patientid);
			ResultSet rs2 =  ps2.executeQuery();
			
			while(rs2.next()){
				medListtaken.add(rs2.getInt("medicineid"));
				
			}
			medListCompulsary.removeAll(medListtaken);
			
			  if(medListCompulsary.size()>0)
			       { for(int i=0;i<medListCompulsary.size();i++)
			           {
			    	    PreparedStatement ps3 = con.prepareStatement("select medicinename from medicinemaster where medicinemaster_ID="
			                                                    +medListCompulsary.get(i));
						ResultSet rs3 =  ps3.executeQuery();
						
						while(rs3.next())
						{
							medicineDetailsNTList.add(rs3.getString("medicinename"));	
						}
			       
			          }
			       }
			  else{
				  medicineDetailsNTList.add("1");
			      }
			         
	       }else{
	    	  
	    	   
	    	   medicineDetailsNTList.add("1");
	       }
			
			
        	
        	
        }
		else{
			medicineDetailsNTList.add("-1");
		}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			medicineDetailsNTList.add("-1");
			
			e.printStackTrace();
		}finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		
		return medicineDetailsNTList;
	}

	public PatientDetailsObject getPrescriptionDetails(int patientid, int doctorId) {
		
		
		Integer userId=patientid;
		String doctorName;
		PatientDetailsObject patientDetailsObject = new PatientDetailsObject();
		Connection con=null;
		try {
			con = Utility.getConnection();
			PreparedStatement ps = con.prepareStatement("select B.LastName,A.doctor_userid,A.prescription_ID from prescription A inner join user B on "
					                                    + "A.doctor_userid=B.user_ID where A.status='a' and A.patient_userid="+userId);
			ResultSet rs =  ps.executeQuery();
			while(rs .next()){
				
				doctorName=rs.getString("LastName");
				patientDetailsObject.setDoctorName(doctorName);
				patientDetailsObject.setPrescrptionId(rs.getInt("prescription_ID"));
				patientDetailsObject.setDoctorUserId(rs.getInt("doctor_userid"));
			}
			
			      PreparedStatement ps2 = con.prepareStatement("select FirstName, LastName, Age, Gender, mobile, Roleid from user  where user_ID="+userId);
                  ResultSet rs2 =  ps2.executeQuery();

                  while(rs2.next())
      			{     patientDetailsObject.setUserId(userId);
                	  patientDetailsObject.setPatientFName(rs2.getString("FirstName"));
                	  patientDetailsObject.setPatientLName(rs2.getString("LastName"));
                	  patientDetailsObject.setAge(rs2.getString("Age"));
                	  patientDetailsObject.setGender(rs2.getString("Gender"));
                	  patientDetailsObject.setMobileNumber(rs2.getString("mobile"));
                	  patientDetailsObject.setRole(rs2.getInt("Roleid"));
      				
      				
      			}   
			
			
			
			PreparedStatement ps3 = con.prepareStatement("select A.FirstName,A.Age,B.prescription_ID,B.doctor_userid,C.medicineid,C.morning,C.afternoon,C.evening,D.medicinename from user A "
					                                    + " inner join prescription B on "
                                                        + " A.user_ID=B.patient_userid inner join medicinedetails C on B.prescription_ID=C.prescriptionid inner join medicinemaster D on D.medicinemaster_ID=C.medicineid where B.status='a' and A.user_ID="+userId);
			ResultSet rs3 =  ps3.executeQuery();
			
			ArrayList<MedDetailsObject> medDetailsObjectList= new ArrayList<MedDetailsObject>();
			
			while(rs3.next())
			{
				MedDetailsObject medDetailsObject = new MedDetailsObject();
				medDetailsObject.setPillName(rs3.getString("medicinename"));
				medDetailsObject.setMorning(rs3.getString("morning"));
				medDetailsObject.setAfternoon(rs3.getString("afternoon"));
				medDetailsObject.setEvening(rs3.getString("evening"));
				medDetailsObject.setMedicineId(rs3.getInt("medicineid"));
				medDetailsObjectList.add(medDetailsObject);
				
			}
			
			patientDetailsObject.setMedDetails(medDetailsObjectList);
			
			
			
			
			
		} catch (SQLException e) {
			System.out.println("SQL ERROR"+e);
		}
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
			
		return patientDetailsObject;
		
		
		
	}

	public List<MasterMedicineObject> getMasterMedicineDetails() {
		List<MasterMedicineObject> masterMedicineObjectList=new ArrayList<MasterMedicineObject>();
		
		
		Connection con = null;
		try {
			con = Utility.getConnection();
		
		PreparedStatement ps = con.prepareStatement("SELECT * from medicinemaster");
		ResultSet rs =  ps.executeQuery();
		
		while(rs.next()){
			MasterMedicineObject masterMedicineObject=new MasterMedicineObject();
			masterMedicineObject.setMedicineMasterId(rs.getInt("medicinemaster_ID"));
			masterMedicineObject.setMedicinename(rs.getString("medicinename"));
			masterMedicineObject.setComments(rs.getString("comments"));
			masterMedicineObjectList.add(masterMedicineObject);
		}
		
		} catch (SQLException e) {
			
			e.printStackTrace();
		}	finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return masterMedicineObjectList;
	}

	public boolean updateMedicineMaster(String medcineName, String comment) {
        boolean success = false;
		Connection con = null;
		try {
			con = Utility.getConnection();
		
		     PreparedStatement ps = con.prepareStatement("SELECT * from medicinemaster where medicinename='"+medcineName+"'");
		     ResultSet rs =  ps.executeQuery();
		
		if(!rs.next()){
			
			PreparedStatement ps2=con.prepareStatement("insert into medicinemaster(medicinename,comments) values (?,?)");
			
			ps2.setString(1, medcineName);
			ps2.setString(2, comment);
			
			if(ps2.executeUpdate()>0)
			{
				success=true;
				
			}
			
			
		}
		
		} catch (SQLException e) {
			
			e.printStackTrace();
		}	finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return success;
	}

	public boolean modifyPrescriptionDetails(int patientid, int doctorId, int medId) {
		
		    boolean success = false;
			Connection con = null;
			int prescriptionId = 0;
			try {
				con = Utility.getConnection();
			
			     PreparedStatement ps = con.prepareStatement("select prescription_ID from prescription where patient_userid="+patientid+ " and doctor_userid="+doctorId);
			     ResultSet rs =  ps.executeQuery();
			    while(rs.next())
			    {
			    	prescriptionId=rs.getInt("prescription_ID");
			    	
			    }
			
			if(!rs.next()){
				
				PreparedStatement ps2=con.prepareStatement("delete from medicinedetails where medicineid="+medId+" and prescriptionid="+prescriptionId);
				
				PreparedStatement ps3= con.prepareStatement("delete from adherencedetails where medicineid="+medId+" and prescriptionid="+prescriptionId);
				if(ps2.executeUpdate()>0 && ps3.executeUpdate()>0 )
				{
					success=true;
					
				}
				
				
			}
			
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			finally{
				if(con!=null)
					try {
						con.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
			return success;
		
	}

	public boolean changeMedicinePrescriptionDetails(MedDetailsObject medDetailsObject, int patientId, int doctorId) {
		boolean success=false;
		int prescriptionId = 0;
		Connection con = null;
		
		
		
		int duration=medDetailsObject.getPrescription_dur();
		try {
			 con = Utility.getConnection();
		     PreparedStatement ps = con.prepareStatement("select prescription_ID from prescription where patient_userid="+patientId+ " and doctor_userid="+doctorId);
		     ResultSet rs =  ps.executeQuery();
		    while(rs.next())
		    {
		    	prescriptionId = rs.getInt("prescription_ID");
		    	
		    }
		
		if(prescriptionId>0){
			
			PreparedStatement ps2=con.prepareStatement("select * from medicinedetails where prescriptionid="+prescriptionId+" and medicineid="
			+medDetailsObject.getMedicineId());
			
			ResultSet rs2 =  ps2.executeQuery();
			if(!rs2.next())
			{
				PreparedStatement ps3=con.prepareStatement("insert into medicinedetails(prescriptionid,medicineid,morning,afternoon,evening,"
						+ "prescription_dur,prescription_rem,sensordata) values(?,?,?,?,?,?,?,?)");
				ps3.setInt(1, prescriptionId);
				ps3.setInt(2, medDetailsObject.getMedicineId());
				ps3.setInt(3,Integer.valueOf(medDetailsObject.getMorning()));
				ps3.setInt(4,Integer.valueOf(medDetailsObject.getAfternoon()));
				ps3.setInt(5,Integer.valueOf(medDetailsObject.getEvening()));
				ps3.setInt(6, medDetailsObject.getPrescription_dur());
				ps3.setInt(7, medDetailsObject.getPrescription_dur());
				ps3.setInt(8, 100);
				int det=ps3.executeUpdate();
				System.out.println(det + " eeeeeeeeeee");
				
				if(det>0)
				{
					for(int i=0;i<duration;i++)
					{   Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DAY_OF_MONTH, i);
						Date entryDate = cal.getTime();
						java.sql.Date sqlDate = new java.sql.Date(entryDate.getTime());
						PreparedStatement ps4=con.prepareStatement("insert into adherencedetails(prescriptionid,patientid,medicineid"
								+ ",intakedate,morning,afternoon,evening) values(?,?,?,?,?,?,?)");
						ps4.setInt(1, prescriptionId);
						ps4.setInt(2, patientId);
						ps4.setInt(3, medDetailsObject.getMedicineId());
						ps4.setDate(4,sqlDate);
						ps4.setInt(5, 0);
						ps4.setInt(6, 0);
						ps4.setInt(7, 0);
						System.out.println(ps4.toString());
						
						if(ps4.executeUpdate()>0){
							success=true;
						}
						
					}
				}
				
			}
			
			
			
			
		}
		
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return success;
	}

	public AdminDetailsObject getAdminDetails(Integer userId, int roleId) {
		
		List<PatientDetailsObject> patientDetailsObjectList=new ArrayList<PatientDetailsObject>();
		List<DocDetailsObjecct> docDetailsObjectList=new ArrayList<DocDetailsObjecct>();
		AdminDetailsObject adminDetailsObject=new AdminDetailsObject();
		
		
		Connection con = null;
		try {
			con = Utility.getConnection();
			PreparedStatement ps = con.prepareStatement("select * from user where Roleid="+101);
			ResultSet rs =  ps.executeQuery();
			while(rs.next())
			{PatientDetailsObject patientDetailsObject=new PatientDetailsObject();
			 patientDetailsObject.setPatientFName(rs.getString("FirstName"));
			 patientDetailsObject.setPatientLName(rs.getString("LastName"));
			 patientDetailsObject.setAge(rs.getString("Age"));
			 patientDetailsObject.setPatientEmail(rs.getString("email"));
			 patientDetailsObject.setGender(rs.getString("Gender"));
			 patientDetailsObject.setMobileNumber(rs.getString("mobile"));
			 patientDetailsObject.setUserId(rs.getInt("user_ID"));
			 patientDetailsObject.setRole(rs.getInt("Roleid"));
			 
			    List<MedDetailsObject> medDetailsObjectList=new ArrayList<MedDetailsObject>();
			    PreparedStatement ps3 = con.prepareStatement("select B.medicineid from prescription A "
			    		                      + "inner join medicinedetails B on A.prescription_ID=B.prescriptionid where A.patient_userid="+rs.getInt("user_ID"));
				ResultSet rs3 =  ps3.executeQuery();
				while(rs3.next())
				{ MedDetailsObject medDetailsObject=new MedDetailsObject();
				  medDetailsObject.setMedicineId(rs3.getInt("medicineid"));
				  medDetailsObjectList.add(medDetailsObject);
				}
				
				patientDetailsObject.setMedDetails(medDetailsObjectList);
				patientDetailsObjectList.add(patientDetailsObject);
				
			}
		
			
			
				PreparedStatement ps2 = con.prepareStatement("select * from user where Roleid="+100);
				ResultSet rs2 =  ps2.executeQuery();
				while(rs2.next())
				{DocDetailsObjecct docDetailsObject=new DocDetailsObjecct();
				docDetailsObject.setDoctorFname(rs2.getString("FirstName"));
				docDetailsObject.setDoctorLName(rs2.getString("LastName"));;
				docDetailsObject.setAge(rs2.getString("Age"));;
				docDetailsObject.setDoctorEmail(rs2.getString("email"));;
				docDetailsObject.setMobileNumber(rs2.getString("mobile"));;
				docDetailsObject.setGender(rs2.getString("Gender"));;
				docDetailsObject.setUserId(rs2.getInt("user_ID"));
				docDetailsObject.setRole(rs2.getInt("Roleid"));
				docDetailsObjectList.add(docDetailsObject);
					
				}
				
			    
				adminDetailsObject.setDocDetailsObjectList(docDetailsObjectList);
				adminDetailsObject.setPatientDetailsObjectList(patientDetailsObjectList);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		StatDetailsObject statDetailsObject=new StatDetailsObject();
		statDetailsObject=getStatDetailsObject();
	
		
		adminDetailsObject.setStatDetailsObject(statDetailsObject);
		
		return adminDetailsObject;
	}

	public boolean addNewUserDb(AddNewUserObject addNewUserObject) {
		boolean success=false;
		Connection con = null;
		int roleId=addNewUserObject.getRoleId();
		int maxUserId=0;
		try {
			con = Utility.getConnection();
			
			PreparedStatement ps = con.prepareStatement("insert into user(FirstName,LastName,Age,Gender,mobile,Roleid,email) values(?,?,?,"
					+ "?,?,?,?)");
			ps.setString(1, addNewUserObject.getFirstName());
			ps.setString(2, addNewUserObject.getLastName());
			ps.setInt(3, addNewUserObject.getAge());
			ps.setString(4, addNewUserObject.getGender());
			ps.setString(5, addNewUserObject.getMobile());
			ps.setInt(6, addNewUserObject.getRoleId());
			ps.setString(7, addNewUserObject.getEmail());
			
			if(ps.executeUpdate()>0)
			{
				PreparedStatement ps2 = con.prepareStatement("select MAX(user_ID) as user_ID from user");
				ResultSet rs2=ps2.executeQuery();
				
				if(rs2.next())
				{
					maxUserId=rs2.getInt("user_ID");
				}
				if(maxUserId>0)
				{
				PreparedStatement ps3 = con.prepareStatement("insert into authentication(userID,Password) values(?,?)");
				
				ps3.setInt(1, maxUserId);
				ps3.setString(2, "password");
				int update=ps3.executeUpdate();
				if(update>0 && roleId==101)
				{
					PreparedStatement ps4 = con.prepareStatement("insert into prescription(prescription_Name,patient_userid,status)"
							+ " values (?,?,?)");
					ps4.setString(1, "default_prescrip");
					ps4.setInt(2, maxUserId);
					ps4.setString(3, "a");
					if(ps4.executeUpdate()>0)
					{
						success=true;
					}
					
				}
				if(update>0 && roleId==100)
				{
					
					success=true;
					
				}
				
				}
				
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return success;
	}

	public AdminDetailsObject getAdminDetailsDefault(int i, int j) {
		List<PatientDetailsObject> patientDetailsObjectList=new ArrayList<PatientDetailsObject>();
		List<DocDetailsObjecct> docDetailsObjectList=new ArrayList<DocDetailsObjecct>();
		AdminDetailsObject adminDetailsObject=new AdminDetailsObject();
		
		
		Connection con = null;
		try {
			con = Utility.getConnection();
			PreparedStatement ps = con.prepareStatement("select * from user where Roleid="+101);
			ResultSet rs =  ps.executeQuery();
			while(rs.next())
			{PatientDetailsObject patientDetailsObject=new PatientDetailsObject();
			 patientDetailsObject.setPatientFName(rs.getString("FirstName"));
			 patientDetailsObject.setPatientLName(rs.getString("LastName"));
			 patientDetailsObject.setAge(rs.getString("Age"));
			 patientDetailsObject.setPatientEmail(rs.getString("email"));
			 patientDetailsObject.setGender(rs.getString("Gender"));
			 patientDetailsObject.setMobileNumber(rs.getString("mobile"));
			 patientDetailsObject.setUserId(rs.getInt("user_ID"));
			 patientDetailsObject.setRole(rs.getInt("Roleid"));
			 patientDetailsObjectList.add(patientDetailsObject);
			 
			 List<MedDetailsObject> medDetailsObjectList=new ArrayList<MedDetailsObject>();
			    PreparedStatement ps3 = con.prepareStatement("select B.medicineid from prescription A "
			    		                      + "inner join medicinedetails B on A.prescription_ID=B.prescriptionid where A.patient_userid="+rs.getInt("user_ID"));
				ResultSet rs3 =  ps3.executeQuery();
				while(rs3.next())
				{ MedDetailsObject medDetailsObject=new MedDetailsObject();
				  medDetailsObject.setMedicineId(rs3.getInt("medicineid"));
				  medDetailsObjectList.add(medDetailsObject);
				}
				
				patientDetailsObject.setMedDetails(medDetailsObjectList);
				
			}
		
			
			
				PreparedStatement ps2 = con.prepareStatement("select * from user where Roleid="+100);
				ResultSet rs2 =  ps2.executeQuery();
				while(rs2.next())
				{DocDetailsObjecct docDetailsObject=new DocDetailsObjecct();
				docDetailsObject.setDoctorFname(rs2.getString("FirstName"));
				docDetailsObject.setDoctorLName(rs2.getString("LastName"));;
				docDetailsObject.setAge(rs2.getString("Age"));;
				docDetailsObject.setDoctorEmail(rs2.getString("email"));;
				docDetailsObject.setMobileNumber(rs2.getString("mobile"));;
				docDetailsObject.setGender(rs2.getString("Gender"));;
				docDetailsObject.setUserId(rs2.getInt("user_ID"));
				docDetailsObject.setRole(rs2.getInt("Roleid"));
				docDetailsObjectList.add(docDetailsObject);
					
				}
				
				
				
			
				adminDetailsObject.setDocDetailsObjectList(docDetailsObjectList);
				adminDetailsObject.setPatientDetailsObjectList(patientDetailsObjectList);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		StatDetailsObject statDetailsObject=new StatDetailsObject();
		statDetailsObject=getStatDetailsObject();
	
		
		adminDetailsObject.setStatDetailsObject(statDetailsObject);
		
		
		return adminDetailsObject;
	}

	public AdminDetailsObject getMapDocPatient() {
		List<PatientDetailsObject> patientDetailsObjectList=new ArrayList<PatientDetailsObject>();
		List<DocDetailsObjecct> docDetailsObjectList=new ArrayList<DocDetailsObjecct>();
		AdminDetailsObject adminDetailsObject=new AdminDetailsObject();
		
		
		Connection con = null;
		try {
			con = Utility.getConnection();
			PreparedStatement ps = con.prepareStatement("select * from user A inner join prescription B on A.user_ID"
					+ "=B.patient_userid where B.doctor_userid=0 and A.Roleid="+101);
			ResultSet rs =  ps.executeQuery();
			while(rs.next())
			{PatientDetailsObject patientDetailsObject=new PatientDetailsObject();
			 patientDetailsObject.setPatientFName(rs.getString("FirstName"));
			 patientDetailsObject.setPatientLName(rs.getString("LastName"));
			 patientDetailsObject.setAge(rs.getString("Age"));
			 patientDetailsObject.setPatientEmail(rs.getString("email"));
			 patientDetailsObject.setGender(rs.getString("Gender"));
			 patientDetailsObject.setMobileNumber(rs.getString("mobile"));
			 patientDetailsObject.setUserId(rs.getInt("user_ID"));
			 patientDetailsObject.setRole(rs.getInt("Roleid"));
			 patientDetailsObjectList.add(patientDetailsObject);
				
			}
		
			
			
				PreparedStatement ps2 = con.prepareStatement("select * from user where Roleid="+100);
				ResultSet rs2 =  ps2.executeQuery();
				while(rs2.next())
				{DocDetailsObjecct docDetailsObject=new DocDetailsObjecct();
				docDetailsObject.setDoctorFname(rs2.getString("FirstName"));
				docDetailsObject.setDoctorLName(rs2.getString("LastName"));;
				docDetailsObject.setAge(rs2.getString("Age"));;
				docDetailsObject.setDoctorEmail(rs2.getString("email"));;
				docDetailsObject.setMobileNumber(rs2.getString("mobile"));;
				docDetailsObject.setGender(rs2.getString("Gender"));;
				docDetailsObject.setUserId(rs2.getInt("user_ID"));
				docDetailsObject.setRole(rs2.getInt("Roleid"));
				docDetailsObjectList.add(docDetailsObject);
					
				}
				
			
				adminDetailsObject.setDocDetailsObjectList(docDetailsObjectList);
				adminDetailsObject.setPatientDetailsObjectList(patientDetailsObjectList);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		StatDetailsObject statDetailsObject=new StatDetailsObject();
		statDetailsObject=getStatDetailsObject();
	
		
		adminDetailsObject.setStatDetailsObject(statDetailsObject);
		
	
		return adminDetailsObject;
		
	}

	public boolean getMapDocPatientPostReq(DocPatientMapObject docPatientMapObject) {
		boolean success=false;
		Connection con = null;
		int patientId=docPatientMapObject.getPatientId();
		int doctorId=docPatientMapObject.getDoctorId();
		try {
			con = Utility.getConnection();
			PreparedStatement ps=con.prepareStatement("update prescription set doctor_userid="+doctorId+" where patient_userid="+patientId);
			if(ps.executeUpdate()>0)
			{
				success=true;	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		
		return success;
	}

	public boolean updateSensorData(int patientid, int medicineId, int sensordata) {
		boolean success=false;
		Connection con = null;
		
		try {
			con=Utility.getConnection();
			PreparedStatement ps=con.prepareStatement("select prescription_ID from prescription where patient_userid="+patientid);
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				
				int prescriptionId=rs.getInt("prescription_ID");
				
				PreparedStatement ps2=con.prepareStatement("update medicinedetails set sensordata="+sensordata+" where prescriptionid="+prescriptionId
						+" and medicineid="+ medicineId);
				
				if(ps2.executeUpdate()>0)
				{
					success=true;
				}
				
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return success;
	}

	public boolean validateAndroidLogin(String patientid, String password) {
		// TODO Auto-generated method stub
		Connection con = null;
		boolean success=false;
		try {
			con=Utility.getConnection();
			PreparedStatement ps=con.prepareStatement("select * from authentication where userID="+patientid+" and "
					+ " Password='"+password+"'");
			
		
			ResultSet rs=ps.executeQuery();
			
			while(rs.next()){
				
				success=true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return success;
	}

	public List<String> getRefillCheck(int patientid) {
		List<String> medicineDetailsNTList=new ArrayList<String>();
		  Connection con=null;
		try {
			con=Utility.getConnection();
			PreparedStatement ps=con.prepareStatement("select prescription_ID from prescription where patient_userid="+patientid);
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				
				int prescriptionId=rs.getInt("prescription_ID");
				System.out.println(prescriptionId + " prescriptionId");
				
				PreparedStatement ps2=con.prepareStatement("select B.medicinename from medicinedetails A inner join medicinemaster B on A.medicineid=B.medicinemaster_ID "
						+ " where A.prescriptionid="+prescriptionId+" and sensordata<30");
				ResultSet rs2=ps2.executeQuery();
				System.out.println(ps2.toString());
				
				while(rs2.next()){
					
					medicineDetailsNTList.add(rs2.getString("medicinename"));
					
				}
				
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		finally{
			if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return medicineDetailsNTList;
		
	}

}
