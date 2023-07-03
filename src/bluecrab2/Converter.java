package bluecrab2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This is a simple program that converts a Classic 0.25_05 SURVIVAL TEST Minecraft level
 * to be playable in Classic 0.27 SURVIVAL TEST. Earlier versions convert to 0.27 successfully
 * and 0.25_05 will convert to later versions successfully, it is only this one update that
 * encounters an unavoidable error. 
 * 
 * == The Issue ==
 * The problem is that the Player$1 did not have its serialVersionUID
 * set to 0 in 0.25_05 like most other classes. Therefore, the serialVersionUID defaulted
 * to -5141891085410515807 in 0.25_05. Notch set the serialVersionUID to 0 by Classic 0.27
 * which caused an error when reading files from 0.25_05 that prevents it from being read.
 * By Classic 0.28_01 and later, Notch made a specialized class that extends ObjectInputStream
 * just so either serialVersionUID from 0.25_05 or 0.27 could be accepted allowing all levels
 * to upgrade. This most likely applies to other versions as well but as of 26 March, 2023
 * these are the only archived versions this program works to fix.
 * 
 * == The Solution ==
 * This program only edits the serialVersionUID of Player$1 in the file from -5141891085410515807 
 * to 0. This value is only used by Java's deserializer to identify the classes it is reading in 
 * and has no effect in game.
 * 
 * == Usage ==
 * The main method of this program can be ran with zero or two parameters. If two parameters are
 * given, the first should be the path to the input file and the second should be the path to the
 * output file. The two paths cannot be the same. If zero parameters are given, the program will
 * default to reading level.dat and writing to output.dat in the current directory.
 * 
 * == Copyright ==
 * I release this code to the public domain, although attribution would be appreciated!
 * 
 * @author bluecrab2
 *
 */
public class Converter {
	private static FileInputStream inputFileStream;
	private static FileOutputStream outputFileStream;
	private static GZIPInputStream gzipIn;
	private static GZIPOutputStream gzipOut;
	
	private static final byte[] PLAYER_SEARCH_SEQUENCE = {
            (byte) 'P', (byte) 'l', (byte) 'a', (byte) 'y', (byte) 'e', (byte) 'r', (byte) '$', (byte) '1',
            (byte) 0xB8, (byte) 0xA4, (byte) 0x55, (byte) 0x4C, (byte) 0xFC, (byte) 0x5B, (byte) 0xC4, (byte) 0xA1
    };

    private static final byte[] PLAYER_REPLACE_SEQUENCE = {
            (byte) 'P', (byte) 'l', (byte) 'a', (byte) 'y', (byte) 'e', (byte) 'r', (byte) '$', (byte) '1',
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
    };
	
	public static void main(String[] args) throws IOException {
		//// Open input and output file
		if(args.length == 0) {
			inputFileStream = new FileInputStream("level.dat");
			outputFileStream = new FileOutputStream("output.dat");
		} else if(args.length == 2) {
			if(args[0] == args[1])
				throw new IllegalArgumentException("Error: input and output files cannot be the same.");
			
			inputFileStream = new FileInputStream(args[0]);
			outputFileStream = new FileOutputStream(args[1]);
		} else {
			System.err.println("Usage: java -jar Classic25to27Converter.jar <input file> <output file>");
		}
		gzipIn = new GZIPInputStream(inputFileStream);
		gzipOut = new GZIPOutputStream(outputFileStream);
		
		//// Find and replace sequences
		int byteRead;
		int seqIdx = 0;
		int replacementCount = 0; //count # of replacements
		
		//Read file byte by byte
		while ((byteRead = gzipIn.read()) != -1) {
			if(byteRead == Byte.toUnsignedInt(PLAYER_SEARCH_SEQUENCE[seqIdx])) { //0xff removes bytes sign
				// Current byte matches next needed in search sequence
				seqIdx++;
				
				//Full sequence found
				if(seqIdx == PLAYER_SEARCH_SEQUENCE.length) {
					gzipOut.write(PLAYER_REPLACE_SEQUENCE);
					seqIdx = 0;
					replacementCount++;
				}
			} else {
				//Write out bytes that were saved but not replaced
				if(seqIdx > 0) {
					gzipOut.write(PLAYER_SEARCH_SEQUENCE, 0, seqIdx);
					seqIdx = 0;
				}
				gzipOut.write(byteRead);
			}
		}
        
		if(replacementCount == 0) {
			throw new IllegalArgumentException("Error: no replacement found, make sure you're using program on a level from 0.25_05 SURVIVAL TEST");
		} else if(replacementCount > 1) {
			throw new IllegalArgumentException("Error: more than one replacement occurred");
		}
        
        //Close file streams
        gzipIn.close();
        gzipOut.close();
	}
}
