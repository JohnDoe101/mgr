/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package speexfilereader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/* Author: Łukasz Czapla */
class CustomException extends Exception {

    public CustomException() {}
    
    public CustomException(String message)
    {
        super(message);
    }
}

public class SpeexFileReader implements CodewordEnergy {
    
    /* @Params:
    vectorQuantizationIndexes   Array with all computed VQ indices used for encoding voice sample
    inputByteFile               Input binary file with encoded voice sample
    outputByteFile              Output binary file with hidden message 
    vqSize                      Amount of bits spent on VQ parameter. Based on Speex mode 
    vqStart                     Set to 0
    sfT                         Set based on used Speex mode: 4/5/6. Parameter defines repetition of subframe params 
    frameT                      Size of frame in bits 
    initIndexVQ                 Indicates last bit of last VQ index. Based on used mode
    idxSize                     Size of index in bits. Based on used mode
    idxAmnt                     Amount of indexes in VQ parameter per subframe
    divisor                     Variable used for count amount of frames in sample of voice
    extractedMessage            String with extracted message after using CRIS#2
    binFile                     Array of strings to write to
    mode                        Calculated Speex mode based on input file
    */
    int [][] vectorQuantizationIndexes;
    int [][] vqiClone;
    byte[] inputByteFile;    // file to read from
    byte[] outputByteFile;
    int vqSize, vqStart, sfT, frameT, initIndexVQ;
    int idxSize, idxAmnt,idxStart;
    int divisor;
    String extractedMessage; // string with data to hide 
    StringBuilder binFile = new StringBuilder();   //array of strings to write to
    int mode;
    int efficiency;
    int distance;
    int usedMethod;
    
    static int USE_LSB_METHOD, USE_CRIS_METHOD;
        
    /*
        reading input Speex stream
    */
    public String readFile(File inputFile)
    {   
        FileInputStream fileInputStream;
        inputByteFile = new byte[(int) inputFile.length()];
        //System.out.println("Length of input file" + inputFile.length());
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
            //System.out.println("Number of bits read from input file: " + 8 * inputByteFile.length);
        }
        catch (Exception e){
            e.printStackTrace();
        } 
        
        String finalBinFile = binFile.toString();
        return finalBinFile;
    }
    /*
        write modified stream into file and convert to original format 
    */
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
            //System.out.println("tmpbyte " + tmpByte);
            outputByteFile[i] = (byte)tmpByte;
            //System.out.println("byte b:" + outputByteFile[i]);
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
        System.out.println("mode: " + mode);
        if (mode != 4 && mode != 5 && mode != 6){
            System.err.println("Usupported Speex mode!!!!");
            return -1;
        }
        setIdxVqParams(mode);
        return mode;
    }
    
    
    public void setIdxVqParams(int m){

        switch(m){
            case 4: 
                vqSize = 35;
                vqStart = 41;
                sfT = 48;
                idxSize = 7;
                idxAmnt = 5;
                divisor = 224;
                initIndexVQ = 75;
                frameT = divisor;
                break;
            case 5:
                vqSize = 48;
                vqStart = 57;
                sfT = 65;
                idxSize = 6;
                idxAmnt = 8;
                divisor = 304;
                initIndexVQ = 104;
                frameT = divisor;
                break;
            case 6:
                vqSize = 64;
                vqStart = 57;
                sfT = 81;
                idxSize = 8;
                idxAmnt = 8;
                divisor = 368;
                initIndexVQ = 120;
                frameT = divisor;
                break;
            default: 
                System.out.println("Unsupported Speex mode!!");
                idxSize = 1;
                idxAmnt = 1;
                divisor = 1;
                break;
        }
    }
    /*
    Function for sending VQ indices to file, for further processing
    */
    public void saveIndicesToFile(){
        String basePath = "C:\\Users\\Cz4p3L\\Desktop\\";
        String extension =".csv";
        String fileName;
        String outputFile;
        Scanner sc = new Scanner(System.in);
        System.out.println("Saving Indices...Please insert fileName...");
        fileName = sc.nextLine();
        outputFile = basePath + fileName + extension;
        
        try{
            FileWriter fw = new FileWriter(outputFile);
            int fr = 0;
            for (int[] vqi : vectorQuantizationIndexes) {
                for (int j = 0; j < vqi.length; j++) {
                    fw.append(fr++ + ",");
                    fw.append(Integer.toString(vqi[j]));
                    fw.append("\n");
                }
            }
            fw.close();
        }
        catch(Exception e){
            System.err.println("Error while saving indices to file");
        }
    }
    /* Insert hidden message into bitstream
    @params         String str, String msg, int m
    str - original voice sample (converted into 8-bit string representation)
    msg - converted message to hide (8-bit representation)
    m - mode of original voice sample 
    Function inserts messag into input bit stream.
    */
    private String insertMessage(String str, String msg, int m){
        String[] arrayOfFrames = null;
        String[] arrayOfRtp = null;
        boolean endOfFile=false;
        boolean endOfMsg=false;
        boolean endOfFrame;
        int i = 0;
        int j = 0;
        int numOfLSB = 0;
        int n = 1; //number of frame
        usedMethod=0;
        
        System.out.println("Which method would you like to use? ( LSB:1 / CRIS:2 )");
        do {
            Scanner scan = new Scanner(System.in);
            try{
                usedMethod = scan.nextInt();
            }
            catch(InputMismatchException e){
                System.out.println("Wrong format");
            }
        }while(usedMethod != USE_CRIS_METHOD && usedMethod != USE_LSB_METHOD);
            
        
        //System.out.println("T    " + frameT + "\n" + "IVQ  " + initIndexVQ + "\n" + "sfT  " + sfT);
        //System.out.println(str.length() + "\n" + "Number of bits to hide: " + msg.length());
        // scan for value how much LSB bits we want to use //
        
        if (usedMethod == USE_LSB_METHOD){
            while (numOfLSB < 1) {
                try {
                    Scanner sc = new Scanner(System.in);
                    System.out.println("How many LSB bits from InnovationVQ you want to use?");
                    numOfLSB = sc.nextInt();
                    try {
                        if (mode == 4 & numOfLSB > 35) {
                            throw new CustomException();
                        }
                        if (mode == 5 & numOfLSB > 48) {
                            throw new CustomException();
                        }
                        if (mode == 6 & numOfLSB > 64) {
                            throw new CustomException();
                        }
                    } catch (CustomException e) {
                        numOfLSB = 0;
                        System.out.println("You exceeded IVQ bit limit for mode " + mode + " !!");
                    }

                } catch (InputMismatchException e) {
                    System.out.println("Input mismatch exception: " + e + "\nYou should use number!");
                }

            }
            arrayOfFrames = divideIntoFrames(str, mode);
            //checkSubstitutionCapability(vectorQuantizationIndexes, refactorizedCodewordMap);
            //saveIndicesToFile();
            String tmpMsg;

            while (!endOfFile && !endOfMsg) {

                endOfFrame = false;
                StringBuffer sb = new StringBuffer(arrayOfFrames[i]);
                while (!endOfFrame && !endOfMsg) {
                    if (j + numOfLSB >= msg.length()) {
                        tmpMsg = msg.substring(j, msg.length());
                        endOfMsg = true;
                        System.out.println("Loop finished due to end of message, frames affected:" + (i + 1));
                        //System.out.println("last message " + tmpMsg);
                    } else {
                        tmpMsg = msg.substring(j, j + numOfLSB);
                        //System.out.println("extracted from m2h " + tmpMsg);
                        j += numOfLSB;
                    }
                    //System.out.println("before: " + i + " " + sb);
                    sb = sb.replace(initIndexVQ - tmpMsg.length() + 1, initIndexVQ + 1, tmpMsg);  // tutaj potrzebny jest jakis SB
                    arrayOfFrames[i] = sb.toString();
                    //System.out.println("after : " + i + " " + arrayOfFrames[i]);
                    initIndexVQ += sfT;
                    n += 1;
                    if (n == 5) {
                        if (mode == 4) {
                            initIndexVQ = 75;
                        } else if (mode == 5) {
                            initIndexVQ = 104;
                        } else if (mode == 6) {
                            initIndexVQ = 120;
                        }
                        i += 1;
                        n = 1;
                        endOfFrame = true;
                    }

                }

                if (i == arrayOfFrames.length) {
                    System.out.println("Loop finished due to end of file");
                    endOfFile = true;
                }
            }
        }
        else if (usedMethod == USE_CRIS_METHOD){
            switch(mode){
                case 4:
                    initEnergyMap(codewordEnergyMode4, codewordEnergyMapMode4);
                    break;
                case 5:
                    initEnergyMap(codewordEnergyMode5, codewordEnergyMapMode5);
                    break;
                case 6:
                    initEnergyMap(codewordEnergyMode6, codewordEnergyMapMode6);
                    break;
                default:
                    break;
            }
            arrayOfFrames = divideIntoFrames(str, mode);
            Scanner sc1 = new Scanner(System.in);
            efficiency = 0;
            System.out.println("Provide hiding efficiency:");
            do{
                try{
                    efficiency = sc1.nextInt();
                }
                catch(InputMismatchException e){
                    System.out.println("Wrong format");
                }
            }
            while(!(efficiency > 0 && efficiency <= 4));
            int curr, next;
            int redBits, offset;
            int intValTmpMsg, exchIdx;
            int lastUsedFrame;
            
            lastUsedFrame = 0;
            String tmpMsg;           
            offset = 0;
            vqiClone = vectorQuantizationIndexes.clone();
            System.out.println("bit redundancy map \n" + bitRedundancyMap);
            System.out.println("idx substitution map \n" + refactorizedCodewordMap);
            /*
                Index substitution with hidden information
            */
            for(i=0;i<vqiClone.length;i+=efficiency){
                for(j=0; j<vqiClone[i].length;j++){
                    curr = vqiClone[i][j];
                    next = refactorizedCodewordMap.get(curr);
                    //System.out.println("curr " + curr);
                    //System.out.println("next " + next);
                    if(curr != next){
                        redBits = bitRedundancyMap.get(curr);
                        //System.out.println("m2h length: " + msg.length());
                        if (redBits > 0 && redBits <= 4){
                            if (offset + 1 == msg.length()) {
                                endOfMsg = true;
                                //System.out.println("last frame i " + i);
                                tmpMsg = msg.substring(offset, msg.length());
                                intValTmpMsg = Integer.parseInt(tmpMsg, 2) << (idxSize - tmpMsg.length());
                                exchIdx = next + intValTmpMsg;
                            } else {               // podmiana kazdej innej
                                tmpMsg = msg.substring(offset, offset+1);
                                intValTmpMsg = Integer.parseInt(tmpMsg, 2) << (idxSize - tmpMsg.length());
                                //System.out.println("tmpMsg str: " + tmpMsg);
                                //System.out.println("tmpMsg int: " + intValTmpMsg);
                                exchIdx = next + intValTmpMsg;
                                offset += 1;
                            }
                            if (endOfMsg) {
                                lastUsedFrame = i;
                                System.out.println("last used frame, idx value: " + i);
                                vqiClone[i][j] = exchIdx;
                                break;
                            }
                            System.out.println("normal frame " + exchIdx);
                            vqiClone[i][j] = exchIdx;
                        }
                    }
                }
                if (endOfMsg) break;
            }          
            /*
                Replacing indexes in VQ parameter
            */
            String strIdx, str2sub = "";
            int str2subCounter = 0;
            
            offset = 0;
            for (i=0; i < arrayOfFrames.length; i+=efficiency){
                if (i==lastUsedFrame) break;
                StringBuffer sb = new StringBuffer(arrayOfFrames[i]);
                for (int idx: vqiClone[i]){
                    strIdx = String.format("%" + idxSize + "s", Integer.toBinaryString(idx)).replace(" ", "0");
                    str2sub+=strIdx;
                    str2subCounter++;
                    if (str2subCounter == idxAmnt){ // changing VQ inside subframe
                        sb.replace(vqStart+offset, vqStart+vqSize+offset, str2sub);
                        offset+=sfT;
                        str2subCounter = 0;
                        str2sub = "";
                    }
                }
                arrayOfFrames[i] = sb.toString();
                offset = 0;
            }
            
            /*
                Creating RTP packet
            */
            int payloadType;
            int csrcSize=1;
            int[] csrc = {1};
            int[][] distancePval = {{31,81},{32,82},{33,83},{34,84},{35,85},
                                    {36,86},{37,87},{38,88},{39,89},{40,90},
                                    {41,91},{42,92},{43,93},{44,94},{45,95},
                                    };
            Random rand = new Random();
            
            payloadType = distancePval[distance-1][rand.nextInt(2)];
            arrayOfRtp = new String[arrayOfFrames.length];    
            boolean isPadding=false;
            int paddingSize = 0;
            for (i=0; i<arrayOfFrames.length;i++){
                paddingSize = arrayOfFrames[i].length()%32;
                if (paddingSize != 0){
                    isPadding = true;                   
                }
                if (i%efficiency != 0 || i > lastUsedFrame){
                    arrayOfRtp[i] = createRtpPacket(2, isPadding, false, csrcSize, false, payloadType, i, System.currentTimeMillis(),0, csrc, arrayOfFrames[i], paddingSize);     // tutaj mark = 0, no frame affected
                }
                else{
                    arrayOfRtp[i] = createRtpPacket(2, isPadding, false, csrcSize, true, payloadType, i, System.currentTimeMillis(), 0, csrc, arrayOfFrames[i], paddingSize);     // mark = 1, frame affected                    
                }
            }
        }
        
        
        StringBuilder sb = new StringBuilder();
        if (usedMethod == USE_LSB_METHOD){
            for (String s : arrayOfFrames) {
                sb.append(s);
            }
        }
        else{
            for (String s : arrayOfRtp) {
                sb.append(s);
            }
        }
        
        
        return sb.toString();
    }
    //Auxiliary functions
    public String createRtpPacket(int version, boolean isPad, boolean isExt,int csrc, boolean marker, int payloadType, int sn, long ts, int ssrc, int[] csrcList, String payload, int padSize){
        RTP rtp = new RTP(version, isPad, isExt, csrc, marker, payloadType, sn, ts, ssrc, csrcList, payload, padSize);
        return rtp.getAllRtpFields();
    }
    
    public int checkUsedMethod(){
        if (usedMethod == USE_LSB_METHOD) return 1;
        else return 2;
    }
    public String[] divideIntoFrames (String str, int mode){
        
        int amountOfFrames;
        idxStart=0; 
        int i=0;
        int j=0;

        if (idxSize == 1 && idxAmnt == 1 || divisor == 1) {
            return null;
        }
        amountOfFrames = str.length()/divisor;
        vectorQuantizationIndexes = new int[amountOfFrames][4*idxAmnt];
        String arrayOfFrames[] = new String[amountOfFrames];
        while( i < amountOfFrames)
        {
            arrayOfFrames[i] = str.substring(j, j + divisor);
            getIndexVQ(arrayOfFrames[i], i, idxAmnt, idxSize,idxStart);
            j += divisor;
            i += 1;           
        }
        
        return arrayOfFrames;
    }
    
    
    public void checkSubstitutionCapability(int[][] idxArr, Map<Integer, Integer> refedMap){
        int counter=0;
        int all=0;
        for(int[] i: idxArr){
            for(int j=0;j<i.length;j++){
                all++;
                if (i[j] != refedMap.get(i[j]) ){
                    counter++;
                }
            }
        }
        System.out.println("counter: " + counter + "\nAll: " + all + "\npercentage: " + (float)counter*100/all +" %");    
    }
    
    public void getIndexVQ(String frame, int frameNo, int indexAmount, int indexBitSize, int indexStart){
        String tmpVQ; //vector quantization indices from subframe
        boolean eoVQ;//end of frame
        int j,k,l,m;
        l=0;
        j=vqStart;
        //System.out.println("start: " + frameNo);
        for(int i=0; i<4;i++){            
            k=indexAmount;
            m=indexStart;
            tmpVQ = frame.substring(j,j+vqSize);
            //System.out.println("tmpVQ " + tmpVQ + "\n " + i);
            //System.out.println("length of VQ" + tmpVQ.length() + "\nVQ content " + tmpVQ);
            do{
                vectorQuantizationIndexes[frameNo][l++] = Integer.parseInt(tmpVQ.substring(m, m + indexBitSize), 2);        // i-th frame,
                //System.out.println("vectorQuantizationIndexes[i][l-1]" + vectorQuantizationIndexes[frameNo][l-1]);
                m+=indexBitSize;
                k--;
                eoVQ = k==0;
            }
            while(!eoVQ);
            j += sfT;
        }
        //System.out.println("end");
        j=vqStart;        
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
        System.out.println("length: " + (binary.toString()).length());
        return binary.toString();
    }
    /*
    Use of this function is to initialize EnegyMap for specific mode
    Needed for further processing.
    */
    public void initEnergyMap(float[] cdbk, Map<Integer,Float> m){
        int k = 0;
        int threshold = 0;
        for(float v: cdbk){
            m.put(k, v);
            refactorizedCodewordMap.put(k, k);  // init refactorized energy map with unchanged values. If better index couldn't be find we left original.
            k++;
        }
        //displayMap(m);
        System.out.println("Refactorizing CodewordEnergy Map...");
        do{
            try{
                Scanner sc = new Scanner(System.in);
                System.out.println("Give acceptable threshold...");
                threshold = sc.nextInt();             
            }
            catch(InputMismatchException e){
                threshold = 0;              
            }           
        }while(threshold==0 || threshold>15);
        //threshold = 15;
        distance = threshold;
        refactorEnergyMap(m, refactorizedCodewordMap,threshold);
        /*int chC=0;
        for(int w=0; w < refactorizedCodewordMap.size(); w++){
            if (w != refactorizedCodewordMap.get(w)) chC++; 
        }
        System.out.println("chC " + (float)chC/(1<<idxSize) * 100 + " %");*/
    }
    
    /*
    Function used for refactorization codeword energy Map. If finds possible redundancy in 0-1 
    codebook index notation. Function rely on provided by user threshold in "%" ( maximum 
    applicable decrease of original odeword energy and new codeword).
    */
    public void refactorEnergyMap(Map<Integer,Float> m, Map<Integer, Integer> m2, int threshold){
        float item, next;
        float tmpItem;
        String _old, _new;
        int _oldSize, _newSize;
        int redundancy =0;
        for (int i=0; i<m.size(); i++){
            item = m.get(i);
            tmpItem = item * (1.f - (float)(threshold*0.01f));
            _old = Integer.toBinaryString(i);
            _oldSize = _old.length();
            
            for(int j=0;j<m.size();j++){
                if (i == j) continue;
                _new = Integer.toBinaryString(j);                
                next = m.get(j);
                if (item <= next){
                    continue;
                } 
                else{                   
                    if (next >= tmpItem){ //   if tmpItem=<next<item then replace
                        _newSize = _new.length();
                        if (_oldSize > _newSize){
                            _oldSize = String.format("%"+ idxSize + "s", Integer.toBinaryString(i)).replace(" ", "0").length();
                            if (_oldSize - _newSize > redundancy){
                                redundancy = _oldSize - _newSize;
                                m2.replace(i, j);
                            }                           
                        }                       
                    }
                    else{    
                        continue;
                    }
                }                
            }
            bitRedundancyMap.put(i, redundancy);
            redundancy = 0;
        }
        
    }
    
    /**
     *
     * @param str SpeexFrame with hidden data placed
     * @param usedTh used threshold for calculating substitional indexes
     * @return Speex frame without hidden data (this will be stored in 
     */
    public String extractAudioPayload(String str, int usedTh){
        extractedMessage="";
        //getIndexVQ(str, frameT, idxAmnt, idxSize, idxStart);
        return "";
    }
    
    
    /*
    String tmpVQ; //vector quantization indices from subframe
        boolean eoVQ;//end of frame
        int j,k,l,m;
        l=0;
        j=vqStart;
        //System.out.println("start: " + frameNo);
        for(int i=0; i<4;i++){            
            k=indexAmount;
            m=indexStart;
            tmpVQ = frame.substring(j,j+vqSize);
            //System.out.println("tmpVQ " + tmpVQ + "\n " + i);
            //System.out.println("length of VQ" + tmpVQ.length() + "\nVQ content " + tmpVQ);
            do{
                vectorQuantizationIndexes[frameNo][l++] = Integer.parseInt(tmpVQ.substring(m, m + indexBitSize), 2);        // i-th frame,
                //System.out.println("vectorQuantizationIndexes[i][l-1]" + vectorQuantizationIndexes[frameNo][l-1]);
                m+=indexBitSize;
                k--;
                eoVQ = k==0;
            }
            while(!eoVQ);
            j += sfT;
        }
        //System.out.println("end");
        j=vqStart;        
    }
    */
/* 
   Main function for testing steganographic method 
   */   
    public static void main(String[] args){
        
        USE_LSB_METHOD = 1;
        USE_CRIS_METHOD = 2;
      
        String basePath = "C:\\Users\\Cz4p3L\\Desktop\\Studia\\Magisterka\\speech_samples\\H57\\"; //base path for speech sample files
        String basePathLCZ = "C:\\Users\\lukasz.czapla\\Desktop\\mgr\\Magisterka\\speech_samples\\H57\\";
        /*String[] sampleArrFile = {"H11mode4.bin", "H12mode4.bin","H13mode4.bin","H14mode4.bin","H15mode4.bin","H16mode4.bin","H17mode4.bin","H18mode4.bin","H19mode4.bin","H110mode4.bin",
                                "H11mode5.bin","H12mode5.bin","H13mode5.bin","H14mode5.bin","H15mode5.bin","H16mode5.bin","H17mode5.bin","H18mode5.bin","H19mode5.bin","H110mode5.bin",
                                "H11mode6.bin","H12mode6.bin","H13mode6.bin","H14mode6.bin","H15mode6.bin","H16mode6.bin","H17mode6.bin","H18mode6.bin","H19mode6.bin","H110mode6.bin"};*/
        String[] sampleArrFile = {"H571mode4.bin", "H572mode4.bin","H573mode4.bin","H574mode4.bin","H575mode4.bin","H576mode4.bin","H577mode4.bin","H578mode4.bin","H579mode4.bin","H5710mode4.bin",
                                "H571mode5.bin","H572mode5.bin","H573mode5.bin","H574mode5.bin","H575mode5.bin","H576mode5.bin","H577mode5.bin","H578mode5.bin","H579mode5.bin","H5710mode5.bin",
                                "H571mode6.bin","H572mode6.bin","H573mode6.bin","H574mode6.bin","H575mode6.bin","H576mode6.bin","H577mode6.bin","H578mode6.bin","H579mode6.bin","H5710mode6.bin"};
        String file  = "H571mode4.bin";
        String dataToHide = "hidden message to sent";
        //    refactorizedCodewordMap.clear();
        System.out.println("Sample fileName: " + file);
        
        SpeexFileReader sfr = new SpeexFileReader();
        String bitStringToHide = sfr.convertToBitString(dataToHide);
      
        File inputFile = new File(basePath + file); // placing input file
        String inputFileString = sfr.readFile(inputFile);// string 
        sfr.checkMode(inputFileString);
        String strAfterInsert = sfr.insertMessage(inputFileString, bitStringToHide, sfr.mode);
        
        if (sfr.checkUsedMethod() == USE_LSB_METHOD) sfr.writeFile(strAfterInsert);
        else {
            //System.out.println("using 2nd method");
            String tmp;
            StringBuilder sb = new StringBuilder();
            int frame=0;
            for (int i=0;i<strAfterInsert.length();i+=352){
                tmp = strAfterInsert.substring(i, i+352);
                int usedTh;
                RTP rtp;
                rtp = RTP.parseRTP(tmp);
                if(rtp.getMarker().equals("1")){
                    usedTh = Integer.parseInt(rtp.getPayloadType(),2);
                    if (usedTh >=35 && usedTh <= 45){
                        //System.out.println("first group");
                        usedTh-=30;
                    }
                    else{
                        //System.out.println("second grup");
                        usedTh-=80;
                    }
                    //System.out.println("used th " + usedTh);
                    sb.append(sfr.extractAudioPayload(rtp.getPayload(), usedTh));
                    break;
                }
                else{
                    sb.append(rtp.getPayload());
                }              
            }
            sfr.writeFile(sb.toString());
        }   
    }      
}
