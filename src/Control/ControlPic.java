package Control;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ControlPic
{
    private String pathName;
    public ControlPic(String pathName)
    {
        this.pathName = pathName;
    }
//    String pathName = this.getClass().getResource("/images/subImages/").getFile();

    public void deletePics()
    {
        //仿佛删除了所有的文件
        File file = new File(pathName + "images/subImages/");
        if (file.isDirectory())
        {
            String[] fileList = file.list();
            int fileNum = fileList.length;
            if (fileNum > 0)
            {
                for (int i = 0; i < fileNum; i++)
                {
                    String fileDetailName = pathName + "images/subImages/" + fileList[i];
                    File f = new File(fileDetailName);
                    if(f.isFile())
                        f.delete();
                }
            }
        }
    }

    public void cutPicture(int size, int rowSize, int colSize, String preName, String imageName)
    {
        //这里的size当做一个规模来看，对于不同图片的尺寸，单独调用图片来看。
        String fileName = this.getClass().getResource("/images/"+imageName).getFile();
        File file = new File(fileName);
        try
        {
            //利用输入流输入原来的图片
            FileInputStream fileInput = new FileInputStream(file);
            BufferedImage bufImage = ImageIO.read(fileInput);
            fileInput.close();//用完就可以关闭啦，该存的存到Buffer就OK了
            //这里使用比较小的作为尺寸。
            int picSize = (bufImage.getHeight() < bufImage.getWidth()) ? bufImage.getHeight() : bufImage.getWidth();
            size = picSize/size;

            for (int row = 0;row < rowSize; row++)
            {
                for (int col = 0; col < colSize; col++)
                {
                    int picIndex = row*colSize + col + 1;
                    String picFileName = pathName + "images/subImages/" + preName + "_" + picIndex + ".jpg";
//                    String picFileName = this.getClass().getResource("/").getFile()+ "images/subImages/" + preName+"_" + picIndex+".jpg";
//                    System.out.println(picFileName);//打印路径
                    //如果没有路径，就创建一个吧
                    File newPath = new File(pathName+ "images/subImages/");
                    if (!newPath.exists())
                    {
                        //如果目录不存在 则创建目录
                        newPath.mkdirs();
                        System.out.println("make dirs!");
                    }
                    BufferedImage smallImage = bufImage.getSubimage(col*size, row*size, size, size);
                    FileOutputStream fileOutput = new FileOutputStream(picFileName);
                    ImageIO.write(smallImage, "jpg", fileOutput);
                    fileOutput.close();

                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
