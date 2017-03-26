#include <ESP8266WiFi.h>
#include <WiFiClient.h>

WiFiClient client;

const char* ssid = "lux";
const char* password = "smartpill";

const char http_site[] = "35.167.209.121";//54.153.76.72";
const int http_port = 8080;


const int patient_id = 1000;
//List of medicine ids. const int medicine_id = 10;
const float PST_GMT_TIME_DIFF = 7;
const int NO_HOURS_IN_A_DAY = 24;
const int TIME_UNITS = 60;

const int MORN_ALARM_ST_TIME = 8;
const int MORN_ALARM_END_TIME = 10;
const int AFT_ALARM_ST_TIME = 13;
const int AFT_ALARM_END_TIME = 15;
const int NIGHT_ALARM_ST_TIME = 19;
const int NIGHT_ALARM_END_TIME = 21;
const int GET_GENERAL_PRESCRIPTION_ST_TIME = 17;
const int BUZZER_INTERVAL = 45000; //3600000;//1 HOUR IN MILLLIS
const int CLOSE_DRAW_HEIGHT = 7; // height in cm.
const int OPEN_DRAW_HEIGHT = 22; // height in cm.

int buz_frequency=1000; //Specified in Hz
int buzzPin=2; 
int buz_timeOn1=100; //specified in milliseconds
int buz_timeOff1=100; //specified in millisecods
int buz_timeOn2 = 200;
int buz_timeOff2=200; //specified in millisecods
const int buzzer_cnt = 6;
const int buzzer_break_cnt = 3;

bool BUTTON_PRESS_VALID_TIME = false;




const int RED_LED = 12;
const int WHITE_LED = 14;

bool PILL_TAKEN_MORN = false;
bool PILL_TAKEN_AFT = false;
bool PILL_TAKEN_NIGHT = false;

int button_ip = 0;
int x = 0;

#include <NewPing.h>

#define TRIGGER_PIN 5

#define ECHO_PIN 4


NewPing sonar(TRIGGER_PIN, ECHO_PIN, OPEN_DRAW_HEIGHT);

bool DRAW_OPEN_CLOSE_EVENT_MONITOR_M = false; 
bool DRAW_OPEN_CLOSE_EVENT_MONITOR_A = false; 
bool DRAW_OPEN_CLOSE_EVENT_MONITOR_N = false; 
bool BUZZER_MORN_CHECK = false;

extern "C" {
#include "user_interface.h"
}

os_timer_t myTimer_MornS;
os_timer_t myTimer_MornE;
os_timer_t myTimer_AftS;
os_timer_t myTimer_AftE;
os_timer_t myTimer_NightS;
os_timer_t myTimer_NightE;
os_timer_t myTimer_btn;
os_timer_t myTimer_Refill;
os_timer_t myTimer_BuzzerMorn;
os_timer_t myTimer_BuzzerAft;
os_timer_t myTimer_BuzzerNight;

bool morning_callback_called = false;
bool afternoon_callback_called = false;
bool night_callback_called = false;
bool get_gen_prescriptionl_callback_called = false;
bool off_slot_time = true;
bool SENSOR_MONITOR = false;

int interval = 2*60*60*1000;//2*60*60*1000;//2.5 min //2 hours actually


int testingMornMilli = 30000;
int gap = 30000;
int testingAftMilli = interval+testingMornMilli+gap;
int testingNightMilli = 2*interval +testingMornMilli+gap*2; 
int testingGetGeneralPrescriptionMilli = 3*interval +testingMornMilli+ gap*3; 

const int TOTAL_DAY_DURATION_MILLIS = 4*interval +testingMornMilli + 4*gap;//24hours

const int TOTAL_SENSOR_DIST_READINGS = ((interval - 20*1000)/1000)*4;//(2 hours - initial setup/indication time(20s))

const int WEIGHT_FOR_HIGHER_DIST = 3;

void setup() {
  Serial.begin(115200); 
  Serial.println(); 
  initWifi();
  
  pinMode(RED_LED, OUTPUT);
  pinMode(WHITE_LED, OUTPUT);
  
  String currentTime = getTime();
  Serial.println(currentTime);
  //int hour = 
  extractHrMinAndSetTimer(currentTime);

   
}

void loop() {
  
  if(morning_callback_called == true){
    Serial.println("Doing morning work");
    doMorningWork();
    
  }
    if(afternoon_callback_called == true){
      Serial.println("Doing afternoon work");
    doAfternoonWork();
  }
    if(night_callback_called == true){
      Serial.println("Doing night work");
    doNightWork();
  }
  if(get_gen_prescriptionl_callback_called == true){
    getGeneralPrescriptionStatus();
  }


  yield();
}


void doMorningWork(){
    Serial.println("Afternoon period start");
    initializePeriodStartTimerMorn(TOTAL_DAY_DURATION_MILLIS);
    String req = "/info.mourya.goiot/alarmInput/";
    req+= patient_id;
    req += "/m"; 
    String res = SendGetAndPrint(req);// timerTimeMilliMorn
    String parsedResp = parseResponse(res);

    handleBuzzerLEDAndLCD(parsedResp);//response
    drawOpenMonitor('m');
    periodEndMornWrapUp('m');
    morning_callback_called = false;
}

String parseResponse(String response) {
  int start_index = response.indexOf('{');
  Serial.println("Response = ");
  if (response.c_str()[start_index + 1] == '1') {
    Serial.println("1");
    return "1";
  } else{
    int first_quotes = response.indexOf('"', start_index);
    int second_quotes = response.indexOf('"', start_index + 2);
    String x = response.substring(first_quotes+1, second_quotes);
    Serial.println(x);
    return x;
  }
}

int getSonarReading() {
    unsigned int uS = sonar.ping();
    

    pinMode(ECHO_PIN,OUTPUT);
    
    digitalWrite(ECHO_PIN,LOW);
    
    pinMode(ECHO_PIN,INPUT);
    
    Serial.print("Ping: ");

    int readingInCm = uS / US_ROUNDTRIP_CM;
    Serial.println(readingInCm);
    return readingInCm;
}

void drawOpenMonitor(char slot){

  int i = TOTAL_SENSOR_DIST_READINGS;//Need 4 readings every second for 2 hours.
  const int ERROR_THRESHOLD = 4;
  int close_draw_height_count = 0;
  int open_draw_height_count = 0;
  enum State {
    OPEN = 0,
    CLOSE,
  } state = CLOSE;

  while(i-- > 0){
    bool state_changed = false;
    int distance = getSonarReading();
    // Ignore readings near 0 and greater than the max we expect.
    int mid = (CLOSE_DRAW_HEIGHT + OPEN_DRAW_HEIGHT) / 2;
    if (distance < ERROR_THRESHOLD || distance > OPEN_DRAW_HEIGHT + ERROR_THRESHOLD) {
    } else if (distance > mid) {
      open_draw_height_count++;
    } else {
      close_draw_height_count++;
    }
    if (i%60 == 0) {
      Serial.println("Checking for state change");
      Serial.print("open draw height count = ");
      Serial.println(open_draw_height_count);
      Serial.print("close_draw_height_count = ");
      Serial.println(close_draw_height_count);
      Serial.print("Prev State = ");
      state == CLOSE ? Serial.println("CLOSE") : Serial.println("OPEN");
      if (open_draw_height_count*WEIGHT_FOR_HIGHER_DIST > close_draw_height_count) {
        if (state == CLOSE) {
          state = OPEN;
          state_changed = true;
          Serial.println("Drawer has been opened");
          Serial.println("LCD: Please press the button after taking the pills.");
        }
      } else {
        if (state == OPEN) {
          state = CLOSE;
          state_changed = true;
          Serial.println("Drawer has been closed");
        }
      }
      open_draw_height_count = 0;
      close_draw_height_count = 0;
    }
    if (state_changed) {
      if(state == CLOSE){
        //get req: get info from server to see if all medications are taken
        String req = "/info.mourya.goiot/alarmInput/";
        req+= patient_id;
        req += "/m"; 
        String parsedRes = parseResponse(SendGetAndPrint(req));
        //parsedRes = "1";
        if (parsedRes == "1") {
          digitalWrite(RED_LED, LOW);
          Serial.println("All pills are taken.");
          break;
        }else{
          Serial.println("Some pills still not taken");
        }
        Serial.println("Checking with the web server if the pills are taken.");
      }
    }
    delay(250);
  }
  Serial.println("Draw open close Monitoring period done");
}

void handleBuzzerLEDAndLCD(String response){
  Serial.println("handleBuzzerLEDAndLCD");
  if(response == "1"){
    Serial.println("response == 1");
    //lcd.print("No pills due");
  }else{
    //Switch on LED. 
    if(digitalRead(RED_LED) == LOW){
      digitalWrite(RED_LED, HIGH);
    }
    Serial.println("response == some text");
    String dispText = "Time for pills! "+response;
    //lcd.print(dispText);
    activateBuzzer();
  }
}

void activateBuzzer(){
  int i = 2;
  while (i > 0){
    i--;
    int cnt = buzzer_cnt;
    while(cnt > 0){
      ringBuzzer();
      if(cnt == buzzer_break_cnt+1){
        int total = (buz_timeOn1 + buz_timeOff1)*2 + buz_timeOn2 + buz_timeOff2;
        delay(total);
      }
      cnt--;
    }
  }
}

void ringBuzzer(){
  int i = 2;
  while(i > 0){
    tone(buzzPin, buz_frequency);
    delay(buz_timeOn1);
    noTone(buzzPin);
    delay(buz_timeOff1);
    i--;
  }
  
  tone(buzzPin, buz_frequency);
  delay(buz_timeOn2);
  noTone(buzzPin);
  delay(buz_timeOff2);

}

void doAfternoonWork(){

    Serial.println("Afternoon period start");
    initializePeriodStartTimerAft(TOTAL_DAY_DURATION_MILLIS);
    String req = "/info.mourya.goiot/alarmInput/";
    req+= patient_id;
    req += "/a"; 
    String res = SendGetAndPrint(req);// timerTimeMilliMorn
    String resp = "{\"Crocin,Paracetamol\"}";
    //String resp = "1";
    String parsedResp = parseResponse(resp);

    handleBuzzerLEDAndLCD(res);//response
    drawOpenMonitor('a');
    periodEndMornWrapUp('a');
    afternoon_callback_called = false;

}

void doNightWork(){

    Serial.println("Night period start");
    initializePeriodStartTimerNight(TOTAL_DAY_DURATION_MILLIS);
    String req = "/info.mourya.goiot/alarmInput/";
    req+= patient_id;
    req += "/n"; 
    String res = SendGetAndPrint(req);// timerTimeMilliMorn
    String resp = "{\"Crocin,Paracetamol\"}";
    //String resp = "1";
    String parsedResp = parseResponse(resp);

    handleBuzzerLEDAndLCD(res);//response
    drawOpenMonitor('n');
    periodEndMornWrapUp('n');
    night_callback_called = false;

  
}

void getGeneralPrescriptionStatus(){
  Serial.println("getGeneralPrescriptionStatus");
  String req = "/info.mourya.goiot/prescriptions/";
  req += patient_id;
  req += "/"; 
  String resp = SendGetAndPrint(req); 
  Serial.println(resp);
  get_gen_prescriptionl_callback_called = false; 
}
  
String getTime() {
  //WiFiClient client;
  while (!!!client.connect("google.com", 80)) {
    Serial.println("connection failed, retrying...");
  }

  client.print("HEAD / HTTP/1.1\r\n\r\n");
 
  while(!!!client.available()) {
     yield();
  }

  while(client.available()){
    if (client.read() == '\n') {    
      if (client.read() == 'D') {    
        if (client.read() == 'a') {    
          if (client.read() == 't') {    
            if (client.read() == 'e') {    
              if (client.read() == ':') {    
                client.read();
                String theDate = client.readStringUntil('\r');
                client.stop();
                return theDate;
              }
            }
          }
        }
      }
    }
  }
}

void initWifi() {
   if (WiFi.status() == WL_CONNECTED) {
    Serial.println("Connected already");
    return;
   }
   Serial.print("Connecting to ");
   Serial.print(ssid);
   WiFi.mode(WIFI_STA);
   WiFi.begin(ssid, password);

   while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.print("?");
   }
  Serial.print("\nWiFi connected, IP address: ");
  Serial.println(WiFi.localIP());
} 

void extractHrMinAndSetTimer(String currentTime){
  //Input Time looks like: Mon, 06 Mar 2017 00:38:34 GMT
  Serial.println(currentTime);
  int index = currentTime.indexOf(":");
  Serial.println(index);
  String hrStr = currentTime.substring(index-2,index);
  String minuteStr = currentTime.substring(index+1, index+3);
  Serial.println(hrStr);
  Serial.println(minuteStr);

  int hr = atoi(hrStr.c_str());
  int minute =  atoi(minuteStr.c_str());

  Serial.println(hr);
  Serial.println(minute);

  int hrPST = getPSThour(hr); 
  Serial.println(hrPST);
  //get timer time in milli for morning, aft and night
  int timerTimeMilliMorn = getTimerTimeFor(MORN_ALARM_ST_TIME, hrPST, minute);
  int timerTimeMilliAft = getTimerTimeFor(AFT_ALARM_ST_TIME, hrPST, minute);
  int timerTimeMilliNight = getTimerTimeFor(NIGHT_ALARM_ST_TIME, hrPST, minute);
  int timerTimeGetGeneralPrescription = getTimerTimeFor(GET_GENERAL_PRESCRIPTION_ST_TIME, hrPST, minute);
  

  Serial.println(timerTimeMilliMorn);
  Serial.println(timerTimeMilliAft);
  Serial.println(timerTimeMilliNight);

  /*timerTimeMilliMorn = testingMornMilli;
  timerTimeMilliAft = testingAftMilli;
  timerTimeMilliNight = testingNightMilli;
  timerTimeGetGeneralPrescription = testingGetGeneralPrescriptionMilli;*/
  
  initializePeriodStartTimerMorn(timerTimeMilliMorn);//timerTimeMilliMorn
  initializePeriodStartTimerAft(timerTimeMilliAft);//timerTimeMilliAft
  initializePeriodStartTimerNight(127*60*1000);//timerTimeMilliNight
  initializeGetGeneralPrescriptionTimer(timerTimeGetGeneralPrescription);
  
  //initializeButtonTimer(1000);
  Serial.println("All timers set");
  
}

int getPSThour(int hr){

  int pst = 0;
  if(hr > (PST_GMT_TIME_DIFF-1)){
    pst = hr - PST_GMT_TIME_DIFF;
  }else{
    pst = hr - PST_GMT_TIME_DIFF +NO_HOURS_IN_A_DAY;
  }
  return pst;
}

void initializePeriodStartTimerMorn(int timerTimeinMilliSec) {
  Serial.println("initializePeriodStartTimerMorn");
  os_timer_setfn(&myTimer_MornS, periodStartTimerMorn, NULL);
  os_timer_arm(&myTimer_MornS, timerTimeinMilliSec, false);
}

// start of periodStartTimerMorn
void periodStartTimerMorn(void *pArg) {
  morning_callback_called = true;


} // End of periodStartTimerMorn

void periodEndMornWrapUp(char slot){
  Serial.println("periodEndMornWrapUp");
  digitalWrite(RED_LED, LOW);
  //sendPostReq(pillTakenStatus);
  //digitalWrite(WHITE_LED, LOW);
  /*switch(slot){//
    case 'm': PILL_TAKEN_MORN = false; break;
    case 'a': PILL_TAKEN_AFT = false; break;
    case 'n': PILL_TAKEN_NIGHT = false; break;
  }*/
}

void initializePeriodStartTimerAft(int timerTimeinMilliSec) {
  Serial.println("initializePeriodStartTimerAft");
  os_timer_setfn(&myTimer_AftS, periodStartTimerAft, NULL);
  os_timer_arm(&myTimer_AftS, timerTimeinMilliSec, false);
}

// start of periodStartTimerAft
void periodStartTimerAft(void *pArg) {
  afternoon_callback_called = true;
} // End of periodStartTimerAft


void initializePeriodStartTimerNight(int timerTimeinMilliSec) {
  Serial.println("initializePeriodStartTimerNight");
  os_timer_setfn(&myTimer_NightS, periodStartTimerNight, NULL);
  os_timer_arm(&myTimer_NightS, timerTimeinMilliSec, false);
}

// start of periodStartTimerAft
void periodStartTimerNight(void *pArg) {
  night_callback_called = true;  
} // End of periodStartTimerNight.

void initializeGetGeneralPrescriptionTimer(int timerTimeinMilliSec) {
  Serial.println("initializeGetGeneralPrescriptionTimer");
  os_timer_setfn(&myTimer_Refill, getGeneralPrescriptionCallback, NULL);//getGeneralPrescriptionCallback
  os_timer_arm(&myTimer_Refill, timerTimeinMilliSec, false);
}

// start of periodStartTimerMorn
void getGeneralPrescriptionCallback(void *pArg) {
  get_gen_prescriptionl_callback_called = true;


} // End of periodStartTimerMorn


String sendGetReq(String req) {
  WiFiClient client1; 

  //format= /info.mourya.goiot/espTrigger/patient_id/medicine_id/m
  // Attempt to make a connection to the remote server

  Serial.println("GET "+req+" HTTP/1.1");
  Serial.println(http_site);
  Serial.println(http_port);
  int times = 10;
  while ( !!!client1.connect(http_site, http_port) && times-->0) {
    Serial.println("Retrying.");
  }
  if (times < 0) {
    Serial.println("Server unreacheable");
    return "";
  }
  // Make an HTTP GET request
  Serial.println("GET "+req+" HTTP/1.1");
  client1.println("GET "+req+" HTTP/1.1");
  client1.print("Host: ");
  client1.println(http_site);
  client1.println("Connection: close");
  client1.println();
  return readIncomingBytes(&client1);
}

String readIncomingBytes(WiFiClient* cl){
  // If there are incoming bytes, print them
  Serial.println("retrieving bytes from network");
  String res;
  //Serial.println("Waiting for server ... ");
  while (!!!cl->available()) {
  }
  while(cl->available()) {
    char c = cl->read();
    res += c;
  }
  Serial.println(res);
  if (!cl->connected()) {
    cl->stop(); 
  }
  return res;
}


String SendGetAndPrint(String req){
  int numOfAttemptsGET = 5;
  Serial.println("Initing wifi ");
  initWifi();
  Serial.println("Sending below req");
  Serial.println(req);
  while(numOfAttemptsGET > 0){
    Serial.println("Sending GET req ");
    //Serial.println(numOfAttemptsGET);
    String res = sendGetReq(req);
    if (res != "") {
      return res;
    }else{
      numOfAttemptsGET--;
    }
  }
  if(numOfAttemptsGET == 0){
    Serial.println("Sending GET req failed");
  } 
  
}

int getTimerTimeFor(int startHour, int hrPST, int minute){
  int timerTimeMin = 0;
  if( hrPST < startHour){
    //subtract and set ((MORN_ALARM_ST_TIME-hr -1)*60 + (60-minute))*60*1000
    //set timer
    timerTimeMin = ((startHour - hrPST -1)*TIME_UNITS + (TIME_UNITS-minute));
  }else{
    //(24-hr+-1+MORN_ALARM_ST_TIME)*60*60*1000
    timerTimeMin = ((NO_HOURS_IN_A_DAY - hrPST - 1 + startHour)*TIME_UNITS + (TIME_UNITS-minute));
  }
Serial.println(timerTimeMin);
  return (timerTimeMin *TIME_UNITS*1000);  
}

