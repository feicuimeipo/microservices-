package com.study;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * @author nianxiaoling
 * @data 2022-09-01
 */
public class Main {
    public static void main(String[] args) {
        //1. 质数求解，如果是质数返回1，如果不是质数返回最小公约数
        int primer = isPrimer(546);
        System.out.println("");
        System.out.println("");
        System.out.println("1.质数判断结果="+primer);
        System.out.println("------------------");


        //2. 括号对数
        String ss = ")))(()()(()))))))))))))))))()))))))()))))))))))))))))))))()))())())())())))()))()))()())))))())))))))((((()()())())))))))))))))))))))())()))())())))()()))))()))))))()))))))()(())))))())))))))))))))))))()))))))(())))())))))))))))())))))))))))))))))())))))(()))))))))))()))))))))())))(()))())))())()))())))))()))())))()))))))))))))))))()))))))))())))))(())()(()))))))))))(()))))))))))))))))))))))()))))))))()))))))))))))))))))))()))())())))())))(()(())))))()())))())))))))))))))))())))))()((())))())(()))))((())))))))))()))))))())))))))))))))()()))(())))))))))))()))))())))))))(()))))))))())))))))))())))))))())))))()))))))))))()())))))))())))))))))))))))())))))())))))))))))))()))()))))()())))))))))())())))()))))())))(())))))))))))))))))))))())()))))))))))))))))()))())))())))(())))))((()))))))))()))))))))))()))))))))()))()))))))())))())(()))))((())))))))()())))))))))()))))))))))))))))()))())))()))))))())))))))))))))())))))))))))()))))()))())))))(()))()))))))))())))))())))))))()()))))))))))))()))";
        int minCount = getMin(ss);
        System.out.println("");
        System.out.println("");
        System.out.println("2.括号对数判断结果="+minCount);
        System.out.println("------------------");


        //3. 邮箱正则表达式
        //线上课程里，末尾海参崴有空格一直通不过
        boolean email = emailRegex("Iaaa@hackerrank.com");
        System.out.println("");
        System.out.println("");
        System.out.println("3.邮件地址判断结果："+ email);
        System.out.println("------------------");


        //4. 找股票对数：规则达到目标数的股票对，相加的两个数不能相等
        System.out.println("");
        //线上课程里，末尾海参崴有空格一直通不过
        List<Integer> stockPrices = new ArrayList<>();
        stockPrices.add(1);
        stockPrices.add(1);
        stockPrices.add(46);
        stockPrices.add(9);
        stockPrices.add(1);
        stockPrices.add(46);
        stockPrices.add(1);
        int count = stockPrice(stockPrices,47);

        System.out.println("");
        System.out.println("");
        System.out.println("4.股票对数判断结果："+count);
        System.out.println("------------------");


        //5. 二叉树，判断一组数据是否是可构成一颗二叉树
        //三个条件：1.根节点不能有两个，，2.子节点最多只能是两个，2.一个节点不能有空两父亲
        String[] strArray = new String[]{"(1,2)", "(2,4)", "(5,7)", "(7,2)", "(9,5)"}; //true
        String[] strArray1 = new String[]{"(1,2)", "(3,2)", "(2,12)", "(5,2)"}; //false
        String result = BinaryTreeIncCheck.start(strArray);
        System.out.println("");
        System.out.println("");
        System.out.println("5.二叉树判断结果："+result);
        System.out.println("------------------");

        //6.回文字符串的判断（正读反读都能读通的句子，即正反数字的顺序都是一样的）
        String str = "1234567654321";
        String stringPalindrome = StringPalindrome.StringChallenge(str);

        System.out.println("");
        System.out.println("");
        System.out.println("6.回文字符判断结果："+(stringPalindrome.equals("")?"正确":stringPalindrome));
        System.out.println("------------------");


    }

    public static boolean emailRegex(String email){
        String pattern ="^\\s*[a-zA-Z]+(|[_]*|(_[0-9]*))@(hackerrank.com)(\\s)*$";

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


    public static List<String> clear(String s, List<String> result){
        if (result==null) {
            result = new LinkedList<>();
        }
        String []ary = s.split("\\(\\)");
        for (String s1 : ary) {
            if (s1.indexOf("\\(\\)")>0){
                return clear(s1,result);
            }else{
                result.add(s1);
            }
        }
        return result;
    }

    public static int getMin(String ss) {
        // Write your code here
        List<String> result = new LinkedList<>();
        result = clear(ss,result);

        String s = "";
        for (String s1 : result) {
            s += s1;
        }

//        System.out.println(s);
//        System.out.println(ss);

        int count =0;
        //int index =0;
        while (s.length()>0){
            //index ++;
            String left = "";
            String right = "";
            //int cur = 0;
            if (s.startsWith("(")){
                if (s.indexOf(")")==-1){
                    left = s;
                    right = "";
                    count += left.length();
                    //System.out.println(index+".左括号--count="+count+"--cur="+cur+"--"+left+right);
                    return count;
                }else {
                    left = s.substring(0,s.indexOf(")"));
                    String end = s.substring(s.indexOf(")"));
                    if (end.indexOf("(")==-1){
                        right = end;
                        s = "";
                    }else{
                        right = end.substring(0,end.indexOf("("));
                        s = end.substring(end.indexOf("("));
                    }
                    count += Math.abs(left.length()-right.length());
                    //cur = Math.abs(left.length()-right.length());;
                   // System.out.println(index+".左括号--count="+count+"--cur="+cur+"--"+left+right);
                }
            }else{
                String end = s;
                if (end.indexOf("(")==-1){
                    right = end;
                    count += right.length();
                    //cur = right.length();
                    //System.out.println(index+".右括号--count="+count+"--cur="+cur+"--"+left+right);
                    return count;
                }else {
                    right = end.substring(0, end.indexOf("("));
                    count += right.length();
                    s = end.substring(end.indexOf("("));
                    //cur = right.length();
                    //System.out.println(index+".右括号--count="+count+"--cur="+cur+"--"+left+right);
                }
            }
        }
        return count;
    }



    public static int stockPrice(List<Integer> stockPrice,long target){
        //可以有另外一个解决，建一个类判断，可能感觉更干净一些，但思路是一样的
        Map<Integer, Set> r = new HashMap<>();
        for (int i=0;i<stockPrice.size()-1;i++){
            int start = stockPrice.get(i);
            for (int j=i+1;j<stockPrice.size();j++){
                int end = stockPrice.get(j);
                if ( (start+end) == target){
                    if (r.containsKey(start) && r.get(start).contains(end)){

                    }else if(r.containsKey(end) && r.get(end).contains(start)){

                    }else if(r.containsKey(start)){
                        r.get(start).add(end);
                    }else{
                        r.put(start,new HashSet(){{add(end);}});
                    }
                }
            }
        }
        int count = r.size();
        return count;
    }


    public static class BinaryTreeIncCheck {

//        public static void main(String[] args) {
////            List<Sample> listSample = Sample.getSampleList();
////            for (Sample sample : listSample) {
//                System.out.println("");
//                String result = start(args);
//                if (result.equals("true")){
//                    System.out.println("true");
//                }else{
//                    System.out.println("false");
//                }
//            //}
//        }

        public static String start(String[] strArr)  {
            StringBuffer debugInfo = new StringBuffer("");
            Map<Integer, TreeNode> parents = new ConcurrentHashMap<>();
            Map<Integer, TreeNode> childs = new ConcurrentHashMap<>();
            if (strArr==null || strArr.length==0){
                return "";
            }
            for(String str:strArr){
                String[] ary = str.split(",");
                if (ary.length!=2){
                    return "false";
                }

                if (ary[0].startsWith("(")){
                    ary[0] = ary[0].substring(1);
                }
                if (ary[1].endsWith(")")){
                    ary[1] = ary[1].substring(0,ary[1].length()-1);
                }

                int c = Integer.parseInt(ary[0]);
                int p = Integer.parseInt(ary[1]);


                if (!parents.containsKey(p)){
                    parents.put (p,new TreeNode(p));
                }
                if (parents.get(p).left==null){
                    parents.get(p).left = new TreeNode(c);
                }else if (parents.get(p).right==null){
                    parents.get(p).right = new TreeNode(c);
                }else{
                    // debugInfo.append(p+"有超过2个子节点").toString();
                    //System.out.println(str.toString());
                    return "false";
                }

                if (!childs.containsKey(c)){
                    childs.put(c,new TreeNode(p,c));
                }else if (childs.get(c).left.value != c){
                    //debugInfo.append(c + "有>1个父节点");
                    return "false";
                }
            }


            Integer rootIndex = Arrays.asList(parents.keySet().toArray(new Integer[]{})).get(0);
            TreeNode rootNode = parents.get(rootIndex);
            parents.remove(rootIndex);
            childs.remove(rootNode.left.value);

            //构建binary tree
            AtomicInteger count = new AtomicInteger(parents.size()*4);
            createTreeNode(rootNode,rootNode,parents,childs,count);

            if (parents.size()>0) {
//            List<TreeNode> treeNodeList = new CopyOnWriteArrayList<>();
//            inorderTraversal(rootNode,treeNodeList);
//            List<Integer> treeValues =  treeNodeList.stream().map(item->{return item.value;}).collect(Collectors.toList());
//            List<String> list = parents.keySet().stream().map(p->{return "("+parents.get(p).left.value+","+ p +")";}).collect(Collectors.toList());
//            debugInfo.append("--BinaryTree:"+treeValues.toString());
//            debugInfo.append("--异常多出节点:"+list.toString());
//            System.out.println(debugInfo.toString());
                return "false";
            }else{
                return "true";
            }
        }


        public static TreeNode createTreeNode(TreeNode rootNode, TreeNode treeNode, Map<Integer, TreeNode> parents, Map<Integer, TreeNode> childs, AtomicInteger count){
            if (count.get()==0){
                return rootNode;
            }
            if (rootNode!=null && childs!=null && childs.containsKey(rootNode.value)){
                TreeNode node = childs.get(rootNode.value);
                node.left = rootNode;
                rootNode = node;
                parents.remove(node.value);
                childs.remove(node.left.value);
                count.decrementAndGet();
                return createTreeNode(rootNode,treeNode,parents,childs,count);
            }

            if (treeNode!=null && treeNode.left!=null && parents!=null && parents.containsKey(treeNode.left.value)){
                treeNode.left = parents.get(treeNode.left.value);
                parents.remove(treeNode.left.value);
                count.decrementAndGet();
                return createTreeNode(rootNode,treeNode.left,parents,childs,count);
            }

            if (treeNode!=null && treeNode.right!=null && parents!=null && parents.size()>0 && parents.containsKey(treeNode.right.value)){
                treeNode.right = parents.get(treeNode.right.value);
                parents.remove(treeNode.right.value);
                count.decrementAndGet();
                return createTreeNode(rootNode,treeNode.right,parents,childs,count);
            }

            return rootNode;
        }
    }


    public static class TreeNode{
        final private int value;
        private TreeNode left;
        private TreeNode right;


        public TreeNode(int i){
            this.value = i;
        }

        public TreeNode(int i,int leftValue){
            this.value = i;
            this.left = new TreeNode(leftValue);
        }

        public String getParentLeft(){
            return "("+ left.value +","+ value +")";
        }
    }


    public  static class StringPalindrome {


        public static String StringChallenge(String str) {
            // code goes here
            if (str==null || str.trim().length()==0){
                return "";
            }

            System.out.println(str);
            str = str.toLowerCase();

            for(int n=0;n<=2;n++){
                for(int j=0;j<=str.length()-n-2;j++){
                    String remove = str.substring(j,j+n);
                    String str1 =  str.substring(0,j) + str.substring(j+n);
                    System.out.println(",,remove=="+remove+",,str="+str+",,str1="+str1);
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
                        return remove;
                    }
                }
            }
            return "not possible";
        }
//
//        public static void main (String[] args) {
//            // keep this function call here
//            Scanner s = new Scanner(System.in);
//            StringChallenge(s.nextLine());
//            //System.out.print(StringChallenge(s.nextLine()));
//        }

    }
}
