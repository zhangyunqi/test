package com.zyq;

import java.util.HashMap;
import java.util.Map;

public class Test1 {
    private final static int N = 16;
    private final static int IP_CFCB = 2000;    //住院病人惩罚成本
    private final static int IP_DTCB = 1000;    //住院病人等待成本
    private final static int IP_SY = 10000;     //住院病人收益
    private final static float IP_GL = 0.4f;    //住院病人到达的概率
    private final static int SOP_CFCB = 4000;   //门诊病人惩罚成本
    private final static int SOP_DTCB = 2000;   //门诊病人等待成本
    private final static int SOP_SY = 8000;     //门诊病人收益
    private final static float EP_GL = 0.6f;    //急诊病人到达的概率
    private final static float ZY_GL = 0.24f;   //转移概率
    //因为只有两台CT,所以行动集是有限的，行动集{门诊病人数量，住院病人数量，急诊病人数量，是否接受病人}
    private final static int[][] A = {{0, 0, 0, 0}, {0, 0, 0, 1}, {0, 0, 1, 0}, {0, 0, 1, 1}, {0, 0, 2, 0}, {0, 0, 2, 1}, {0, 1, 0, 0}, {0, 1, 0, 1},
            {0, 1, 1, 0}, {0, 1, 1, 1}, {0, 2, 0, 0}, {0, 2, 0, 1}, {1, 0, 0, 0}, {1, 0, 0, 1}, {1, 0, 1, 0}, {1, 0, 1, 1}, {1, 1, 0, 0}, {1, 1, 0, 1},
            {2, 0, 0, 0}, {2, 0, 0, 1}};
    //先以9个状态来说

    //第16槽以5个状态为例 {门诊病人数量，住院病人数量，急诊病人数量}
    private final static int[][] S15 = {{0, 0, 0}, {1, 1, 0}, {1, 0, 1}, {1, 1, 1}, {1, 0, 0}, {2, 1, 0}, {2, 0, 0}, {2, 0, 1}, {2, 1, 1}};
    private final static int[][] S16 = {{0, 0, 0}, {1, 1, 0}, {1, 0, 1}, {1, 1, 1}, {1, 0, 0}};

    /**
     * 时间的槽的收益 不同状态集，不同行动集下的收益
     * @param A
     * @param W
     * @return
     */
    public static double getR(int[] A, int[] W) {
        double r = 0.0d;
        r = IP_SY * A[1] - (W[1] - A[1]) * IP_DTCB + (SOP_SY * A[0] - (W[0] - A[0]) * SOP_DTCB);
        return r;
    }

    /**
     * 获取下一状态的门诊病人
     * @param A
     * @param W
     * @return
     */
    public static int getWSOP_next(int[] A,int[] W){
        return W[0]+A[3]-A[0];
    }

    /**
     * 下一阶段住院病人的数量
     * @param A 行动
     * @param W 状态
     * @param dIP 住院病人是否到达 0：否 1：是
     * @return
     */
    public static int getWIP_next(int[] A,int[] W,int dIP){
        return  W[1]+dIP-A[1];
    }

    /**
     * 下一阶段的状态值
     * @param A
     * @param W
     * @param dIP
     * @param dEP 急诊病人是否到达 0：否 1：是
     * @return
     */
    public static int[] getNextState(int[] A,int[] W,int dIP,int dEP){
        int[] array = {getWSOP_next(A,W),getWIP_next(A, W, dIP),dEP};
        return array;
    }

    public static double getAllCFCB(int[] A,int[] W){
        double r =0.0d;
        r= - SOP_CFCB*getWSOP_next(A, W)-IP_CFCB*getWIP_next(A, W,0);
        return r;
    }

    public static Map<int[],Double> getMaxVS(int[] S,int n) throws Exception{
        if(n==N){

        }else if(n>=1&&n<=N-1){

        }else{
            throw new Exception("超出范围");
        }
    }

    public static Map<int[], Map<int[], Double>> getVS() {
        Map<int[], Map<int[], Double>> map = new HashMap<int[], Map<int[], Double>>();
        for (int[] a : S15) {
            Map<int[], Double> rmap = new HashMap<int[], Double>();
            for (int[] b : A) {
                if (b[0] > a[0] || b[1] > a[1] || b[2] != a[2]) {
                    continue;
                } else {
                    Double d = new Double(getR(b[0], a[0], b[1], a[1], b[3]));
                    if (rmap.size() > 0) {
                        for (int[] key : rmap.keySet()) {
                            if (rmap.get(key) < d) {
                                rmap.remove(key);
                                rmap.put(b, d);
                            } else if (rmap.get(key) == d) {
                                rmap.put(b, d);
                            }
                        }
                    } else {
                        rmap.put(b, d);
                    }

                }
            }
            map.put(a, rmap);
        }
        return map;
    }

    private static String arrayToString(int[] array) {
        String s = "{";
        for (int a : array) {
            s += a + ",";
        }
        s = s.substring(0, s.length() - 1) + "}";
        return s;
    }

    public static void main(String[] args) {
        Map<int[], Map<int[], Double>> map = getVS();
        for (int[] a : S15) {
            for (Map.Entry<int[], Double> rentry : map.get(a).entrySet()) {
                System.out.println(arrayToString(a) + ":" + arrayToString(rentry.getKey()) + ":" + rentry.getValue());
            }
        }
        /*
        for (Map.Entry<int[], Map<int[], Double>> entry : map.entrySet()) {
            for (Map.Entry<int[], Double> rentry : entry.getValue().entrySet()) {
                System.out.println(arrayToString(entry.getKey()) + ":" + arrayToString(rentry.getKey()) + ":" + rentry.getValue());
            }
        }
        */
    }


}
