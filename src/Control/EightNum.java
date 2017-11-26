package Control;


public class EightNum {
    //拼图问题可以简化为多数码问题，这里试试八数码的情况
    //另外，可以利用人工智能的算法求解该问题
    private int size = 3;
    private int[] array;
    private int inversionSum;

    public EightNum(int size)
    {
        this.size = size;
    }

    public boolean isSolvable(int[][] numField)
    {
        //这里使用的原理是n数码的原理，稍后会整理到Blog上
        //需要求逆序和，在《算法导论》的习题2-4中有所解释。
        //可以使用迭代来处理。

        //首先要把数组合并，并为size==4的情况做准备，找到9的位置。
        array =new int[size * size];
        int nineRow = size-1;
        for (int i = 0; i<size; i++)
            for (int j = 0; j<size; j++)
            {
                //这里的遍历其实有点问题，java好像是竖向存储的...
                //但是之前都这么写的，就先这样吧
                array[i*size+j] = numField[i][j];
                if (numField[i][j] == 9)
                    nineRow = i;
            }
        calInversion result = new calInversion(array);
        inversionSum = result.count(0, array.length - 1);

        if ((size == 3 || size == 5)& inversionSum%2 ==0)
        {
            //此时可解。
            return true;
        }
        else if((size ==4)&&(inversionSum%2==0))
        {
            //这个再说吧233
            //更新一下，对于size==4的情况，需要计算'9'的距离
            if ((inversionSum%2==0)==((size-1-nineRow)%2==0))
                return true;
            else
                return false;
        }
        else
         return false;
    }

}
