package com.study;

class Main1 {

    public static void main(String[] args) {

        System.out.println(ArrayChallenge(new int[] {0,-2,-2,5,5,5}));
        System.out.println("");
        System.out.println("");
        //System.out.println(ArrayChallenge(new int[] {0,-2,-2,5,5,5});


    }
    public static String StringChallenge(String str) {
        // code goes here
        if (str==null || str.trim().length()==0){
            return "true";
        }

        str = str.toLowerCase();

        str = str.replaceAll("\\s*|\t|\r|\n","");

        String regEx = "[\n`-~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， ·、？]";
        str = str.replaceAll(regEx,"");

        System.out.println("str="+str);
        String str1 = str;


        boolean ok = true;
        for(int i=0;i<(str1.length()/2);i++){
            String start =str1.substring(i,i+1);
            String end = str1.substring(str1.length()-i-1,str1.length()-i);
            if (!start.equals(end)){
                ok = false;
                break;
            }
        }
        if (ok){
            //return remove;
            return "true";
        }

        return "false";
    }

    public static int ArrayChallenge(int[] arr) {
        // code goes here
//        Set<Integer> duplicate1 = new HashSet();
//        Set<Integer> duplicate2 = new HashSet();
        String duplicate1 = "";
        String duplicate2 = "";
        for(int i=0;i<arr.length-1;i++){
            int begin = arr[i];
            for(int j=i+1;j<arr.length;j++){
                if (!duplicate2.contains(String.valueOf(j))){
                    int end = arr[j];
                    if (begin==end){
//                        duplicate1.add(i);
//                        duplicate2.add(j);
                        duplicate1 += i;
                        duplicate2 += j;
                        break;
                    }
                }
            }
        }

        return duplicate1.length();
    }


}