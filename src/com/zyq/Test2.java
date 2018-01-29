package com.zyq;

import java.util.ArrayList;

public class Test2 {
    public static ArrayList<int[]> getA(){
        ArrayList<int[]> AA = new ArrayList<int[]>();

        for (int i = 0;i <= 2;i++){
            int[] A = new int[4];
            A[0] = i;
            for(int j = 0;j <= 2;j++){
                if(i+j>2){
                    break;
                }else{
                    A[1] = j;
                }
                for(int k = 0;k <= 2;k++){
                    if(i+A[1]+k>2){
                        break;
                    }else{
                        A[2] = k;
                    }
                    for (int z = 0; z <= 1; z++){
                        A[3] = z;
                        int[] B = new int[4];
                        B = A.clone();
                        AA.add(B);
                    }
                }
            }
        }
        return AA;
    }

    public static void main(String[] args) {
        ArrayList<int[]> AA = getA();
        String s = "{";
        for (int[] a: AA) {
            s += "{"+a[0]+","+a[1]+","+a[2]+","+a[3] +"},";
        }
        System.out.println(s);
    }
}
