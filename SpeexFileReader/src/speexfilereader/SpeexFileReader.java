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
import java.util.Arrays;
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
    float[] usedCdbk;
    byte[] inputByteFile;    // file to read from
    byte[] outputByteFile;
    int vqSize, vqStart, sfT, frameT, initIndexVQ;
    int idxSize, idxAmnt,idxStart;
    int divisor;
    String extractedMessage = ""; // string with data to hide 
    StringBuilder binFile = new StringBuilder();   //array of strings to write to
    int mode;
    int efficiency;
    int distance;
    int usedMethod;
    int lastUsedFrame;
    
    static int USE_LSB_METHOD, USE_ECBD_METHOD;
        
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
        String outputPathLCZ = "C:\\Users\\lukasz.czapla\\Desktop\\";
        String extension = ".bin";
        Scanner sc = new Scanner(System.in);
        System.out.println("Please insert fileName");
        String outputFileName = sc.nextLine();
        String fileDest = outputPathLCZ + outputFileName + extension;
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
                usedCdbk = codewordEnergyMode4.clone();
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
                usedCdbk = codewordEnergyMode5.clone();
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
                usedCdbk = codewordEnergyMode6.clone();
                break;
            default: 
                System.err.println("Unsupported Speex mode!!");
                break;
        }
    }
    /*
    Function for sending VQ indices to file, for further processing
    */
    /*public void saveIndicesToFile(){
        String basePath = "C:\\Users\\Cz4p3L\\Desktop\\";
        String extension =".csv";
        String fileName;
        String outputFile;
        Scanner sc = new Scanner(System.in);
        System.out.println("Saving Indices...Please insert fileName...");
        fileName = sc.nextLine();
        outputFile = basePath + fileName + extension;
        
        try(FileWriter fw = new FileWriter(outputFile)) {
            int fr = 0;
            for (int[] vqi : vectorQuantizationIndexes) {
                for (int j = 0; j < vqi.length; j++) {
                    fw.append(fr++ + ",");
                    fw.append(Integer.toString(vqi[j]));
                    fw.append("\n");
                }
            }
        }
        catch(Exception e){
            System.err.println("Error while saving indices to file");
        }
    }*/
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
        //System.out.println("msg " + msg);
        
        System.out.println("Which method would you like to use? ( LSB:1 / CBD:2 )");
        do {
            Scanner scan = new Scanner(System.in);
            try{
                usedMethod = scan.nextInt();
            }
            catch(InputMismatchException e){
                System.out.println("Wrong format");
            }
        }while(usedMethod != USE_ECBD_METHOD && usedMethod != USE_LSB_METHOD);
            
        
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
        else if (usedMethod == USE_ECBD_METHOD){    // ECBD - Equally CodeBook Division
            
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
            
            String stegoBit;
            int buff=0;
            int bitsInserted=0;
            System.out.println("msg len :" + msg.length());
            
                for (i=0; i< vectorQuantizationIndexes.length; i+=efficiency){
                    for(j=0; j < vectorQuantizationIndexes[i].length; j++){
                        if(buff == msg.length()-1){        // insert last message                           
                            stegoBit = msg.substring(buff, msg.length());
                            if(insertStegoBit(i, j, vectorQuantizationIndexes[i][j], stegoBit)){
                                lastUsedFrame = i;
                                bitsInserted++;
                                endOfMsg = true;
                            }
                            else{
                                continue;
                            }                            
                        }
                        else{
                            stegoBit = msg.substring(buff, buff+1);
                            if(insertStegoBit(i, j, vectorQuantizationIndexes[i][j], stegoBit)){
                                bitsInserted++;
                                buff++;
                            }
                            else{
                                continue;
                            }
                        }                    
                        if (endOfMsg == true || endOfFile == true) break;
                    }
                    if (endOfMsg == true || endOfFile == true) break;
                } 
            /*
                Insert substitued indices into array of frames
            */
            for (i=0; i < arrayOfFrames.length; i++){
                if (i > lastUsedFrame) break;               
                arrayOfFrames[i] = setIndicesInFrame(arrayOfFrames[i], vectorQuantizationIndexes[i]);
            }
            
            /*
                Creating RTP packet
            */
            int payloadType;
            int csrcSize=1;
            int[] csrc = {1};
            int[] distancePval = {31,32,33};
            Random rand = new Random();
            
            payloadType = distancePval[rand.nextInt(3)];
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
    public String setIndicesInFrame(String frame, int[] vqi){
        
        StringBuffer bufFrame = new StringBuffer(frame);
        
        System.out.println("bufFrame " + bufFrame);
        String strIdx, str2sub = "";
        int str2subCounter = 0;
        int offset=0;
        
        for (int idx : vqi) {
            strIdx = String.format("%" + idxSize + "s", Integer.toBinaryString(idx)).replace(" ", "0");
            str2sub += strIdx;
            str2subCounter++;
            if (str2subCounter == idxAmnt) { // changing VQ inside subframe
                bufFrame.replace(vqStart + offset, vqStart + vqSize + offset, str2sub);
                offset += sfT;
                str2subCounter = 0;
                str2sub = "";
            }
        }      
        return bufFrame.toString();
    }  
    
    public boolean insertStegoBit(int frame, int vqiPos, int vqiVal, String bit){   //
        
        if (vqiVal < edivisiveIndices[0] && bit.equals("0")){   // OK - don't need to exchange anything
            return true;
        }
        else if (vqiVal < edivisiveIndices[2] && vqiVal >= edivisiveIndices[1]  && bit.equals("0")){ // 0 to hide, index points 1
            vectorQuantizationIndexes[frame][vqiPos] = findVqiSubstitution('0', vqiVal, usedCdbk);
            return true;
        }
        else if (vqiVal < edivisiveIndices[2] && vqiVal >= edivisiveIndices[1] && bit.equals("1")){ // OK - don't need to exchange anything
            return true;
        }
        else if (vqiVal < edivisiveIndices[0] && bit.equals("1")){  // 1 to hide, index points 0
            vectorQuantizationIndexes[frame][vqiPos] = findVqiSubstitution('1', vqiVal, usedCdbk);
            return true;
        }
        else{
            System.out.println("skip iteration");
            return false;
        }
    }
    
    /**
     *
     * @param c bit to hide
     * @param vqiVal actual value of index
     * @param uc codebook used for encoding 
     * @return
     */
    public int findVqiSubstitution(char c, int vqiVal, float[] uc){
        
        float currEn, distance;
        int vqSubInd;
        vqSubInd = 0;
        distance = Float.MAX_VALUE;
        
        if (c == '0'){
            currEn = uc[vqiVal];
            for(int i=0; i < edivisiveIndices[0]; i++){
                if (Math.abs(currEn - uc[i]) < distance){
                    distance = Math.abs(currEn - uc[i]);
                    vqSubInd = i;
                }
            }
            return vqSubInd;
        }
        else{
            currEn = uc[vqiVal];
            for(int i=edivisiveIndices[1]; i < edivisiveIndices[2]; i++){
                if (Math.abs(currEn - uc[i]) < distance){
                    distance = Math.abs(currEn - uc[i]);
                    vqSubInd = i;
                }
            }
            return vqSubInd;
        }
    }
    
    public String createRtpPacket(int version, boolean isPad, boolean isExt,int csrc, boolean marker, int payloadType, int sn, long ts, int ssrc, int[] csrcList, String payload, int padSize){
        RTP rtp = new RTP(version, isPad, isExt, csrc, marker, payloadType, sn, ts, ssrc, csrcList, payload, padSize);
        return rtp.getAllRtpFields();
    }
    
    public int checkUsedMethod(){
        if (usedMethod == USE_LSB_METHOD) return 1;
        else return 2;
    }
    
    public int checkLastUsedFrame(){
        return lastUsedFrame;
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
    /*
        Convert hidden message to array of strings, it is simplier to insert into stream
    */
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
        System.out.println("Bit string to hide:\n " + binary.toString());
        return binary.toString();
    }
    /*
    Use of this function is to initialize EnegyMap for specific mode
    Needed for further processing.
    */
    /*public void initEnergyMap(float[] cdbk, Map<Integer,Float> m){
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
        //refactorEnergyMap(m, refactorizedCodewordMap,threshold);
        /*int chC=0;
        for(int w=0; w < refactorizedCodewordMap.size(); w++){
            if (w != refactorizedCodewordMap.get(w)) chC++; 
        }
        System.out.println("chC " + (float)chC/(1<<idxSize) * 100 + " %");
    }*/
    
    /*
    Function used for refactorization codeword energy Map. If finds possible redundancy in 0-1 
    codebook index notation. Function rely on provided by user threshold in "%" ( maximum 
    applicable decrease of original odeword energy and new codeword).
    @param m - map with original energy levels for appropriate index 
           m2- map with substituted index
           threshold - maximum energy decrease for seeked index
    */
    /*public void refactorEnergyMap(Map<Integer,Float> m, Map<Integer, Integer> m2, int threshold){
        
        Integer[] alreadyUsedIndices = new Integer[1<<idxSize];
        int redundancy =0;
        int nBest=0;
        Scanner sc = new Scanner(System.in);
        
        do{
            System.out.println("How many n-best indices find?");
            try{
                nBest = sc.nextInt();
            }
            catch(Exception e){
                System.out.println("Caught exception " + e);
            }
        }while(nBest==0 || nBest > 10);
        
        Integer[][] foundNBestIndices = new Integer[1<<idxSize][nBest]; // look for up to 5 indexes
        System.out.println("size of energy map ");
        for (int i=0; i<m.size(); i++){
            foundNBestIndices[i] = findNBestIndices(i, m, threshold, nBest);
        }    
        
        
        
        System.out.println("array with indices");
        int k=0;
        for (Integer[] i: foundNBestIndices){
            System.out.print(k++ + ":");
            for(Integer j: i){
                System.out.print(" " + j);
            }
            System.out.println(" ");
        
        }
        
        
            //bitRedundancyMap.put(i, redundancy);
            //redundancy = 0;
        
        
    }*/
    /*
        Find n-best indices for provided threshold.
        @param currentIndex
               m
               m2
               threshold
               nBest
    
    public Integer[] findNBestIndices(int currentIndex, Map<Integer,Float> m, int threshold, int nBest){
        float oldEnergy, newEnergy, tmpEnergy;
        String _oldEnergyBinary, _newEnergyBinary;
        int _oldEnergyBinaryLength, _newEnergyBinaryLength;
        int maxVal = Integer.MIN_VALUE;
        int maxPos=nBest-1;
        Integer[] nBestIndices = new Integer[nBest];
        oldEnergy = m.get(currentIndex);
        tmpEnergy = oldEnergy * (1.f - (float)(threshold*0.01f));
        _oldEnergyBinary = Integer.toBinaryString(currentIndex);
        _oldEnergyBinaryLength = _oldEnergyBinary.length();
        int nullIdx;
        for(int i=0;i<m.size();i++){
            newEnergy = m.get(i);
            _newEnergyBinary = Integer.toBinaryString(i);                
            _newEnergyBinaryLength = _newEnergyBinary.length();
            if (tmpEnergy < newEnergy && newEnergy < oldEnergy){  // if index can be used do sth
                if (_oldEnergyBinaryLength - _newEnergyBinaryLength > 0){
                    if ((nullIdx = Arrays.asList(nBestIndices).indexOf(null)) != -1) {
                        nBestIndices[nullIdx] = i;
                    }
                    else {
                        for(int j=0; j<nBestIndices.length;j++){        // looking for biggest value in array, don't care about energy value for index
                            if (nBestIndices[j] > maxVal){
                                maxVal = nBestIndices[j];   
                                maxPos = j;
                            } 
                        }
                        nBestIndices[maxPos] = i;
                    }               
                }
                else if(_oldEnergyBinaryLength == _newEnergyBinaryLength && _oldEnergyBinaryLength != idxSize){   // length difference is equal 0 and size != idxSize
                    if ((nullIdx = Arrays.asList(nBestIndices).indexOf(null)) != -1) {
                        nBestIndices[nullIdx] = i;
                    }
                    else {
                        for(int j=0; j<nBestIndices.length;j++){
                            if (nBestIndices[j] > maxVal){
                                maxVal = nBestIndices[j];   
                                maxPos = j;
                            } 
                        }
                        nBestIndices[maxPos] = i;
                    }
                }   
                else{ // if  we cannoot find check in longer indices 
                    if ((nullIdx = Arrays.asList(nBestIndices).indexOf(null)) != -1) {
                        nBestIndices[nullIdx] = i;
                    }
                    else {
                        for(int j=0; j<nBestIndices.length;j++){
                            if (nBestIndices[j] > maxVal){
                                maxVal = nBestIndices[j];   
                                maxPos = j;
                            } 
                        }
                        nBestIndices[maxPos] = i;
                    }
                }              
            }       
        }
        return nBestIndices;
    }*/
    
    /*
        show decoded message
    */
    public void showMessage(){
        System.out.println("Decoded message " + extractedMessage);
        String rm = extractedMessage.substring(0, extractedMessage.length()-extractedMessage.length()%8);
        int val;
        String tmp;
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<rm.length();i+=8){
            tmp = rm.substring(i, i+8);
            val = Integer.parseInt(tmp, 2);
            sb.append((char)val);
        }
        System.out.println("sb " + sb);

        
        
    }
    /**
     *
     * @param str SpeexFrame with hidden data placed
     * @param lastUsedFrame
     * @param frameNo
     * @return Speex frame without hidden data (this will be stored in 
     */
    public String extractAudioPayload(String str, int lastUsedFrame, int frameNo){
        
        int[][] frame;
        frame = getIndexesVQfromRTP(str, idxAmnt, idxSize, idxStart);
        for(int i=0; i<frame[0].length;i++){
            if (frame[0][i] < edivisiveIndices[0]){
                extractedMessage += "0";
            }
            else if (frame[0][i] >= edivisiveIndices[1] && frame[0][i] < edivisiveIndices[2]){
                extractedMessage += "1";
            }
        }       
        return str;
    }
    
    public int[][] getIndexesVQfromRTP(String str, int idxAmnt, int idxSize, int idxStart){
        String tmpVQ; //vector quantization indices from subframe
        boolean eoVQ;//end of frame
        int j,k,l,m;
        l=0;
        j=vqStart;
        int[][] frame = new int[1][4*idxAmnt];
        for(int i=0; i<4;i++){            
            k=idxAmnt;
            m=idxStart;
            tmpVQ = str.substring(j,j+vqSize);
            //System.out.println("tmpVQ " + tmpVQ + "\n " + i);
            //System.out.println("length of VQ" + tmpVQ.length() + "\nVQ content " + tmpVQ);
            do{
                frame[0][l++] = Integer.parseInt(tmpVQ.substring(m, m + idxSize), 2);        // i-th frame,
                //System.out.println("vectorQuantizationIndexes[i][l-1]" + vectorQuantizationIndexes[frameNo][l-1]);
                m+=idxSize;
                k--;
                eoVQ = k==0;
            }
            while(!eoVQ);
            j += sfT;
        }
        //System.out.println("end");
        //j=vqStart;
        return frame;
    }

/* 
   Main function for testing steganographic method 
   */   
    public static void main(String[] args){
        
        USE_LSB_METHOD = 1;
        USE_ECBD_METHOD = 2;
      
        String basePath = "C:\\Users\\Cz4p3L\\Desktop\\Studia\\Magisterka\\speech_samples\\H57\\"; //base path for speech sample files
        String basePathLCZ = "C:\\Users\\lukasz.czapla\\Desktop\\mgr\\Magisterka\\speech_samples\\H57\\";
        /*String[] sampleArrFile = {"H11mode4.bin", "H12mode4.bin","H13mode4.bin","H14mode4.bin","H15mode4.bin","H16mode4.bin","H17mode4.bin","H18mode4.bin","H19mode4.bin","H110mode4.bin",
                                "H11mode5.bin","H12mode5.bin","H13mode5.bin","H14mode5.bin","H15mode5.bin","H16mode5.bin","H17mode5.bin","H18mode5.bin","H19mode5.bin","H110mode5.bin",
                                "H11mode6.bin","H12mode6.bin","H13mode6.bin","H14mode6.bin","H15mode6.bin","H16mode6.bin","H17mode6.bin","H18mode6.bin","H19mode6.bin","H110mode6.bin"};*/
        String[] sampleArrFile = {"H571mode4.bin", "H572mode4.bin","H573mode4.bin","H574mode4.bin","H575mode4.bin","H576mode4.bin","H577mode4.bin","H578mode4.bin","H579mode4.bin","H5710mode4.bin",
                                "H571mode5.bin","H572mode5.bin","H573mode5.bin","H574mode5.bin","H575mode5.bin","H576mode5.bin","H577mode5.bin","H578mode5.bin","H579mode5.bin","H5710mode5.bin",
                                "H571mode6.bin","H572mode6.bin","H573mode6.bin","H574mode6.bin","H575mode6.bin","H576mode6.bin","H577mode6.bin","H578mode6.bin","H579mode6.bin","H5710mode6.bin"};
        String file  = "H571mode4.bin";
        String dataToHide = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        System.out.println("Sample fileName: " + file);
        
        SpeexFileReader sfr = new SpeexFileReader();
        String bitStringToHide = sfr.convertToBitString(dataToHide);
      
        File inputFile = new File(basePathLCZ + file); // placing input file
        String inputFileString = sfr.readFile(inputFile);// string 
        sfr.checkMode(inputFileString);
        String strAfterInsert = sfr.insertMessage(inputFileString, bitStringToHide, sfr.mode);
        
        System.out.println("last frame " + sfr.checkLastUsedFrame());
        
        int luf = sfr.checkLastUsedFrame();
        
        if (sfr.checkUsedMethod() == USE_LSB_METHOD) sfr.writeFile(strAfterInsert);
        else {
            //System.out.println("using 2nd method");
            String tmp;
            StringBuilder sb = new StringBuilder();
            int frame=0;
            for (int i=0;i<strAfterInsert.length();i+=352){
                tmp = strAfterInsert.substring(i, i+352);
                RTP rtp;
                rtp = RTP.parseRTP(tmp);
                if(rtp.getMarker().equals("1")){
                    //System.out.println("used th " + usedTh);
                    sb.append(sfr.extractAudioPayload(rtp.getPayload(), luf, frame));
                    //break;
                }
                else{
                    sb.append(rtp.getPayload());
                }  
                frame++;
            }
            sfr.showMessage();
            sfr.writeFile(sb.toString());
        }
    }      
}
