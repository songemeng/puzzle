package Control;

public class calInversion {
    //计算逆序数。
    private int[] array;
    public calInversion(int[] array)
    {
        this.array = array;
    }

    public int count(int p, int r)
    {
        int inversionCount = 0;
        if (p<r)
        {
            int mid = (p+r)/2;
            //递归
            inversionCount += count(p, mid);
            inversionCount += count(mid+1, r);
            //接下来是归并。
            inversionCount += mergeInversion(array, p, mid, r);

        }
        return inversionCount;
    }

    public int mergeInversion(int[]array, int p, int mid, int r)
    {
        int inversionCount = 0;
        int[] temp = new int[r-p+1];
        if(array.length < r)
            return inversionCount;
        int left = p;
        int right = mid + 1;
        int storeIndex = 0;
        while(left <= mid && right <= r)
        {
            if(array[left] > array[right])
            {
                //当前right存在逆序数，数目等于mid-left+1
                //左侧比右侧大了，此时左侧的排序都做好了。
                inversionCount += mid-left+1;
                temp[storeIndex] = array[right];
                right++;
            }
            else
            {
                temp[storeIndex] = array[left];
                left++;
            }
            storeIndex++;
        }
        if(left <= mid)
        {
            for(int i = left; i <= mid; i++)
            {
                temp[storeIndex] = array[i];
                storeIndex++;
            }
        }
        if(right <= r)
        {
            for(int i = right; i <= r; i++)
            {
                temp[storeIndex] = array[i];
                storeIndex++;
            }
        }

        for(int i = p; i <= r; i++)
        {
            array[i] = temp[i-p];

        }
        return inversionCount;
    }
    public static void main(String[] arg0)
    {
        //逆序和测试。
        int[] array = {9, 8, 7, 6, 5, 4, 3, 2};
        calInversion test = new calInversion(array);
        int result = test.count(0, array.length - 1);
        System.out.println(result);
    }

}
