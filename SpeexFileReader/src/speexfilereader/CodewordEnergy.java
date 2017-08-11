package speexfilereader;

import java.util.HashMap;
import java.util.Map;

/*
 * To change this license headerf choose License Headers in Project Properties.
 * To change this template filef choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Cz4p3L
 */
public interface CodewordEnergy {
    
    Map<Integer, Float> codewordEnergyMapMode4 = new HashMap<>();
    Map<Integer, Float> codewordEnergyMapMode5 = new HashMap<>();
    Map<Integer, Float> codewordEnergyMapMode6 = new HashMap<>();
    
    Map<Integer, Integer> refactorizedCodewordMap = new HashMap<>();
    
    Map<Integer, Integer> bitRedundancyMap = new HashMap<>();
    
    static final float[] codewordEnergyMode4 = {
                                                        1.9737711f,
                                                        5.901472f,
                                                        10.008736f,
                                                        4.3876424f,
                                                        127.729614f,
                                                        15.97236f,
                                                        3.370049f,
                                                        1.9705099f,
                                                        3.8123603f,
                                                        6.767845f,
                                                        0.86377347f,
                                                        5.095256f,
                                                        53.180374f,
                                                        1.7827424f,
                                                        4.404454f,
                                                        5.010966f,
                                                        9.7029705f,
                                                        2.62756f,
                                                        5.968965f,
                                                        32.404636f,
                                                        3.7594683f,
                                                        20.111824f,
                                                        2.44581f,
                                                        6.9050875f,
                                                        3.2158394f,
                                                        6.3031635f,
                                                        3.2934983f,
                                                        1.3929085f,
                                                        6.1763844f,
                                                        4.882888f,
                                                        6.479773f,
                                                        10.537826f,
                                                        10.277063f,
                                                        6.8327575f,
                                                        7.0165668f,
                                                        4.8876386f,
                                                        20.83415f,
                                                        49.377f,
                                                        5.0112185f,
                                                        7.0966516f,
                                                        3.6516814f,
                                                        8.143771f,
                                                        6.07155f,
                                                        1.2096401f,
                                                        17.765259f,
                                                        27.395882f,
                                                        2.7851105f,
                                                        9.792625f,
                                                        9.748872f,
                                                        0.974419f,
                                                        3.9030185f,
                                                        2.4148452f,
                                                        6.1411357f,
                                                        13.074324f,
                                                        0.9173832f,
                                                        3.2939537f,
                                                        5.560345f,
                                                        4.992622f,
                                                        9.819363f,
                                                        3.576397f,
                                                        4.8563824f,
                                                        4.132111f,
                                                        18.618904f,
                                                        23.696342f,
                                                        6.4262705f,
                                                        2.6309068f,
                                                        3.1302202f,
                                                        5.894439f,
                                                        44.554703f,
                                                        61.130127f,
                                                        2.0468712f,
                                                        2.0932405f,
                                                        3.321388f,
                                                        4.2099414f,
                                                        15.46493f,
                                                        5.280531f,
                                                        19.807291f,
                                                        2.969915f,
                                                        24.71976f,
                                                        5.052266f,
                                                        3.2523303f,
                                                        2.0228271f,
                                                        5.2735324f,
                                                        3.3353121f,
                                                        8.685424f,
                                                        28.759214f,
                                                        3.3604238f,
                                                        10.831715f,
                                                        2.755249f,
                                                        3.6919055f,
                                                        2.0090024f,
                                                        5.217579f,
                                                        4.4981704f,
                                                        8.338275f,
                                                        3.9342346f,
                                                        3.9495788f,
                                                        4.4506936f,
                                                        4.3325524f,
                                                        11.757854f,
                                                        12.0030365f,
                                                        21.169678f,
                                                        17.819056f,
                                                        11.251504f,
                                                        1.8721828f,
                                                        11.530824f,
                                                        18.278303f,
                                                        6.287928f,
                                                        6.595452f,
                                                        16.228485f,
                                                        130.20627f,
                                                        4.590528f,
                                                        3.1671789f,
                                                        1.9694363f,
                                                        14.530546f,
                                                        7.1204786f,
                                                        5.9728274f,
                                                        16.359488f,
                                                        11.030245f,
                                                        6.9668603f,
                                                        1.9773355f,
                                                        1.4342076f,
                                                        7.0012784f,
                                                        5.727733f,
                                                        12.928488f,
                                                        9.100881f,
                                                        5.690405f,
                                                        8.154691f,
                                                        8.400818f
    };
    
    static final float[] codewordEnergyMode5 = {
                                                        2.8028073f,
                                                        6.275052f,
                                                        6.6646724f,
                                                        2.7463624f,
                                                        6.3464413f,
                                                        6.527106f,
                                                        36.730755f,
                                                        4.6917863f,
                                                        3.6058254f,
                                                        2.5580597f,
                                                        2.5651743f,
                                                        15.774959f,
                                                        3.1524198f,
                                                        3.1307206f,
                                                        1.1096395f,
                                                        13.406779f,
                                                        6.9479556f,
                                                        5.159763f,
                                                        1.5471859f,
                                                        1.931334f,
                                                        4.785431f,
                                                        3.6614785f,
                                                        2.000058f,
                                                        8.863804f,
                                                        4.3887844f,
                                                        3.45425f,
                                                        1.8806453f,
                                                        3.2533367f,
                                                        50.28613f,
                                                        10.008479f,
                                                        2.9147959f,
                                                        15.449234f,
                                                        1.1773952f,
                                                        3.9368176f,
                                                        18.827341f,
                                                        6.027807f,
                                                        9.540686f,
                                                        5.1710763f,
                                                        1.3301853f,
                                                        0.9108135f,
                                                        20.340466f,
                                                        7.2157736f,
                                                        13.410003f,
                                                        4.751934f,
                                                        8.499002f,
                                                        0.94879085f,
                                                        8.165798f,
                                                        5.4771223f,
                                                        4.4491034f,
                                                        5.166603f,
                                                        21.200466f,
                                                        33.52779f,
                                                        6.606855f,
                                                        0.7732322f,
                                                        8.328012f,
                                                        49.31714f,
                                                        2.7990642f,
                                                        3.2625406f,
                                                        22.288403f,
                                                        1.821504f,
                                                        3.4193656f,
                                                        4.913309f,
                                                        2.0791807f,
                                                        0.023499468f
    };
    static final float[] codewordEnergyMode6 = {
                                                        11.627344f,
                                                        78.004486f,
                                                        16.357685f,
                                                        1.3152227f,
                                                        1.9195089f,
                                                        4.419409f,
                                                        30.998405f,
                                                        6.2338276f,
                                                        6.4470415f,
                                                        10.317154f,
                                                        6.11654f,
                                                        31.809858f,
                                                        15.971704f,
                                                        7.1452885f,
                                                        2.9592116f,
                                                        6.8200364f,
                                                        2.6277223f,
                                                        2.598775f,
                                                        6.724902f,
                                                        4.485894f,
                                                        10.211773f,
                                                        4.1952972f,
                                                        6.471451f,
                                                        3.8594465f,
                                                        3.3939052f,
                                                        2.0959861f,
                                                        2.4322832f,
                                                        6.0542846f,
                                                        17.32181f,
                                                        58.94436f,
                                                        20.614647f,
                                                        5.231437f,
                                                        3.5643516f,
                                                        30.700727f,
                                                        71.00737f,
                                                        4.590015f,
                                                        10.901411f,
                                                        2.172509f,
                                                        14.961801f,
                                                        3.4475174f,
                                                        4.0588417f,
                                                        30.648941f,
                                                        5.507512f,
                                                        24.826223f,
                                                        7.9638767f,
                                                        17.919762f,
                                                        11.407709f,
                                                        6.38569f,
                                                        1.2742095f,
                                                        0.2821158f,
                                                        0.77353454f,
                                                        1.4138973f,
                                                        2.2367768f,
                                                        4.114907f,
                                                        8.135468f,
                                                        6.7918744f,
                                                        1.9729824f,
                                                        13.98372f,
                                                        4.51602f,
                                                        2.4476614f,
                                                        4.7218585f,
                                                        20.452515f,
                                                        3.4815016f,
                                                        4.041743f,
                                                        53.618805f,
                                                        42.05125f,
                                                        15.289128f,
                                                        23.239273f,
                                                        6.377231f,
                                                        4.636112f,
                                                        8.218865f,
                                                        4.926035f,
                                                        2.6816478f,
                                                        1.1368928f,
                                                        26.170887f,
                                                        10.610801f,
                                                        14.362072f,
                                                        21.678942f,
                                                        6.256892f,
                                                        3.7972841f,
                                                        1.7927266f,
                                                        3.654834f,
                                                        4.4844103f,
                                                        23.44965f,
                                                        12.438408f,
                                                        1.3298919f,
                                                        11.053662f,
                                                        1.2378756f,
                                                        3.869538f,
                                                        10.631986f,
                                                        1.2305508f,
                                                        11.794373f,
                                                        6.806429f,
                                                        7.0440297f,
                                                        0.45514822f,
                                                        1.9909937f,
                                                        5.0523496f,
                                                        9.427503f,
                                                        2.9509408f,
                                                        3.9597187f,
                                                        51.74985f,
                                                        9.7273035f,
                                                        7.5619526f,
                                                        3.7430568f,
                                                        4.8808365f,
                                                        1.8392603f,
                                                        21.252167f,
                                                        1.8130856f,
                                                        2.691833f,
                                                        3.7919066f,
                                                        1.0240006f,
                                                        10.731213f,
                                                        7.0946255f,
                                                        4.2536592f,
                                                        4.6702642f,
                                                        21.089579f,
                                                        3.1860223f,
                                                        5.9544873f,
                                                        4.9369516f,
                                                        9.906532f,
                                                        4.19884f,
                                                        40.88607f,
                                                        6.2385516f,
                                                        12.345903f,
                                                        4.3448296f,
                                                        111.63214f,
                                                        3.7893724f,
                                                        7.191921f,
                                                        6.0329866f,
                                                        110.72185f,
                                                        6.8071327f,
                                                        3.3861122f,
                                                        2.0208697f,
                                                        6.504469f,
                                                        10.363074f,
                                                        6.1536565f,
                                                        2.962642f,
                                                        4.293643f,
                                                        1.093021f,
                                                        26.256958f,
                                                        28.978598f,
                                                        16.182941f,
                                                        5.883137f,
                                                        6.968818f,
                                                        2.7666621f,
                                                        0.59748936f,
                                                        8.760822f,
                                                        5.056801f,
                                                        11.909997f,
                                                        11.885719f,
                                                        27.818186f,
                                                        1.293302f,
                                                        7.5125294f,
                                                        11.675793f,
                                                        7.416996f,
                                                        13.210276f,
                                                        47.03653f,
                                                        17.969734f,
                                                        2.6155562f,
                                                        2.4426415f,
                                                        5.4851437f,
                                                        25.745754f,
                                                        15.166906f,
                                                        3.07134f,
                                                        11.427741f,
                                                        6.4447236f,
                                                        6.2381144f,
                                                        5.131088f,
                                                        7.831582f,
                                                        54.089684f,
                                                        6.669345f,
                                                        4.678587f,
                                                        5.227964f,
                                                        9.999871f,
                                                        7.317199f,
                                                        8.165247f,
                                                        1.3620623f,
                                                        4.2761784f,
                                                        0.94219893f,
                                                        2.8918717f,
                                                        0.656583f,
                                                        6.3666196f,
                                                        16.480978f,
                                                        4.8056297f,
                                                        6.7550097f,
                                                        6.3951006f,
                                                        1.4822295f,
                                                        7.7962117f,
                                                        10.461834f,
                                                        33.58716f,
                                                        6.9326935f,
                                                        1.681675f,
                                                        5.4891634f,
                                                        27.79012f,
                                                        31.575804f,
                                                        27.540916f,
                                                        4.2525635f,
                                                        28.841373f,
                                                        6.5247407f,
                                                        4.0936155f,
                                                        6.2235003f,
                                                        16.00006f,
                                                        12.078106f,
                                                        2.3844714f,
                                                        12.60587f,
                                                        13.163307f,
                                                        4.0113587f,
                                                        3.439604f,
                                                        1.8038626f,
                                                        0.02852276f,
                                                        3.3012655f,
                                                        2.203042f,
                                                        5.518115f,
                                                        15.359391f,
                                                        20.436144f,
                                                        13.576077f,
                                                        8.576721f,
                                                        21.46748f,
                                                        6.3902125f,
                                                        3.5462604f,
                                                        0.978859f,
                                                        22.239655f,
                                                        13.085932f,
                                                        14.513482f,
                                                        19.444788f,
                                                        38.79273f,
                                                        8.942496f,
                                                        6.803147f,
                                                        22.11443f,
                                                        1.873291f,
                                                        12.930462f,
                                                        5.836111f,
                                                        11.376314f,
                                                        13.459976f,
                                                        6.716345f,
                                                        2.0049217f,
                                                        13.709911f,
                                                        21.047554f,
                                                        8.459197f,
                                                        8.96612f,
                                                        0.73211914f,
                                                        1.0117052f,
                                                        0.79338f,
                                                        8.237563f,
                                                        20.460966f,
                                                        3.474843f,
                                                        6.2659993f,
                                                        6.6059413f,
                                                        4.0896297f,
                                                        14.163478f,
                                                        17.107557f,
                                                        11.286746f,
                                                        6.7407117f,
                                                        45.553402f,
                                                        3.8727748f,
                                                        1.1992335f
    };
    
    void initEnergyMap(float[] cbdk, Map<Integer,Float> m);
}
