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
    
    private static int VERSION_FIELD_SIZE = 2;
    private static int ISPADDING_FIELD_SIZE = 1;
    private static int EXTENSION_FIELD_SIZE = 1;
    private static int CSRC_FIELD_SIZE = 4;
    private static int MARKER_FIELD_SIZE = 1;
    private static int PT_FIELD_SIZE = 7;
    private static int SN_FIELD_SIZE = 16;
    private static int TIMESTAMP_FIELD_SIZE = 32;
    private static int SSRC_FIELD_SIZE = 32;
    
    private int CSRCLIST_FIELD_SIZE=32;
    private int PAYLOAD_FIELD_SIZE;
    private int PADDING_FIELD_SIZE;
    
    
    private int version;            // version of RTP Protocol
    private int padding;            // 1 - padding used, 0 - no padding; 
    private int extension;          // 1 - extension header used, 0 - no extension header used
    private int csrc;               // number of CSRC count followed by fixed header
    private int marker;             // merking significant frames
    private int pt;                 // Payload Type (PT), determine interpretation by app
    private int sn;                 // Sequence Number, incremented with each RTP packet
    private String timeStamp;
    private int ssrc;
    private int[] csrcList;
    private String payload;         // payload will be set outside of constructor
    //public static class RTPBuilder(){}
    public RTP(){}
    
    public RTP(int version, int padding, int extension, int csrc,int marker, int pt, int sn, String timeStamp, int ssrc, int[] csrcList) {
        this.version = version;
        this.padding = padding;
        this.extension = extension;
        this.csrc = csrc;
        this.marker = marker;
        this.pt = pt;
        this.sn = sn;
        this.timeStamp = timeStamp;        
        this.ssrc = ssrc;
        this.csrcList = csrcList;
    }
    
    public int setVersion(int version){
        this.version = version;
        return this.version;
    }
    
    public int getVersion(){
        return this.version;
    }
    public int setPadding(int padding){
        this.padding = padding;
        return this.padding;
    }
    
    public int getPadding(){
        return this.padding;
    }
    
    public int setExtension(int extension){
        this.extension = extension;
        return this.extension;
    }
    
    public int getExtension(){
        return this.extension;
    }
    
    public int setCsrc(int csrc){
        this.csrc = csrc;
        return this.csrc;
    }
    
    public int incrementCsrc(){
        return ++this.csrc;
    }
    
    public int getCsrc(){
        return this.csrc;
    }
    
    public int setMarker(int marker){
        this.marker = marker;
        return this.marker;
    }
    
    public int getMarker(){
        return this.marker;
    }
    
    public int setPayloadType(int pt){
        this.pt = pt;
        return this.pt;
    }
    
    public int getPayloadType(){
        return this.pt;
    }
    
    public String setTimestamp(String timeStamp){
        this.timeStamp = timeStamp;
        return this.timeStamp;
    }
    
    public String getTimestamp(){
        return this.timeStamp;
    }
    
    public int setSsrc(int ssrc){
        this.ssrc = ssrc;
        return this.ssrc;
    }
    
    public int getSsrc(){
        return this.ssrc;
    }
    
    public int[] setCsrcList(int[] csrc){
        this.csrcList = csrc;
        return this.csrcList;
    }
    
    public int[] getCsrcList() throws NullPointerException{
        return this.csrcList;
    }
    
    public String setPayload(String payload){
        this.payload = payload;
        return this.payload;
    }    
    
    public int setCsrcListFieldSize() throws NullPointerException{        
        return CSRCLIST_FIELD_SIZE*=getCsrc();
    }
    
}
