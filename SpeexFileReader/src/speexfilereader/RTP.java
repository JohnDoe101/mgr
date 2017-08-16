/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package speexfilereader;

/**
 *
 * @author lukasz.czapla
 * Class made for preparing RTP packet, with already inserted hidden data into 
 * bit stream.
 * 
 */
public class RTP {
    
    /* Sizes of RTP packet fields; base unit : bit
    */
    private static int VERSION_FIELD_SIZE = 2;
    private static int ISPADDING_FIELD_SIZE = 1;
    private static int EXTENSION_FIELD_SIZE = 1;
    private static int CSRC_FIELD_SIZE = 4;
    private static int MARKER_FIELD_SIZE = 1;
    private static int PT_FIELD_SIZE = 7;
    private static int SN_FIELD_SIZE = 16;
    private static int TIMESTAMP_FIELD_SIZE = 32;
    private static int SSRC_FIELD_SIZE = 32;
    
    private static int BASIC_CSRCLIST_FIELD_SIZE=32;
    private int CSRCLIST_FIELD_SIZE=32; 
    private int PAYLOAD_FIELD_SIZE;
    private int PADDING_FIELD_SIZE;
    
    
    private String version;            // version of RTP Protocol
    private String isPadding;          // 1 - padding used, 0 - no padding; 
    private String extension;          // 1 - extension header used, 0 - no extension header used
    private String csrc;               // number of CSRC count followed by fixed header
    private String marker;             // merking significant frames
    private String pt;                 // Payload Type (PT), determine interpretation by app
    private String sn;                 // Sequence Number, incremented with each RTP packet
    private String timeStamp;          // Timestamp of packet creation in seconds
    private String ssrc;               // 
    private String[] csrcList;         // 
    private String payload;            // audio data to send
    private String padding;            // packet trailer 
    //public static class RTPBuilder(){}
    public RTP(){}
    
    public RTP(int v, boolean isPad, boolean isExt, int csrc, boolean marker, int pt, int sn, int ssrc, int[] csrcList, String payload, int padSize) {
        this.version = setVersion(v);           
        this.isPadding = setIsPadding(isPad);
        this.extension = setExtension(isExt);
        this.csrc = setCsrc(csrc);
        this.csrcList = new String[Integer.parseInt(getCsrc())];
        this.marker = setMarker(marker);
        this.pt = setPayloadType(pt);
        this.sn = setSequenceNumer(sn);
        this.timeStamp = setTimestamp();        
        this.ssrc = setSsrc(ssrc);
        this.csrcList = setCsrcList(csrcList);
        this.payload = setPayload(payload);
        this.padding = setPadding(padSize);
    }
    
    public final String getAllRtpFields(){
        return getVersion()   
             + getIsPadding() 
             + getExtension()
             + getCsrc()
             + getMarker()
             + getPayloadType()
             + getSequenceNumber()
             + getTimestamp()
             + getSsrc()
             + getCsrcList()
             + getPayload()
             + getPadding();
    }
    
    public final String setVersion(int v){
        return this.version=String.format("%" + VERSION_FIELD_SIZE + "s", Integer.toBinaryString(v)).replace(" ", "0");
    }
    
    public String getVersion(){
        return this.version;
    }
    public final String setIsPadding(boolean isP){
        if (isP) return this.isPadding=String.format("%" + ISPADDING_FIELD_SIZE + "s", Integer.toBinaryString(1));
        else return this.isPadding=String.format("%" + ISPADDING_FIELD_SIZE + "s", Integer.toBinaryString(0));
    }
    
    public String getIsPadding(){
        return this.isPadding;
    }
    
    public final String setExtension(boolean ext){
        if (ext) return this.extension=String.format("%" + EXTENSION_FIELD_SIZE + "s", Integer.toBinaryString(1));
        else return this.extension=String.format("%" + EXTENSION_FIELD_SIZE + "s", Integer.toBinaryString(0));
    }
    
    public String getExtension(){
        return this.extension;
    }
    
    public final String setCsrc(int csrc){
        return this.csrc=String.format("%" + CSRC_FIELD_SIZE + "s", Integer.toBinaryString(csrc)).replace(" ", "0");
    }
    
    public String incrementCsrc(){
        int c;
        c=Integer.parseInt(getCsrc(),2);
        c++;
        return this.csrc=String.format("%" + CSRC_FIELD_SIZE + "s", Integer.toBinaryString(c)).replace(" ", "0");
    }
    
    public String getCsrc(){
        return this.csrc;
    }
    
    public final String setMarker(boolean marker){
        if (marker) return this.marker=String.format("%" + MARKER_FIELD_SIZE + "s", Integer.toBinaryString(1)).replace(" ", "0");
        else return this.marker=String.format("%" + MARKER_FIELD_SIZE + "s", Integer.toBinaryString(0)).replace(" ", "0");
    }
    
    public String getMarker(){
        return this.marker;
    }
    
    public final String setPayloadType(int pt){
        return this.pt=String.format("%" + PT_FIELD_SIZE + "s", Integer.toBinaryString(pt)).replace(" ", "0");
    }
    
    public String getPayloadType(){
        return this.pt;
    }
    
    public final String setSequenceNumer(int sN){
        return this.sn=String.format("%" + SN_FIELD_SIZE + "s", Integer.toBinaryString(sN)).replace(" ", "0");
    }
    
    public String getSequenceNumber(){
        return this.sn;
    }
    
    
    public final String setTimestamp(){
        return this.timeStamp=String.format("%" + TIMESTAMP_FIELD_SIZE + "s", Long.toBinaryString(System.currentTimeMillis()/1000)).replace(" ", "0");
    }
    
    public String getTimestamp(){
        return this.timeStamp;
    }
    
    public final String setSsrc(int ssrc){
        return this.ssrc=String.format("%" + SSRC_FIELD_SIZE + "s", Integer.toBinaryString(ssrc)).replace(" ", "0");
    }
    
    public String getSsrc(){
        return this.ssrc;
    }
    
    public final String[] setCsrcList(int[] csrc){
        for (int i=0;i<csrc.length;i++){
            this.csrcList[i] = String.format("%" + BASIC_CSRCLIST_FIELD_SIZE + "s", Integer.toBinaryString(csrc[i])).replace(" ", "0");
        }
        return this.csrcList;
    }
    
    public String getCsrcList() throws NullPointerException{
        StringBuilder sb = new StringBuilder();
        for (String s: this.csrcList){
            sb.append(s);
        }
        return sb.toString();
    }
    
    public final String setPayload(String payload){
        this.payload = payload;
        return this.payload;
    }  
    
    public String getPayload(){
        return this.payload;
    }
    
    public final String setPadding(int pSize) throws NullPointerException{
        if (pSize==0) return this.padding="";
        else{            
            return this.padding="0"+String.format("%" + (pSize-1) + "s", Integer.toBinaryString(1)).replace(" ", "1");
        }        
    }
    
    public String getPadding(){
        return this.padding;
    }
    
    public int setCsrcListFieldSize() throws NullPointerException{        
        return CSRCLIST_FIELD_SIZE*=Integer.parseInt(getCsrc(),2);
    }
    
    public int getCsrcListFieldSize() throws NullPointerException{        
        return CSRCLIST_FIELD_SIZE;
    }
    
    public int setPayloadFieldSize(int fS) throws NullPointerException{
        return PAYLOAD_FIELD_SIZE=fS;
    }
    
    public int getPayloadFieldSize() throws NullPointerException{
        return PAYLOAD_FIELD_SIZE;
    }
    
    public int setPaddingFieldSize(int pS) throws NullPointerException{
        return PADDING_FIELD_SIZE=pS;
    }
    
    public int getPaddingFieldSize() throws NullPointerException{
        return PADDING_FIELD_SIZE;
    }
    
}
