# Classic 0.25_05 to 0.27 Converter
This is a simple program that converts a Classic 0.25_05 SURVIVAL TEST Minecraft level to be playable in Classic 0.27 SURVIVAL TEST. Earlier versions convert 
to 0.27 successfully and 0.25_05 will convert to later versions successfully, it is only this one update that encounters an unavoidable error. 

## The Issue
The problem is that the Player$1 class did not have its serialVersionUID set to 0 in 0.25_05 like most other classes. Therefore, the serialVersionUID defaulted
to -5141891085410515807 in 0.25_05. Notch set the serialVersionUID to 0 by Classic 0.27 which caused an error when reading files from 0.25_05 that prevents
it from being read. By Classic 0.28_01 and later, Notch made a specialized class that extends ObjectInputStream just so either serialVersionUID from 0.25_05
or 0.27 could be accepted allowing all levels to upgrade. This most likely applies to other versions as well but as of 26 March, 2023 these are the only 
archived versions this program works to fix.

## The Solution
This program only edits the serialVersionUID of Player$1 in the file from -5141891085410515807 to 0. This value is only used by Java's deserializer to 
identify the classes it is reading in and has no effect in game.

## Usage
Ensure Java is downloaded in order to use program.

### JAR File
The JAR File can be used by putting it inside the same folder as the level file with the name "level.dat". The program will output the new, corrected file to "output.dat" in that same folder. The "output.dat" file should be renamed to "level.dat" in order to be played by your launcher.

### Command Line
The main method of this program can be ran with zero or two parameters. With no parameters, the program defaults to read from level.dat and write to 
output.dat (`java -jar Classic25to27Converter.jar`).

If two parameters are given, the first should be the path to the input file and the second should be the path to the output file. The two paths cannot be the same. (`java -jar Classic25to27Converter.jar My025Level.dat Corrected027Level.dat`)
