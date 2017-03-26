#include <ESP8266WiFi.h>
#include <WiFiClient.h>

WiFiClient client;

const char* ssid = "lux";
const char* password = "smartpill";

const char http_site[] = "35.167.209.121";//54.153.76.72";
const int http_port = 8080;


const int patient_id = 1000;
const int medicine_id = 10;
const float PST_GMT_TIME_DIFF = 7;
const int NO_HOURS_IN_A_DAY = 24;
const int TIME_UNITS = 60;

const int MORN_ALARM_ST_TIME = 8;
const int MORN_ALARM_END_TIME = 10;
const int AFT_ALARM_ST_TIME = 13;
const int AFT_ALARM_END_TIME = 15;
const int NIGHT_ALARM_ST_TIME = 19;
const int NIGHT_ALARM_END_TIME = 21;
const int PILL_REFILL_CHECK_ALARM_ST_TIME = 17;

bool BUTTON_PRESS_VALID_TIME = false;

const int RED_LED = 13;
const int WHITE_LED = 14;
const int BUTTON = 12;


bool PILL_TAKEN_MORN = false;
bool PILL_TAKEN_AFT = false;
bool PILL_TAKEN_NIGHT = false;

int button_ip = 0;
int x = 0;

#include <NewPing.h>

#define TRIGGER_PIN 5

#define ECHO_PIN 4

#define MAX_LENGTH_PILL_BOTTLE 10

#define TOTAL_SENSOR_DIST_READINGS 1000

NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_LENGTH_PILL_BOTTLE);

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

bool morning_callback_called = false;
bool afternoon_callback_called = false;
bool night_callback_called = false;
bool pill_refill_callback_called = false;

const int interval = 2*60*60*1000;//2 min //2 hours actually


int testingMornMilli = 30000;
int testingAftMilli = interval+testingMornMilli;
int testingNightMilli = 2*interval +testingMornMilli; 
int testingRefillCheckMilli = 3*interval +testingMornMilli; 

const int TOTAL_DAY_DURATION_MILLIS = 4*interval +testingMornMilli;//24hours

void setup() {
  Serial.begin(115200); 
  Serial.println(); 
  initWifi();
  
  pinMode(RED_LED, OUTPUT);
  pinMode(WHITE_LED, OUTPUT);
  pinMode(BUTTON, INPUT_PULLUP);
  
  String currentTime = getTime();
  Serial.println(currentTime);
  //int hour = 
  extractHrMinAndSetTimer(currentTime);

   
}

void loop() {
  
  if(morning_callback_called == true){
    do_morning_work();
  }
    if(afternoon_callback_called == true){
    do_afternoon_work();
  }
    if(night_callback_called == true){
    do_night_work();
  }
  if(pill_refill_callback_called == true){
    checkNSendPillsQuantityStatus();
  }
}

bool parseResponse(String response) {
  int open_index = response.indexOf('{');
  if (response.c_str()[open_index + 1] == '1') {
    return true;
  }
  return false;  
}

void do_morning_work(){
    
    initializePeriodStartTimerMorn(TOTAL_DAY_DURATION_MILLIS);
    String req = "/info.mourya.goiot/espTrigger/";
    req += patient_id; 
    req += "/";
    req += medicine_id; 
    req += "/m"; 
    String res = SendGetAndPrint(req);// 
    Serial.println("Response:");
    Serial.println(parseResponse(res) ? "true" : "false");
    if(parseResponse(res)){
      digitalWrite(RED_LED, HIGH);
      pushButtonMonitoring('m');
      periodEndMornWrapUp('m');
    }

    morning_callback_called = false;
}

void do_afternoon_work(){
    digitalWrite(RED_LED, HIGH);
    initializePeriodStartTimerAft(TOTAL_DAY_DURATION_MILLIS);
        String req = "/info.mourya.goiot/espTrigger/";
    req += patient_id; 
    req += "/";
    req += medicine_id; 
    req += "/a"; 
    req = "/info.mourya.goiot/espTrigger/1000/10/m";//to be deleted 
    String res = SendGetAndPrint(req);// timerTimeMilliMorn
    Serial.println("Response:");
    Serial.println(parseResponse(res) ? "true" : "false");
    if(parseResponse(res)){
      digitalWrite(RED_LED, HIGH);
      pushButtonMonitoring('a');
      periodEndMornWrapUp('a');
    }
    afternoon_callback_called = false;
}

void do_night_work(){
    digitalWrite(RED_LED, HIGH);
    initializePeriodStartTimerNight(TOTAL_DAY_DURATION_MILLIS);
    String req = "/info.mourya.goiot/espTrigger/";
    req += patient_id; 
    req += "/";
    req += medicine_id; 
    req += "/e"; 
    String res = SendGetAndPrint(req);// timerTimeMilliMorn
    Serial.println("Response:");
    Serial.println(parseResponse(res) ? "true" : "false");    
    if(parseResponse(res)){
      digitalWrite(RED_LED, HIGH);
      pushButtonMonitoring('n');
      periodEndMornWrapUp('n');
    }
    night_callback_called = false;
}

void checkNSendPillsQuantityStatus(){
  unsigned int mostCommonReading = ((do_pill_refill_check() * 100) / MAX_LENGTH_PILL_BOTTLE) ;
  Serial.println("most common reading:");
  Serial.println(mostCommonReading);
  Serial.print("%");
  Serial.println("Percentage of pills remaining in the bottle=");
  int remainingPillsPercent = 100-mostCommonReading;
  Serial.println(100-mostCommonReading);
  Serial.print("%");
  String req = "/info.mourya.goiot/pillRemainingPercent/";
  req += patient_id;
  req += "/";
  req += medicine_id;
  req += "/";
  req += remainingPillsPercent; 
  SendGetAndPrint(req);
  pill_refill_callback_called = false;
}

int do_pill_refill_check(){
  unsigned int readings[MAX_LENGTH_PILL_BOTTLE] = {0};
  
  int i = TOTAL_SENSOR_DIST_READINGS;
  Serial.println("TOTAL_SENSOR_DIST_READINGS i = ");
  Serial.println(i);
  while( i > 0){
    delay(100);
    
    unsigned int uS = sonar.ping();
    
    pinMode(ECHO_PIN,OUTPUT);
    
    digitalWrite(ECHO_PIN,LOW);
    
    pinMode(ECHO_PIN,INPUT);
    
    Serial.print("Ping: ");
    

    int readingInCm = uS / US_ROUNDTRIP_CM;
    if(readingInCm <= MAX_LENGTH_PILL_BOTTLE && (readingInCm > 2)){
      readings[readingInCm]++;
    }//else ignore
    
    Serial.print(readingInCm);
    
    Serial.println("cm");
    Serial.print(US_ROUNDTRIP_CM);
    Serial.print(uS);
    i--;
    
  }
  //find most freq reading
  int mostCommon = 0;
  int maxFreq = 0;
  for(int j = 0; j < MAX_LENGTH_PILL_BOTTLE  ; j++){
    Serial.println(readings[j]);
    if(readings[j] > maxFreq){
      mostCommon = j;
      maxFreq = readings[j];
      Serial.println("mostCommon");
      Serial.println(mostCommon);
      Serial.println("MAx freq");
      Serial.println(maxFreq);
    }
  }
  Serial.print("most common reading = ");
  Serial.println(mostCommon);
  
  return mostCommon;
}
  
String getTime() {
  //WiFiClient client;
  Serial.println("Hi getTime");
  while (!!!client.connect("google.com", 80)) {
    Serial.println("connection failed, retrying...");
  }

  client.print("HEAD / HTTP/1.1\r\n\r\n");
 
  while(!!!client.available()) {
    Serial.println("client not available.");
     yield();
  }

  while(client.available()){
    Serial.println(" yay! client available.");
    if (client.read() == '\n') {    
      if (client.read() == 'D') {    
        if (client.read() == 'a') {    
          if (client.read() == 't') {    
            if (client.read() == 'e') {    
              if (client.read() == ':') {    
                client.read();
                String theDate = client.readStringUntil('\r');
                Serial.println(theDate);
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
   //if (strcmp (WiFi.SSID(),ssid) != 0) {
   
   //}

   while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.print("?");
   }
  Serial.print("\nWiFi connected, IP address: ");
  Serial.println(WiFi.localIP());
} 

void extractHrMinAndSetTimer(String currentTime){
  //Input Time looks like: Mon, 06 Mar 2017 00:38:34 GMT
  //char* str = currentTime.c_str();
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
  int timerTimeMilliPillRefillCheck = getTimerTimeFor(PILL_REFILL_CHECK_ALARM_ST_TIME, hrPST, minute);
  

  Serial.println(timerTimeMilliMorn);
  Serial.println(timerTimeMilliAft);
  Serial.println(timerTimeMilliNight);

  /*timerTimeMilliMorn = testingMornMilli;
  timerTimeMilliAft = testingAftMilli;
  timerTimeMilliNight = testingNightMilli;
  timerTimeMilliPillRefillCheck = testingRefillCheckMilli;*/
  
  initializePeriodStartTimerMorn(timerTimeMilliMorn);//timerTimeMilliMorn
  initializePeriodStartTimerAft(timerTimeMilliAft);//timerTimeMilliAft
  initializePeriodStartTimerNight(timerTimeMilliNight);//timerTimeMilliNight
  initializePillRefillCheckTimer(timerTimeMilliPillRefillCheck);
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

void pushButtonMonitoring(char slot) {
  Serial.println("pushButtonMonitoring");
  int cnt = (interval/1000)-20; //200;//2*60*60*1000
  //if(BUTTON_PRESS_VALID_TIME == true){
  
  while(cnt > 0){
    if(digitalRead(BUTTON) == LOW){
  
      Serial.println("PILL TAKEN");
      digitalWrite(WHITE_LED, HIGH);
      digitalWrite(RED_LED, LOW);
      String req = "/info.mourya.goiot/adherenceUpdate/";
      req += patient_id;
      req += "/";
      req += medicine_id;
      switch(slot){
        case 'm': if(PILL_TAKEN_MORN == false){
                    PILL_TAKEN_MORN = true; 
          
                    req += "/m/1";
                    SendGetAndPrint(req);
                  }
                  break;
        case 'a': if(PILL_TAKEN_AFT == false){
                    PILL_TAKEN_AFT = true; 
                    req += "/a/1"; 
                    SendGetAndPrint(req);
                  }
                  break;
        case 'n': if(PILL_TAKEN_NIGHT == false){
                    PILL_TAKEN_NIGHT = true;                  
                    req += "/e/1"; 
                    SendGetAndPrint(req);
                  }
                  break; 
      }  

    }
    delay(500);
    cnt--;
  }
}

void initializePeriodStartTimerMorn(int timerTimeinMilliSec) {
  Serial.println("initializePeriodStartTimerMorn");
  os_timer_setfn(&myTimer_MornS, periodStartTimerMorn, NULL); //pillRefillCheck
  os_timer_arm(&myTimer_MornS, timerTimeinMilliSec, false);
}

// start of periodStartTimerMorn
void periodStartTimerMorn(void *pArg) {
  morning_callback_called = true;


} // End of periodStartTimerMorn

void periodEndMornWrapUp(char slot){
  
  digitalWrite(RED_LED, LOW);//this wont be needed after switch is used.
  //sendPostReq(pillTakenStatus);
  digitalWrite(WHITE_LED, LOW);
  switch(slot){//
    case 'm': PILL_TAKEN_MORN = false; break;
    case 'a': PILL_TAKEN_AFT = false; break;
    case 'n': PILL_TAKEN_NIGHT = false; break;
  }
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

void initializePillRefillCheckTimer(int timerTimeinMilliSec) {
  Serial.println("initializePillRefillCheckTimer");
  os_timer_setfn(&myTimer_Refill, pillRefillCheck, NULL);//pillRefillCheck
  os_timer_arm(&myTimer_Refill, timerTimeinMilliSec, false);
}

// start of periodStartTimerMorn
void pillRefillCheck(void *pArg) {
  pill_refill_callback_called = true;


} // End of periodStartTimerMorn


String sendGetReq(String req) {
  WiFiClient client1; 

  Serial.println("GET "+req+" HTTP/1.1");
  Serial.println(http_site);
  Serial.println(http_port);
  int times = 10;
  while ( !!!client1.connect(http_site, http_port) && times-->0) {
    Serial.print("Retrying.");
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
  Serial.println("retrieing bytes from network");
  String res;
  while (!!!cl->available()) {
    Serial.println("Waiting for server ... ");
  }
  while(cl->available()) {
    char c = cl->read();
    res += c;  // ignore bytes before {
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
  while(numOfAttemptsGET > 0){
    Serial.println("Sending GET req ");
    Serial.println(numOfAttemptsGET);
    String res = sendGetReq(req);
    if (res != "") {
      return res;
    }else{
      numOfAttemptsGET--;
    }
  }
  Serial.println("Sending GET req failed");
  return "";
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

  return (timerTimeMin *TIME_UNITS*1000);  
}

