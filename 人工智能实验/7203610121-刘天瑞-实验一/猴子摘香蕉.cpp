#include <iostream>
#include <cstdio>
#include <string>

//使用命名空间
using namespace std;

/*
项目名称（File Name）:猴子摘香蕉（monkey_grasp_banana）
日期（Date）:9.20
*/

//首先用一个结构体变量记录某一状态下猴子、箱子、香蕉的位置，以及猴子是否在箱子上、是否摘得香蕉 
typedef struct state
{
	string MONKEY;
	string BANANA;
	string BOX;
	int ON; //1表示猴子摘得香蕉，0表示没有 
	int HOLDS;	//1表示猴子在箱子上，0表示没有 
}state;

//函数声明 
void Goto(struct state& ps);
void Pushbox(struct state& ps);
void Climbbox(struct state& ps);
void ClimbDownbox(struct state& ps);

//主函数
int main() 
{
	int ON,HOLDS;
	struct state ps;
	cout << "用A,B,C三个字母分别输入猴子、香蕉、箱子在初始状态下的位置以及猴子是否在箱子上(1表示在，0代表没有),中间用空格隔开：" << endl;
	cin >> ps.MONKEY >> ps.BANANA >> ps.BOX >> ps.ON;//首先输入初始状态
	cout << "输入的猴子、香蕉、箱子的位置分别是：" << endl;
	cout << "monkey\tbanana\tbox" << endl;
	cout << ps.MONKEY << "\n\t" << ps.BANANA << "\n\t\t" << ps.BOX << endl;
	cout << "猴子摘香蕉的全过程如下所示：" << endl;
    if (ps.MONKEY != ps.BOX && ps.ON == 1)//输入错误判断
    {
        cout << "此情况不符合实际，输入错误。" << endl;
        return 0;
    }
    if (ps.BANANA != ps.BOX)//判断箱子和香蕉的位置
    {
        if (ps.MONKEY != ps.BOX)//判断猴子和箱子的位置
        {
            Climbbox(ps);
            Goto(ps);
        }
        Climbbox(ps);
        Pushbox(ps);
    }
	else
    {
        if (ps.MONKEY != ps.BOX)//判断猴子和箱子的位置
        {
            Climbbox(ps);
            Goto(ps);
        }
        if (ps.BANANA == ps.BOX && ps.BOX == ps.MONKEY && ps.ON == 1)//判断猴子是否摘到香蕉
	    {
	    	ps.HOLDS = 1;
		    cout << "猴子摘到了香蕉。\n" << endl;
		    return 0;
	    }
        Climbbox(ps);
    }
	ClimbDownbox(ps);
    if (ps.BANANA == ps.BOX && ps.BOX == ps.MONKEY && ps.ON == 1)//判断猴子是否摘到香蕉
	{
		ps.HOLDS = 1;
		cout << "猴子摘到了香蕉。\n" << endl;
		return 0;
	}
    return 0;
}

//猴子走到箱子处
void Goto(struct state& ps)
{
	string m;
    m = ps.MONKEY;
    ps.MONKEY = ps.BOX;
	cout << "\n猴子从 " << m << " 处走到 " << ps.BOX << " 处，" << endl;

}

//猴子搬起箱子推着走到香蕉处
void Pushbox(struct state& ps)
{
	string m;
	m = ps.BOX;
	ps.MONKEY = ps.BANANA;
	ps.BOX = ps.BANANA;
 	cout << "\n猴子将箱子从 " << m << " 处推着走到 " << ps.BANANA << " 处，" << endl;
}

//猴子爬到箱子上
void Climbbox(struct state& ps) 
{
	if (ps.ON == 1)
	{
		ps.ON = 0;
		cout << "\n猴子爬下了箱子，\n" << endl;
	}	 
}

//猴子爬下了箱子
void ClimbDownbox(struct state &ps)
{ 
    if (ps.ON == 0)
	{
		ps.ON = 1;
		cout << "\n猴子爬到箱子上，\n" << endl;
	}	 
}