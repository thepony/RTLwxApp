//File created by B. Greg Colburn - N3BYR
// This will write up to three file types to the folder from teh information pulled using RTL_433
// The files can then be used to pass the information in a more readable format for
// APRS or Websites or Packet data


import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RTLwxApp {

    //Fix the conversion annoyance with too many digits
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static void main(String[] args) throws IOException {
	
	//debug testing - leave at zero unles testing changes to code or output is garbage
	int debug = 0;
	int WXfile = 1;	// turn on wx.txt output
	int APRSwx = 0; // turn on APRS formatted file for BPQ
	int historicalWX = 1; // create and save history
	int justgiveme = 1; // print the damn thing to the screen
	
	//Set listening interval
	String flag6 = "22"; // time in seconds to let RTL SDR run to capture data - Recommended less that 60 - MUST BE STRING!
	
	// File to output the weather report
	String theOutput = "wx.txt";		  // Filename for web data
	String aprsFormatted = "aprswx.txt";	  // Formatted for APRS output
	String historicalFile = "wx_history.txt"; // Historical data
	String rainCheck = "rain.txt";		  // Track rain for maths
	String wxInformation = "";		  // Store the format for theOutput file write
	String aprsInformation = "";		  // Store the format for APRS file write
	String wxHistory = "";			  // Store the format for Historical file write
	
	//Variables to store weather data into for processing
	//Not all variables are currently implemented...
	String wxDate = "";
	String wxTime = "";
	String wxID = "";
	float wxTemp = 0;
	float SwxTemp = 0;
	float wxHum = 0;
	float wxWindDir = 0;
	float wxWindSpd = 0;
	float SwxWindSpd = 0;
	float wxWindGust = 0;
	float SwxWindGust = 0;
	float wxRain = 0;
	float SwxRain = 0;
	float wxUV = 0;
	float wxUVI = 0;
	float wxLight = 0;
	String wxStationBatt = "";
	String wxIntegrity = "";
	Boolean wxNewRpt = false;
		
	//RTL application arguments
	String prog = "rtl_433";
	String flag1 = "-f";
	String flag2 = "915M";
	String flag3 = "-F";
	String flag4 = "json";
	String flag5 = "-T";
	//Flag for time instance it new start of method
	ProcessBuilder processBuilder = new ProcessBuilder();
	processBuilder.command(prog, flag1, flag2, flag3, flag4, flag5, flag6);    
		
	try {
	    Process process = processBuilder.start();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

	    String line;
	    while ((line = reader.readLine()) != null) {
		// debug line
		if (debug == 1) System.out.println(line);
		
		String[] theInput = line.split("}");
		String[] theData = theInput[0].split(",");
		int theTotal = theData.length;
		for (int i = 0; i < theTotal; i++) {
		    if (debug == 1) System.out.println("Found: " + theData[i]);
		    String[] theInfo = theData[i].split(":");
		    int parts = theInfo.length;
		    for (int j = 0; j < parts; j++){
			//Prep segments and strip out junk chars
			theInfo[j] = theInfo[j].replace("{", "");
			theInfo[j] = theInfo[j].replace("\"", "");
			theInfo[j] = theInfo[j].strip();
			if (debug == 1) System.out.println("Found:" + theInfo[j]);
					
			switch(theInfo[j]) {
				case "time":
					String[] temp = theInfo[j+1].split(" ");
					wxDate = temp[1];
					wxTime = temp[2] + ":" + theInfo[j+2];
					if (debug == 1) System.out.println("DATE: " + temp[1]);
					if (debug == 1) System.out.println("Hour: " + temp[2]);
					break;
				case "temperature_C":
					wxTemp = Float.parseFloat(theInfo[j+1]);
					break;
				case "humidity":
					wxHum = Float.parseFloat(theInfo[j+1]);
					break;
				case "wind_dir_deg":
					wxWindDir = Float.parseFloat(theInfo[j+1]);
					break;
				case "wind_avg_m_s":
					wxWindSpd = Float.parseFloat(theInfo[j+1]);
					break;
				case "wind_max_m_s":
					wxWindGust = Float.parseFloat(theInfo[j+1]);
					break;
				case "rain_mm":
					wxRain = Float.parseFloat(theInfo[j+1]);
					break;
				case "uv":
					wxUV = Float.parseFloat(theInfo[j+1]);
					break;
				case "uvi":
					wxUVI = Float.parseFloat(theInfo[j+1]);
					break;
				case "light_lux":
					wxLight = Float.parseFloat(theInfo[j+1]);
					break;
				default:
					//nothing
			} // switch tail
		
		    } // nested If statement 
		
		} // Top If statment

	    } // While Statement
	
	    int exitCode = process.waitFor();
	    if (debug == 1) System.out.println("\nExited with error code : " + exitCode);
	    
	    // Process and Output the information pulled
	    //Convert C to F - M/S to MPH - mm to Inch
	    SwxTemp = (float)((wxTemp * 9/5) + 32);
	    SwxWindSpd = (float)(wxWindSpd * 2.237);
	    SwxWindGust = (float)(wxWindGust * 2.237);
	    SwxRain = (float)(wxRain/25.4);
		
	    // Turn to Debug - Info needs to be written to a file for later processing
	    if (debug == 1 || justgiveme == 1) System.out.println(" WX: Time " + wxTime + 
			    "\n Temp: " + wxTemp + "C - " + df.format(SwxTemp) + "F" +
			    "\n Humidity: " + wxHum + "%" +
			    "\n Wind Direction: " + (int)wxWindDir + " degree at " + wxWindSpd + "m/s - " + df.format(SwxWindSpd) + "mph" +
			    "\n Wind Gusts: " + wxWindGust + "m/s - " + df.format(SwxWindGust) + "mph" +
			    "\n Rain Fall Total: " + wxRain + "mm - " + df.format(SwxRain) + "in.");
			    
	    wxInformation = " WX: Time " + wxTime + 
			    "\n Temp: " + wxTemp + "C - " + df.format(SwxTemp) + "F" +
			    "\n Humidity: " + wxHum + "%" +
			    "\n Wind Direction: " + (int)wxWindDir + " degree at " + wxWindSpd + "m/s - " + df.format(SwxWindSpd) + "mph" +
			    "\n Wind Gusts: " + wxWindGust + "m/s - " + df.format(SwxWindGust) + "mph" +
			    "\n Rain Fall Total: " + wxRain + "mm - " + df.format(SwxRain) + "in.";

	    aprsInformation = " WX Time Stamp: " + wxTime + "L (Eastern)" +
			    "\n Temp: " + wxTemp + "c - " + SwxTemp + "f" +
			    "\n Humidity: " + wxHum + "%" +
			    "\n Wind Direction: " + (int)wxWindDir + " degrees at " + wxWindSpd + "m/s - " + SwxWindSpd + "mph" +
			    "\n Gusts: " + wxWindGust + "m/s - " + SwxWindGust + "mph" +
			    "\n Rain: " + wxRain + "mm - " + SwxRain + "in. - past hour";
			    
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd");
	    LocalDate localDate = LocalDate.now();
	    wxHistory = "Date: " + dtf.format(localDate) + " - Time " + wxTime + 
			    "# Temp: " + wxTemp + "C - " + df.format(SwxTemp) + "F" +
			    "# Humidity: " + wxHum + "%" +
			    "# Wind Direction: " + (int)wxWindDir + " degree at "+ wxWindSpd + "m/s - " + df.format(SwxWindSpd) + "mph " +
			    "# Wind Gusts: " + wxWindGust + "m/s - " + df.format(SwxWindGust) + "mph " +
			    "# Rain Fall Total: " + wxRain + "mm - " + df.format(SwxRain) + "in. ";


        } // Try tail statment 

        catch (IOException e) {
	    e.printStackTrace();
	}

	catch (InterruptedException e) {
	    e.printStackTrace();
	}
	
	// Write the information to an external file for use
	try {
	    if (WXfile == 1) {
		FileWriter fn = new FileWriter(theOutput);
		fn.write(wxInformation);
		fn.close();
	    }
	    if (APRSwx == 1) {
		FileWriter fn = new FileWriter(aprsFormatted);
		fn.write(aprsInformation);
		fn.close();
	    }
	    if (historicalWX == 1) {
		BufferedWriter fn = null;
		fn = new BufferedWriter(new FileWriter(historicalFile, true));
		fn.write(wxHistory);
		fn.newLine();
		fn.close();
	    }
	}
	
	catch (IOException e) {
	    System.out.println("File open/use failed!");
	}
	    

    } // Main Statement tail
} // Class statement tail




