/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package speexfilereader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/* Author: Łukasz Czapla */
class CustomException extends Exception {

    public CustomException() {}
    
    public CustomException(String message)
    {
        super(message);
    }
}

public class SpeexFileReader {
    
    //global variables
    int [][] vectorQuantizationIndexes;
    byte[] inputByteFile;    // file to read from
    byte[] outputByteFile;
    String[] binaryStr; // string with data to hide 
    StringBuilder binFile = new StringBuilder();   //array of strings to write to
    int mode;
    
    //reading input Speex stream
    public String readFile(File inputFile)
    {   
        FileInputStream fileInputStream;
        inputByteFile = new byte[(int) inputFile.length()];
        System.out.println("Length of input file" + inputFile.length());
        try
        { 
            fileInputStream = new FileInputStream(inputFile);
            fileInputStream.read(inputByteFile);
           
            fileInputStream.close();
            for (byte b: inputByteFile){
                int val = b;
                //System.out.println("b value " + b);
                for (int i = 0; i < 8; i++)
                {
                    binFile.append((val & 128) == 0 ? 0 : 1);
                    val <<= 1;
                }
               
            }
            
            //System.out.println(binFile);
            System.out.println("Number of bits read from input file: " + 8 * inputByteFile.length);
        }
        catch (Exception e){
            e.printStackTrace();
        } 
        
        String finalBinFile = binFile.toString();
        return finalBinFile;
    }
    //write modified stream into file and convert to original format 
    private void writeFile(String str){
        
        String outputPath = "C:\\Users\\Cz4p3L\\Desktop\\";
        String extension = ".bin";
        Scanner sc = new Scanner(System.in);
        System.out.println("Please insert fileName");
        String outputFileName = sc.nextLine();
        String fileDest = outputPath + outputFileName + extension;
        boolean endOfStr = false;
        int i=0;
        int j=0;
        int tmpByte=0;
        outputByteFile = new byte[str.length()/8];
        while(!endOfStr){
            tmpByte = Integer.parseInt(str.substring(j, j+8), 2);
            System.out.println("tmpbyte " + tmpByte);
            outputByteFile[i] = (byte)tmpByte;
            System.out.println("byte b:" + outputByteFile[i]);
            i+=1;
            j+=8;
            if (j >= str.length()){
                endOfStr = true;
            }
            
        }
        
        try (FileOutputStream fileOuputStream = new FileOutputStream(fileDest)){
            fileOuputStream.write(outputByteFile);
            System.out.println("Writing file - done!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
        
    //check speex mode
    public int checkMode(String str){
        str = str.substring(1, 5);
        mode = Integer.parseInt(str,2);
        if (mode != 4 && mode != 5 && mode != 6){
            System.err.println("Usupported Speex mode!!!!");
            return -1;
        }
        return mode;
    }
    /* Insert hidden message into bitstream
    @params         String str, String msg, int m
    str - original voice sample (converted into 8-bit string representation)
    msg - converted message to hide (8-bit representation)
    m - mode of original voice sample 
    Function inserts messag into input bit stream.
    */
    private String insertMessage(String str, String msg, int m){
        String[] arrayOfFrames;
        boolean endOfFile=false;
        boolean endOfMsg=false;
        boolean endOfFrame;
        int T = 0;
        int sfT = 0;
        int i = 0;
        int j = 0;
        int initIndexVQ = 0;
        int numOfLSB = 0;
        int n = 1; //number of frame
        // initialize inserting message...
        switch(m){
            case 4:
                System.out.println("Speex mode: 4");
                T = 224;    
                initIndexVQ = 75;  
                sfT=48;              
                break;
            case 5:
                System.out.println("Speex mode: 5");
                T = 304;
                initIndexVQ = 104;
                sfT=65; // 
                break;
            case 6:
                System.out.println("Speex mode: 6");
                T = 368;
                initIndexVQ = 120;
                //firstSubframe = 120;
                sfT=81; //
                break;
            default: 
                System.out.println("Unsupported Speex mode!!");
                break;
        }
        
        System.out.println("T    " + T + "\n" + "IVQ  " + initIndexVQ + "\n" + "sfT  " + sfT);
        System.out.println(str.length() + "\n" + "Number of bits to hide: " + msg.length());
        // scan for value how much LSB bits we want to use //
        while(numOfLSB < 1)
        {
            try{
                Scanner sc = new Scanner(System.in);
                System.out.println("How many LSB bits from InnovationVQ you want to use?");
                numOfLSB = sc.nextInt();
                try {
                    if (mode == 4 & numOfLSB > 35){                    
                        throw new CustomException();
                    }               
                    if (mode == 5 & numOfLSB > 48){
                        throw new CustomException();
                    }                   
                    if (mode == 6 & numOfLSB > 64){
                        throw new CustomException();         
                    }                                        
                }                
                catch (CustomException e){
                    numOfLSB = 0;
                    System.out.println("You exceeded IVQ bit limit for mode " + mode + " !!" );
                }
                
            }
            catch(InputMismatchException e){
                System.out.println("Input mismatch exception: " + e + "\nYou should use number!");
            }
            
        }
        arrayOfFrames = divideIntoFrames(str, mode);
        String tmpMsg;
        while(!endOfFile && !endOfMsg){

                endOfFrame = false;
                StringBuffer sb = new StringBuffer(arrayOfFrames[i]);
                while(!endOfFrame && !endOfMsg){
                    if (j + numOfLSB >= msg.length()){                                
                        tmpMsg = msg.substring(j, msg.length());
                        endOfMsg = true;
                        System.out.println("Loop finished due to end of message, frames affected:" + (i+1));
                        System.out.println("last message " + tmpMsg);
                    }
                    else{
                        tmpMsg = msg.substring(j, j+numOfLSB);
                        System.out.println("extracted from m2h " + tmpMsg);
                        j +=  numOfLSB;                                      
                    }
                    System.out.println("before: " + i + " "+ sb);
                    sb = sb.replace(initIndexVQ - tmpMsg.length() + 1, initIndexVQ+1, tmpMsg);  // tutaj potrzebny jest jakis SB
                    arrayOfFrames[i] = sb.toString();
                    System.out.println("after : " + i + " "+ arrayOfFrames[i]);
                    initIndexVQ += sfT;
                    n+=1;
                    if(n == 5){
                        if (mode == 4) initIndexVQ = 75;
                        else if (mode == 5) initIndexVQ = 104;
                        else if (mode == 6) initIndexVQ = 120;
                        i+=1;
                        n=1;
                        endOfFrame = true;
                    }
                
                }
    
            if(i == arrayOfFrames.length){
                System.out.println("Loop finished due to end of file");
                endOfFile = true;
            }
            
        }
        
        StringBuilder sb = new StringBuilder();
        for (String s: arrayOfFrames){
            sb.append(s);
        }
        
        return sb.toString();
    }
    //Auxiliary functions
    public String[] divideIntoFrames (String str, int mode){
        
        int divisor, amountOfFrames;
        int idxSize, idxAmnt;   // number of bits 
        int i=0;
        int j=0;
        switch(mode){
            case 4:
                idxSize = 7;
                idxAmnt = 5;
                divisor = 224;
                break;
            case 5:
                idxSize = 6;
                idxAmnt = 8;
                divisor = 304;
                break;
            case 6:
                idxSize = 8;
                idxAmnt = 8;
                divisor = 368;
                break;
            default:
                idxSize = 1;
                idxAmnt = 1;
                divisor = 1;
                break;        
        }
        if (idxSize == 1 && idxAmnt == 1 || divisor == 1) {
            return null;
        }
        amountOfFrames = str.length()/divisor;
        vectorQuantizationIndexes = new int[amountOfFrames][4*idxAmnt];
        String arrayOfFrames[] = new String[amountOfFrames];
        while( i < amountOfFrames)
        {
            arrayOfFrames[i] = str.substring(j, j + divisor);
            getIndexVQ(arrayOfFrames[i], i, idxAmnt, idxSize);
            j += divisor;
            i += 1;           
        }
        
        return arrayOfFrames;
    }
    
    public void getIndexVQ(String frame, int frameNo, int indexAmount, int indexBitSize){
        vectorQuantizationIndexes[frameNo][0] = 1;    
    }
    //Convert hidden message to array of strings, it is simplier to insert into stream
    private String convertToBitString(String str){
        if (str.length() == 0) 
            return "";
        
        byte[] bytes = str.getBytes();
        StringBuilder binary = new StringBuilder();
        
        for (byte b : bytes)
        {
            int val = b;
            for (int i = 0; i < 8; i++)
            {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }       
        return binary.toString();
    }
/* 
   Main function for testing steganographic method 
   */   
    public static void main(String[] args){
      
      String basePath = "C:\\Users\\Cz4p3L\\Desktop\\"; //base path for speech sample files
      
      SpeexFileReader sfr = new SpeexFileReader();
      String dataToHide = "tajna wiadomosc do przekazania przy pomoxy Speex";
      String bitStringToHide = sfr.convertToBitString(dataToHide);
      
      File inputFile = new File(basePath + "H11after.bin"); // placing input file
      String inputFileString = sfr.readFile(inputFile);// string 
      sfr.checkMode(inputFileString);
      String strAfterInsert = sfr.insertMessage(inputFileString, bitStringToHide, sfr.mode);      
      //System.out.println("\n" + inputFileString+ "\n");
      
      sfr.writeFile(strAfterInsert);
      System.out.println(strAfterInsert.length());
    }

}
