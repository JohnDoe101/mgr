/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package speexfilereader;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
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
    String[] binaryStr; // string with data to hide 
    byte[] byteFile;    // file to read from
    StringBuilder binFile = new StringBuilder();   //array of strings to write to
    
    int mode;   //
    
    //reading input Speex stream
    public String readFile(File inputFile)
    {   
        FileInputStream fileInputStream;
        byteFile = new byte[(int) inputFile.length()];
        
        try
        { 
            fileInputStream = new FileInputStream(inputFile);
            fileInputStream.read(byteFile);
            fileInputStream.close();
            
            for (byte b: byteFile){
                int val = b;
                for (int i = 0; i < 8; i++)
                {
                    binFile.append((val & 128) == 0 ? 0 : 1);
                    val <<= 1;
                }
               
            }
            
            //System.out.println(binFile);
            System.out.println("Number of bits read from input file: " + 8 * byteFile.length);
        }
        catch (Exception e){
            e.printStackTrace();
        } 
        
        String finalBinFile = binFile.toString();
        return finalBinFile;
    }
    //write modified stream into file and convert to original format 
    /*private byte[] writeFile(String str){
    
      try{   
        File outputFile = new File("C:/Users/Cz4p3L/Desktop/test.bin"); //where to write file;;
          
        if(!outputFile.exists()){
            outputFile.createNewFile();
        }
        
        FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
	for(String a: binFile){
            fw.write(a);            
        }
	bw.close();
        System.out.println("Writing to file: DONE");
      }
      catch (IOException e){
        e.printStackTrace();
      }
      return byteFile;
    }*/
    //check speex mode
    public int checkMode(String s){
        s = s.substring(1, 5);
        mode = Integer.parseInt(s,2);
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
    */
    private String insertMessage(String str, String msg, int m){
        String[] arrayOfFrames;
        boolean endOfMsg=false;
        boolean endOfFrame=false;
        int T = 0;
        int sfT = 0;
        int i = 0;
        int j = 0;
        int initIndexVQ = 0;
        //int firstSubframe = 0;
        int numOfLSB = 0;
        int n = 1; //number of frame
        // initialize inserting message...
        switch(m){
            case 4:
                System.out.println("Speex mode: 4");
                T = 224;    //used to change index of next frame 
                initIndexVQ = 75; // start or end index? 
                //firstSubframe = 75;
                sfT=48; // start of next InnovationVQ subframe is at initIndexVQ += n*sfT; gdzie n <1,3>                
                break;
            case 5:
                System.out.println("Speex mode: 5");
                T = 304;
                initIndexVQ = 104;
                //firstSubframe = 104;
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
        
        System.out.println(T + "\n" + initIndexVQ + "\n" + sfT);
        
         StringBuilder originalStr = new StringBuilder(str);
        StringBuilder msgToHide = new StringBuilder(msg);
        
       
        
        System.out.println(originalStr.length() + "\n" + "Number of bits to hide: " + msgToHide.length());
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
        
        while(j < str.length() || !endOfMsg){
            arrayOfFrames = divideIntoFrames(str, mode);
           
            String tmpMsg;
            
            while(!endOfFrame)//when n <= 4
            {
                if (j + numOfLSB >= msgToHide.length()){                                
                    tmpMsg = msgToHide.substring(j, msgToHide.length());
                    System.out.println("last message " + tmpMsg);
            }
            else{
                tmpMsg = msgToHide.substring(j, j+numOfLSB);
                System.out.println("extracted from m2h " + tmpMsg);
                j +=  numOfLSB;                                       // to troche nie tak, nie mogę umieścić całej wiadomości w jednej ramce
            }
                System.out.println(n + " " + tmpMsg);
                n+=1;
                if(n>4){
                    endOfFrame = true;
                    n=1;
                }
            }
            endOfMsg = true;
        }  
        return "";
    }
    //Auxiliary functions
    public String[] divideIntoFrames (String str, int mode){
        
        int divisor, amountOfFrames;
        int i=0;
        int j=0;
        switch(mode){
            case 4:
                divisor = 224;
                break;
            case 5:
                divisor = 304;
                break;
            case 6:
                divisor = 368;
                break;
            default:
                divisor = 1;
                break;        
        } 
        amountOfFrames = str.length()/divisor;
        String arrayOfFrames[] = new String[amountOfFrames];
        while( i < amountOfFrames)
        {
            arrayOfFrames[i] = str.substring(j, j + divisor);
            j += divisor;
            i += 1;           
        }
        
        return arrayOfFrames;
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
   
    public static void main(String[] args){
      
      String basePath = "C:\\Users\\Cz4p3L\\Desktop\\Studia\\Magisterka\\speech_samples\\H1\\"; //base path for speech sample files
      
      SpeexFileReader sfr = new SpeexFileReader();
      String dataToHide = "this is hidden message";
      String bitStringToHide = sfr.convertToBitString(dataToHide);
      
      File inputFile = new File(basePath + "H110mode4.bin"); // placing input file
      String ad = sfr.readFile(inputFile);// string 
      sfr.checkMode(ad);
      sfr.insertMessage(ad, bitStringToHide, sfr.mode);      
      //sfr.writeFile(sfr.binFile);
      
    }

}
