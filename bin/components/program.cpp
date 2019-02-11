//RegTech_Sprungantwort
#include <string.h>

boolean usecsvgen = true;
double ampl_d=1.539125122; // Amplifier d
double ampl_k=0.3856;      // Amplifier k
double R_vor=8107;         // Resistor 
double soll_temp = 24+5;   // Initial value! Will be dynamically set in the setup!
double B_NTC=3950;
long int startzeit = millis(); // Time in millis of the last output of the values
double power; // Contains the power output value (Either 0/50 pwm)

void setup() {
  Serial.begin(9600);
  delay(1000); 
  
  // Pre-Execution information gathering
  while(millis() < 5000) {
      double temp = measureTemp(); //Print the temperatur of the environment
      printCSVValue(power,temp,0.0);
  }
  soll_temp = measureTemp() + 5; // Remember the current temperatur + 5deg.
  printCSVValue(power,soll_temp,0.0); // Print it so we now the initial value
  printCSVStart(); // Start mark
}

// Measures the temperature (Described in the previous lab report
double measureTemp() {
  double valueADC_out=analogRead(A1);
  double valueADC_in=valueADC_out*5.0/1023.0*ampl_k+ampl_d;
  double V_in=valueADC_in;
  double R_NTC=(5-V_in)/V_in*R_vor;
  double temp=1.0/(log(R_NTC/10000.0)/B_NTC+1/298.15)-273.15;
  return temp;
}

// Prints the result (required before new implementation)
void messSerial() {
  if(usecsvgen){
    printCSVValue(power,measureTemp(),0);
  }else{
    Serial.print("\t");
    Serial.print(power);
    Serial.print("\t");
    Serial.println(measureTemp());
  } 
}

// loop
/**
 * Analog write -- IMMER MIT CONSTRAIN(X,A,B);Power,0,255
 */
void loop() {
  piRegulation(); 
}

/*
 * Performs a PI-regulation for our PTN control loop to the soll_temp 
 */
void piRegulation(){
   double temp = measureTemp(); // Current temperature
   
   static double Tu = 0.5; // Verzug
   static double Tg = 10.5; // Ausgleich
   static double Ks = (16.7/50.0); // Höchster Punkt Y 

   // ohne Überschwingen
   static double Kp = (0.35 * Tg)/(Ks*Tu); //Kp

   static double Tn=1.2*Tg; //Tn
   static double Ki=Kp/Tn; //Ki

   static double integralVal=0; //Value which holds the current int egralValue
   static double pwrOutput_integralPart=0; //Value which holds the integralpart of the power_output
   // --> Calculate output factor with temp
   int power_output = (Kp * (soll_temp-temp));
   power_output+=(int)pwrOutput_integralPart; //Add integralpart to power_output
   
   //int power_output = (Kp*Ks*soll_temp)/(1.0+Ks*Kp);
   //Serial.println((String)"Power Output: "+power_output+"\n");
   
   // --> Constrain to 255 as max... no strange values
   power_output = constrain(power_output,0,255);

   // --> Write Analog Output
   analogWrite(5,power_output);

   //current systemtime [millis()]
   long int zeit = millis();

   // --> Serial Output
   if((zeit - startzeit)>1000){ // If we printed data more than 1000ms ago, we do it again
    startzeit+=1000; // and set the new starttime to the currentTime + 1000ms!

    integralVal+=(soll_temp-temp)/60.0; //add the temp-difference of now, divided by 60 (convertion to minutes)
                      //to the integralValue   =>  integration of temp-difference to minutes
    pwrOutput_integralPart=integralVal*Ki; //re-calculate the integralpart of the power output

    printCSVValue(power_output,temp,pwrOutput_integralPart); // Print in a format easily transferable into csv files  
  }
}

// Prints a starttag to the serial monitor
void printCSVStart(){
  Serial.print("<START>Millis;Power;Temperature\n");
}

// Prints the values in a csv trasferable format to the serial monitor
void printCSVValue(int power,double temp, double integral){
  Serial.print((String)""+millis()+";"+power+";"+temp+";"+integral+"\n");
}

// Endtag (Not required this time)
void printCSVEnd(){
  Serial.print("<END>\n");
}