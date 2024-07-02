package com.nx.common.model;

import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        //1. 质数求解
        System.out.println("");
        System.out.println(Integer.valueOf((isPrimer(546))).toString());
        //2. 括号对数
        System.out.println("");
        String ss = ")))()()()()((((()))))))))))))))))))))))))()(((((((";
        System.out.println(q2(ss));
        //3. 邮箱正则表达式
        System.out.println("");
        System.out.println(emailRegex("aaa_999@a.com"));
        //4. 没看到
    }

    public static boolean emailRegex(String email){
        String pattern ="^\\s*[a-zA-Z]+([_]|(_[0-9]+))@([a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";

        boolean isMatch = Pattern.matches(pattern, email);
        System.out.println("字符串中是否包含了 'runoob' 子字符串? " + isMatch);
        System.out.println("isMatch="+isMatch);
        return isMatch;
    }

    //质数
    public static int isPrimer(int n){
        if (n<2) return 0;

        for (int i=2;i<n;i++){
            if (n%i==0){
                return i;
            }
        }
        return 1;
    }


    /**
     * 括号补足个数
     * @param s
     * @return
     */
    public static int q2(String s){
        int count =0;
        while (s.length()>0){
            if (s.startsWith("(")){
                if (s.indexOf(")")==-1){
                    count += s.length();
                    return count;
                }
                String left = s.substring(0,s.indexOf(")"));
                String end = s.substring(s.indexOf(")"));

                if (end.indexOf("(")==-1){
                    count += Math.abs(left.length()-end.length());
                    return count;
                }

                String right = end.substring(0,end.indexOf("("));
                count += Math.abs(left.length()-right.length());

                s = end.substring(end.indexOf("("));

                //System.out.println("1.sss="+s+"--.left.indexOf="+s.indexOf(")")+".left="+left+"---.end="+end+"--.right="+right+"---");

            }else{
                if (s.indexOf("(")==-1){
                    count += s.length();
                    return count;
                }
                String left = s.substring(0,s.indexOf("("));
                String end = s.substring(s.indexOf("("));

                if (end.indexOf(")")==-1){
                    count += Math.abs(left.length()-end.length());
                    return count;
                }

                String right = end.substring(0,end.indexOf(")"));
                count += Math.abs(left.length()-right.length());

                s = end.substring(end.indexOf(")"));

                //System.out.println("2.sss="+s+"--.left.indexOf="+s.indexOf(")")+".left="+left+"---.end="+end+"--.right="+right+"---");
            }
        }
        return count;
    }


}
