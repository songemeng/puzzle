package views;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.*;


public class ImageButton extends JButton
{
    private int row, col, num, size;
    //行列和图片编号
    private String preName;

    public ImageButton(int row, int col, int num, int size, String preName)
    {
        super();
        this.row = row;
        this.col = col;
        this.num = num;
        this.size = size;
        this.preName = preName;
        updateImage(true);//首次创建，直接显示。
    }

    public void updateImage(boolean isShow)
    {
        if(isShow)
        {
            String pathName = this.getClass().getResource("/").getFile();
//            String picName = this.getClass().getResource("/images/subImages/" + preName + num + ".jpg").getFile();
            String picName = pathName + "images/subImages/" + preName + num + ".jpg";
            ImageIcon icon = new ImageIcon(picName);
            icon.setImage(icon.getImage().getScaledInstance(size,size, Image.SCALE_DEFAULT));
            this.setIcon(icon)
        }
        else
            this.setIcon(null);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getNum() {
        return num;
    }

    public String getPreName() {
        return preName;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setPreName(String preName) {
        this.preName = preName;
    }
}
