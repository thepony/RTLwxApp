# RTLwxApp
Java code to pull RTL_433 JSON for WX stations
This quick snippet of code was created to resolve an issue of pulling weather data from
a home weather station that utilizes the RTL_433 application to pull a snapshot of data
using JSON (https://github.com/merbanan/rtl_433).

The data is processed by this java code and stored in three (3) optional areas based off
how the code is compiled. The first is a base TXT file named wx.txt, the second is for APRS
(Automatic Packet Reporting System) aprswx.txt, and the third is a historical file
wx_history.txt which is appended every time the option is used to pull data. 

The APRS data format still needs to be cleaned up and is most like not in a correct
format for the APRS system.

The project is based on a need for my Ham Radio Station to have local weather available
to the packet station (non-APRS) for any connections looking for weather data. The first
iteration is a simple format beta that is functional. When more time is available the 
code needs to be cleaned up to be a more usable format, it was literally a "toss it into
the code file and run it" idea. JSON parsing is done within the code and does not use
external code or libraries to parse - partly due to not coding in a few years and forgetting
how to properly get a library into the java environment while doing a 'quick and dirty' 
coding. Because no JSON library is used there are some anomilies in the parsing of data
to be aware of, not all JSON formatting is properly removed.

To run the code simple use a java machine to run it from the command line. There is no
need or intention to create a GUI for this proect yet, that may occur at a later date.
