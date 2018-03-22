package com.zyq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Test4 {
    private final static int C = 2; //CT数量
    private final static int N = 8;    //时间槽数量
    private final static int IP_CFCB = 2000;    //住院病人惩罚成本
    private final static int IP_DTCB = 1000;    //住院病人等待成本
    private final static int IP_SY = 10000;     //住院病人收益
    private final static double IP_GL = 0.4f;    //住院病人到达的概率
    private final static int SOP_CFCB = 4000;   //门诊病人惩罚成本
    private final static int SOP_DTCB = 2000;   //门诊病人等待成本
    private final static int SOP_SY = 8000;     //门诊病人收益
    private final static double EP_GL = 0.6f;    //急诊病人到达的概率
    private final static double ZY_GL_00 = 0.24f;   //转移概率 {住院病人到达，急诊病人到达}={0，0}
    private final static double ZY_GL_01 = 0.36f;   //转移概率 {住院病人到达，急诊病人到达}={0，1}
    private final static double ZY_GL_10 = 0.16f;   //转移概率 {住院病人到达，急诊病人到达}={1，0}
    private final static double ZY_GL_11 = 0.24f;   //转移概率 {住院病人到达，急诊病人到达}={1，1}
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
     *
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
     *
     * @param A
     * @param W
     * @return
     */
    public static int getWSOP_next(int[] A, int[] W) {
        return W[0] + A[3] - A[0];
    }

    /**
     * 下一阶段住院病人的数量
     *
     * @param A   行动
     * @param W   状态
     * @param dIP 住院病人是否到达 0：否 1：是
     * @return
     */
    public static int getWIP_next(int[] A, int[] W, int dIP) {
        return W[1] + dIP - A[1];
    }

    /**
     * 下一阶段的状态值
     *
     * @param A
     * @param W
     * @param dIP
     * @param dEP 急诊病人是否到达 0：否 1：是
     * @return
     */
    public static int[] getNextState(int[] A, int[] W, int dIP, int dEP) {
        int[] array = {getWSOP_next(A, W), getWIP_next(A, W, dIP), dEP};
        return array;
    }

    /**
     * 获取惩罚成本 最后的惩罚成本已最后阶段的S，A有关
     *
     * @param A
     * @param W
     * @return
     */
    public static double getAllCFCB(int[] A, int[] W) {
        double r = 0.0d;
        r = -SOP_CFCB * getWSOP_next(A, W) - IP_CFCB * getWIP_next(A, W, 0);
        return r;
    }

    /**
     * 获取阶段某一状态最大的受益的行动及受益的值
     * @param S 状态
     * @param n 第几个时间槽
     * @return
     * @throws Exception
     */
    public static Map<int[], Double> getMaxVS(int[] S, int n) throws Exception{
        Map<int[], Double> rmap = new HashMap<int[], Double>();
        int[][] A_metch = getAofMatch(S,n);
        for (int[] a : A_metch) {
            Double d = new Double(0.0d);
            if (n == N){
                d = new Double(getR(a, S) + getAllCFCB(a, S));
            }else{
                d = new Double(getR(a, S) + ZY_GL_00 * getValOfMap(getMaxVS(getNextState(a, S, 0, 0), n + 1))
                        + ZY_GL_01 * getValOfMap(getMaxVS(getNextState(a, S, 0, 1), n + 1))
                        + ZY_GL_10 * getValOfMap(getMaxVS(getNextState(a, S, 1, 0), n + 1))
                        + ZY_GL_11 * getValOfMap(getMaxVS(getNextState(a, S, 1, 1), n + 1)));
            }
            if (rmap.size() > 0) {
                for (int[] key : rmap.keySet()) {
                    if (rmap.get(key) < d) {
                        rmap.remove(key);
                        rmap.put(a, d);
                    } else if (rmap.get(key) == d) {
                        rmap.put(a, d);
                    }
                }
            } else {
                rmap.put(a, d);
            }
        }
        return rmap;
    }

    //根据约束条件得到满足条件的行动集A
    public static int[][] getAofMatch(int[] S,int n){
        ArrayList<int[]> arrayOfA = new ArrayList();
        for (int[] a: A) {
            //约束条件
            if (isAmetch(a,S)){
                if(n == N){
                    //最后时刻不在接受病人
                    if (a[3] == 0){
                        arrayOfA.add(a);
                    }else{
                        continue;
                    }
                }else{
                    arrayOfA.add(a);
                }
            }else{
                continue;
            }
        }
        int size = arrayOfA.size();
        int[][] A_metch = new int[size][4];
        return arrayOfA.toArray(A_metch);
    }

    public static boolean isAmetch(int[] a,int[] S){
        boolean b = false;
        boolean one = a[0] <= S[0];
        boolean two = a[1] <= S[1];
        boolean three = a[2] == S[2];
        boolean four = a[0]+a[1]+a[2] <= C;
        boolean five = 1/2*Math.abs(((double) a[0])/((double) S[0])-((double)a[1])/((double)S[1])) <= 0.01;
        boolean six = 1/3*Math.abs(((double)a[0])*2/((double)S[0])-((double)a[1])/((double)S[1])-((double)a[2])/((double)S[2])) <= 0.01;
        boolean seven = 1/3*Math.abs(((double)a[1])*2/((double)S[1])-((double)a[0])/((double)S[0])-((double)a[2])/((double)S[2])) <= 0.01;
        boolean eight = 1/3*Math.abs(((double)a[2])*2/((double)S[2])-((double)a[1])/((double)S[1])-((double)a[0])/((double)S[0])) <= 0.01;

        if(one && two && three && four){
            if(S[0] > 0 && S[1] > 0 && S[2] == 0){
                if (five){
                    b = true;
                }
            }else if (S[0] > 0 && S[1] > 0 && S[2] > 0){
                if(six && seven && eight) {
                    b = true;
                }
            }else{
                b = true;
            }

        }
        return b;
    }

    private static String arrayToString(int[] array) {
        String s = "{";
        for (int a : array) {
            s += a + ",";
        }
        s = s.substring(0, s.length() - 1) + "}";
        return s;
    }

    private static double getValOfMap(Map<int[], Double> map) throws Exception {
        double r = 0.0d;
        if (map.size() == 1) {
            for (int[] key : map.keySet()) {
                r = map.get(key);
            }
        } else {
            throw new Exception("有多个最大值");
        }
        return r;
    }

    public static void main(String[] args) throws Exception {
//        for (int[] s : S16) {
//            Map<int[], Double> map = getMaxVS(s, 1);
//            for (Map.Entry<int[], Double> rentry : map.entrySet()) {
//                System.out.println(arrayToString(s) + ":" + arrayToString(rentry.getKey()) + ":" + rentry.getValue());
//            }
//        }
        /*
        //测试不同状态下是否有符合的行动集
        for (int[] s1 : S15) {
            System.out.println(arrayToString(s1)+"--------------");
            for(int[] a : A){
                if (isAmetch(a,s)){
                    System.out.println(arrayToString(a));
                }
            }

        }*/

        //----------------------------------------------------------//
        for (int[] s: S15) {
            Map<int[], Double> map = getMaxVS(s, 2);
            for (Map.Entry<int[], Double> rentry : map.entrySet()) {
                System.out.println(arrayToString(s) + ":" + arrayToString(rentry.getKey()) + ":" + rentry.getValue());
            }
        }
    }
}
