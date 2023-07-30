
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class Lab3  {

    public static void main(String[] args) {
        ArrayList<String> arrayList = new ArrayList<>();//节点名字列表
        ArrayList<String> arrayList2;//问题列表
        double[] P; //用来存储问题答案，P[0]表示true的概率，P[1]表示false的概率
        File f1, f2;
        f1 = new File("burglarnetwork.txt");
        f2 = new File("burglarqueries.txt");
        Note cpt = geteverycpt(f1, arrayList);  //用来获取贝叶斯网络的节点信息
        arrayList2 = getquestion(f2);
        double[] pro= allpro(cpt);//获取全概率
        for(int i=0;i < arrayList2.size();i++){
            P=getPT(arrayList2.get(i), cpt,arrayList,pro);
            System.out.println("P("+arrayList2.get(i)+") = \n  "+String.format("%.5f", P[0])+" , "+String.format("%.5f", P[1]));
        }

    }

    /*
     *查询概率并输出
     */
    public static double[] getPT(String question, Note cpt, ArrayList<String> arrayList, double[] pro) {
        String[] strings = question.split(" \\| ");
        double[] answer = new double[2];
        int query = arrayList.indexOf(strings[0]);//观看变量的坐标
        String[] strings2 = strings[1].split(", ");
        String[] strings3;
        int[] evidence = new int[strings2.length];// 证据的坐标
        int[] TF = new int[strings2.length];//存储是t还是f
        for (int i = 0; i < strings2.length; i++) {
            strings3 = strings2[i].split("=");
            evidence[i] = arrayList.indexOf(strings3[0]);
            if (strings3[1].contains("true")) {
                TF[i] = 1;
            } else {
                TF[i] = 0;
            }
        }
        double[] p1 = compute(pro, evidence, TF, query, cpt);

        answer[1] = p1[0] / (p1[0]+p1[1]);
        answer[0] = p1[1] / (p1[0]+p1[1]);

        return answer;
    }

    /*
     * 处理问题文本并返回问题
     */
    public static ArrayList<String> getquestion(File file) {
        ArrayList<String> arrayList = new ArrayList<>();
        BufferedReader bfr1;
        try {
            bfr1 = new BufferedReader(new FileReader(file));
            String string;
            while ((string = bfr1.readLine()) != null) {
                String[] strings = string.split("P\\(");
                if (strings.length == 0) {
                    continue;
                }
                for (int i = 1; i < strings.length; i++) {
                    arrayList.add(strings[i].substring(0, (strings[i].length() - 1)));
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("未找到文件！\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("read不了\n");
        }

        return arrayList;
    }

    /*
     * 处理贝叶斯网络文本并返回概率表
     */
    public static Note geteverycpt(File file, ArrayList<String> arrayList) {
        int count;//变量个数
        Note information = new Note();
        BufferedReader bfr1;
        try {
            //读取变量个数
            bfr1 = new BufferedReader(new FileReader(file));
            count = Integer.valueOf(bfr1.readLine());
            information.count = count;
            bfr1.readLine();
            //读取随机事件的名字
            String[] strings = bfr1.readLine().split(" ");
            for (int i = 0; i < strings.length; i++) {
                arrayList.add(strings[i]);
            }
            bfr1.readLine();
            //读取变量关系表
            information.q = new int[count];//存父节点个数
            information.p = new int[count][count];//存节点关系
            information.t = new double[count][(int) Math.pow(2, (count - 1))][2];//存cpt 0为假 1为真
            for (int i = 0; i < 5; i++) {
                information.arrayList.add(null);
            }
            for (int i = 0; i < count; i++) {
                HashMap<String, Integer> map = new HashMap<>();
                String[] tstrings = bfr1.readLine().split(" ");
                for (int j = 0; j < tstrings.length; j++) {
                    information.p[i][j] = Integer.valueOf(tstrings[j]);
                    if (information.p[i][j] == 1) {
                        information.q[j]++;
                        if (!information.arrayList.isEmpty() && information.arrayList.size() > j && information.arrayList.get(j) != null) {
                            information.arrayList.get(j).put(arrayList.get(i), information.q[j]);
                        } else {
                            map.put(arrayList.get(i), information.q[j]);
                            information.arrayList.add(j, map);
//  							if(xinxi.arrayList.get(j+1)==null){
//  								xinxi.arrayList.remove(j+1);
//  							}
                        }
                    }
                }
            }
            //读取CPT
            for (int i = 0; i < count; i++) {
                bfr1.readLine();
                for (int j = 0; j <= ((int) Math.pow(2, (information.q[i])) - 1); j++) {
                    String str4 = bfr1.readLine();
                    String str3[] = str4.split(" ");
                    information.t[i][j][1] = Double.valueOf(str3[0]);
                    information.t[i][j][0] = Double.valueOf(str3[1]);
                }
            }
            bfr1.close();
            return information;
        } catch (FileNotFoundException e) {
            System.out.println("未找到文件！\n");
        } catch (NumberFormatException e) {
            System.out.println("数格式不对\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 计算所有的全概率,r认为第一个节点为最高位
     */
    public static double[] allpro(Note cpt) {
        int sum = (int) Math.pow(2, cpt.count);
        int[] flag = new int[cpt.count];  //取值为0或者1
        double[] pro = new double[sum];
        for (int i = 0; i < sum; i++) {
            pro[i] = 1;
        }
        for (int i = 0; i < sum; i++) {

            //将十进制转换为二进制，存入flag数组
            flag = binarychange(i,flag.length);
            for (int j = 0; j < cpt.count; j++) {//对每个节点开始累乘
                if (cpt.q[j] == 0) {//如果没有父节点，先验概率
                    pro[i] *= cpt.t[j][0][flag[j]];
                } else {
                    pro[i] *= cpt.t[j][pos(cpt, flag, j)][flag[j]];
                }
            }
            for (int j = 0; j < flag.length; j++) {
                flag[j] = 0;
            }
        }
        return pro;
    }

    /*
     * 计算该节点条件概率列表中所在位置
     */
    public static int pos(Note cpt, int[] flag, int j) {
        int post = 0;
        int k = 0;
        for (int i = 0; i < cpt.count; i++) {
            if (cpt.p[i][j] == 1) {
                if (flag[i] == 1) {
                    post += (int) Math.pow(2, cpt.q[j] - 1 - k);
                }
                k++;
            }
        }
        return post;
    }

    /*
     * 计算给定公式的概率，采用累加法
     */
    public static double[] compute(double[] pro, int[] evidence, int[] TF, int query, Note cpt) {
        int [] flag = new int[cpt.count];//用来存储每个随机变量真假
        double answer[] = new double[2]; //用来存观察变量取真or假的概率值
        int hlength = cpt.count-evidence.length-1; //隐藏变量的个数
        int[] data = new int[hlength];//隐藏变量坐标
        for (int i = 0; i < evidence.length; i++) {
            if(TF[i]==1){
                flag[evidence[i]]=1;
            }
        }
        //将隐藏变量装进数组
        int k = 0;
        for (int i = 0; i < cpt.count; i++) {
            boolean flag1 = true;
            for (int j = 0; j < evidence.length; j++) {
                if(i==evidence[j]||i==query){
                    flag1=false;
                    break;
                }
            }
            if (flag1) {
                data[k] =  i;
                k++;
            }
        }
        for (int i = 0; i < Math.pow(2, hlength); i++) {
            int sum1;
            int[] data2 = new int[hlength];//放二进制的
            data2=binarychange(i,data2.length);
            for (int j = 0; j < hlength; j++) {
                if (data2[j] == 1) {
                    flag[data[j]]=1;
                }
            }
            flag[query]=0;
            sum1=decimalchange(flag);
            answer[0] += pro[sum1];

            flag[query]=1;
            sum1=decimalchange(flag);
            answer[1] += pro[sum1];
            for (int j = 0; j < hlength; j++) {
                flag[data[j]]=0;
            }
        }
        return answer;
    }

    //实现将十进制数字转化为存放该数二进制的数组
    public static int [] binarychange(int i,int length){
        int[] flag=new int[length];
        String string = Integer.toBinaryString(i);
        char[]cs = string.toCharArray();
        for (int j = 0; j < cs.length; j++) {
            flag[cs.length - 1 - j] = cs[j];
            flag[cs.length - 1 - j] -= 48;
        }

        for (int j = 0; j < flag.length / 2; j++) {
            int temp = flag[j];
            flag[j] = flag[flag.length - j - 1];
            flag[flag.length - j - 1] = temp;
        }
        return flag;
    }

    //实现将二进制数组转化为十进制数字
    public  static  int decimalchange(int []flag){
        int sum=0;
        for (int i = 0; i < flag.length; i++) {
            if(flag[i]==1){
                sum+=(int)Math.pow(2,flag.length-1-i);
            }
        }
        return sum;
    }
}




