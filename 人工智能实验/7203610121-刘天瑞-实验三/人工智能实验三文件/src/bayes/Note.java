package bayes;

import java.util.ArrayList;
import java.util.HashMap;

public class Note {
    int count;//有的多少个变量
    double[][][] t;//所有mat表
    int[][] p;//点的关系表
    int []  q;//各个节点父节点个数
    ArrayList<HashMap<String, Integer>> arrayList=new ArrayList<>();
}