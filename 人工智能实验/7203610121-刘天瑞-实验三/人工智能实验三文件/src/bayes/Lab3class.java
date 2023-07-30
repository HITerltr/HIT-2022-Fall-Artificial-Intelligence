package bayes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class Lab3class {

    public static void main(String[] args)  {
        ArrayList<String> arrayList = new ArrayList<>();//人名列表
        ArrayList<String> arrayList2 = new ArrayList<>();//问题列表
        double [] P;
        File f1,f2;
//        System.out.println("请输入文件名称\n");
        f1 = new File("carnetwork.txt");
        f2 = new File("carqueries.txt");
        Note cpt = geteverycpt(f1,arrayList);
        arrayList2 = getquestion(f2);
        double[] pro;//存全概率
        pro = allpro(cpt);
//        for(int i = 0;i < Math.pow(2, cpt.count);i++){
//        	System.out.println(pro[i]);
//        }
        for(int i=0;i < arrayList2.size();i++){
            P=getPT(arrayList2.get(i), cpt,arrayList,pro);
            System.out.println("P("+arrayList2.get(i)+") = \n : "+P[0]+" : "+P[1]);
        }
    }
    /*
     *查询概率并输出
     */
    public static double[] getPT(String question,Note cpt,ArrayList<String> arrayList,double[] pro){
        String[] strings=question.split(" \\| ");
        double[] answer=new double[2];
        int count=arrayList.indexOf(strings[0]);//前面的Note序号
        if(cpt.q[count]==0){
            answer[0]=cpt.t[0][0][count];
            answer[1]=cpt.t[0][1][count];
        }
        else{
            String[] strings2=strings[1].split(", ");
            String[] strings3;
            int sum=0;
            int[] count2=new int[strings2.length];//条件Note序号
            int [] TF=new int[strings2.length];//存储是t还是f
            for(int i=0;i<strings2.length;i++){
                strings3=strings2[i].split("=");
                count2[i]=arrayList.indexOf(strings3[0]);
                if(strings3[1].contains("true")){
                    TF[i]=1;
                }
                else{
                    TF[0]=0;
                }
            }
            double[] p1=compute(pro, count2, TF, count,cpt);
            double[] p2=compute(pro, count2, TF, -1,cpt);
            answer[0]=p1[0]/p2[0];
            answer[1]=p1[1]/p2[0];
            //System.out.println(answer[0]+" "+answer[1]);
        }
        return answer;
    }
    /*
     * 处理问题文本并返回问题
     */
    public static ArrayList<String> getquestion(File file){
        ArrayList<String> arrayList=new ArrayList<>();
        BufferedReader bfr1;
        try {
            bfr1=new BufferedReader(new FileReader(file));
            String string;
            while((string=bfr1.readLine())!=null){
                String[] strings=string.split("P\\(");
                if (strings.length == 0) {
                    continue;
                }
                for(int i=1;i<strings.length;i++){
                    arrayList.add(strings[i].substring(0,(strings[i].length()-1)));
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
    public static Note  geteverycpt(File file,ArrayList<String> arrayList){
        int count;//变量个数
        Note xinxi=new Note();
        BufferedReader bfr1;
        try {
            //读取变量个数
            bfr1=new BufferedReader(new FileReader(file));
            count=Integer.valueOf(bfr1.readLine());
            xinxi.count=count;
            bfr1.readLine();
            //读取随机事件的名字
            String[] strings=bfr1.readLine().split(" ");
            for(int i=0;i<strings.length;i++){
                arrayList.add(strings[i]);
            }
            bfr1.readLine();
            //读取变量关系表
            xinxi.q=new int[count];//存父节点个数
            xinxi. p=new int [count][count];//存节点关系
            xinxi.t=new double[(int)Math.pow(2,( count-1))][2][count];//存cpt o列为假 1列为真
            for(int i=0;i<5;i++){
                xinxi.arrayList.add(null);
            }
            for(int i=0;i<count;i++){
                HashMap<String, Integer> map=new HashMap<>();
                String[] tstrings=bfr1.readLine().split(" ");
                for(int j=0;j<tstrings.length;j++){
                    xinxi.p[i][j]=Integer.valueOf(tstrings[j]);
                    if(xinxi.p[i][j]==1){
                        xinxi.q[j]++;
                        if(!xinxi.arrayList.isEmpty()&&xinxi.arrayList.size()>j&&xinxi.arrayList.get(j)!=null){
                            xinxi.arrayList.get(j).put(arrayList.get(i), xinxi.q[j]);
                        }
                        else{
                            map.put(arrayList.get(i), xinxi.q[j]);
                            xinxi.arrayList.add(j,map);
//  							if(xinxi.arrayList.get(j+1)==null){
//  								xinxi.arrayList.remove(j+1);
//  							}
                        }
                    }
                }
            }
            //读取CPT
            for(int i=0;i<count;i++){
                bfr1.readLine();
                for(int j=0;j<=((int)Math.pow(2,( xinxi.q[i]))-1);j++){
                    String str4=bfr1.readLine();
                    String str3[]=str4.split(" ");
                    xinxi.t[j][0][i]=Double.valueOf(str3[0]);
                    xinxi.t[j][1][i]=Double.valueOf(str3[1]);
                }
            }
            bfr1.close();
            return xinxi;
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
    public static double[]  allpro(Note cpt){
        int sum = (int)Math.pow(2, cpt.count);
        String string;
        char[] cs;
        int[] shu = new int[cpt.count];
        double[] pro = new double[sum];
        for(int i = 0;i < sum;i++){
            string = Integer.toBinaryString(i);
            cs=string.toCharArray();
            for(int j = 0;j < cs.length;j++){
                shu[cs.length-1-j] = cs[j];
                shu[cs.length-1-j] -= 48;
            }
            for(int j = 0;j < cpt.count;j++){
                if(j == 0){//第一个需要使用加法
                    if(cpt.q[j]==0){
                        pro[i] = cpt.t[0][shu[cpt.count-1]][j];
                    }
                    else{
                        pro[i] = cpt.t[pos(cpt, shu,j)][shu[cpt.count-1]][j];
                    }
                }
                else{//以后的使用乘法
                    if(cpt.q[j]==0){
                        pro[i]=pro[i]*cpt.t[0][shu[cpt.count-1-j]][j];
                    }
                    else{
                        pro[i]=pro[i]*cpt.t[pos(cpt, shu,j)][shu[cpt.count-1-j]][j];
                    }
                }
            }
        }
        return pro;
    }
    /*
     * 计算在对应cpt表中位置
     */
    public static int  pos(Note cpt,int[] shu,int j){
        int post=0;
        int k=0;
        int i=0;
        while(true){
            if(i==cpt.count){
                break;
            }
            if(cpt.p[i][j]==1){
                if(shu[cpt.count-i-1]==1){
                    post+=(int)Math.pow(2, cpt.q[j]-k-1);
                    //System.out.println(i);
                }
                k++;
            }
            i++;
        }
        return post;
    }
    /*
     * 计算概率
     */
    public static double[]  compute(double[] pro ,int[] count2,int[] TF,int count,Note cpt){
        int[] cs=new int[cpt.count];//数据
        double sum[]=new double[2];
        int[] shuju=new int[cpt.count-count2.length];//未被确定的数据
        for(int i=0;i<count2.length;i++){
            cs[cpt.count-1-count2[i]]=TF[i];
        }
        int[] sum1=new int[2];
        for(int i = 0;i < count2.length;i++){
            if(TF[i]==1)
                sum1[0]+=Math.pow(2, cpt.count-1-count2[i]);
        }
        if(count!=-1){
//            System.out.println(count);
            sum1[1]+=sum1[0]+Math.pow(2,cpt.count-1- count);
        }
        if(count==-1){
            int k=0;
            for(int i=0;i<cpt.count;i++){
                int w=0;
                for(int j=0;j<count2.length;j++){
                    if(count2[j]==i){
                        w=1;
                        break;
                    }
                }
                if(w==0){
                    shuju[k]=cpt.count-1-i;
                    k++;
                }
            }
            for(int i=0;i<Math.pow(2,cpt.count-count2.length );i++){
                String string=Integer.toBinaryString(i);//化为二进制
                char[] cs2=string.toCharArray();
                int sum2=0;
                int[] shuju2=new int[cpt.count-count2.length];//放二进制的
                for(int j=0;j<string.length();j++){
                    shuju2[string.length()-1-j]=cs2[j];
                    shuju2[string.length()-1-j]-=48;
                }
                for(int j=0;j<cpt.count-count2.length;j++){
                    if(shuju2[j]==1)
                        sum2+=Math.pow(2, shuju[cpt.count-count2.length-1-j]);
                }
                sum[0]+=pro[sum2+sum1[0]];
            }
        }
        else{
            int k=0;
            for(int i=0;i<cpt.count;i++){
                int w=0;
                for(int j=0;j<count2.length;j++){
                    if(count2[j]==i||i==count){
                        w=1;
                        break;
                    }
                }
                if(w==0){
                    shuju[k]=cpt.count-1-i;
                    k++;
                }
            }
            for(int i=0;i<Math.pow(2,cpt.count-count2.length -1);i++){
                String string=Integer.toBinaryString(i);//化为二进制
                char[] cs2=string.toCharArray();
                int sum2=0;
                int[] shuju2=new int[cpt.count-count2.length-1];//放二进制的
                for(int j=0;j<string.length();j++){
                    shuju2[string.length()-1-j]=cs2[j];
                    shuju2[string.length()-1-j]-=48;
                }
                for(int j=0;j<cpt.count-count2.length-1;j++){
                    if(shuju2[j]==1){
                        sum2+=Math.pow(2, shuju[cpt.count-count2.length-2-j]);
                    }
                }
                //System.out.println(sum2+" "+sum1[1]+" "+sum1[0]);
                sum[0]+=pro[sum2+sum1[0]];
                sum[1]+=pro[sum2+sum1[1]];

            }
        }
        return sum;
    }
}