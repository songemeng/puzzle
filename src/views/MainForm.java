package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import Control.ControlPic;
import Control.EightNum;
import Control.MySQL;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;

public class MainForm extends JFrame implements ActionListener{
    private JPanel contentPanel;
    private JPanel imagePanel;
    private JButton btnDoc = new JButton("Rank");
    private JButton btnMusic = new JButton("Play Music");
    private JButton btnStart = new JButton("Play!");
    private JComboBox boxPic;
    private JComboBox boxGrade;
    private JLabel prePic;
    private final JLabel aLabel = new JLabel("last time:");
    private JLabel lblLeftTime = new JLabel("1200");
    //空白格的坐标
    private int blankRow = 2;
    private int blankCol = 2;
    private ImageButton[][] btnField = null;
    private int[][] numField;
    private int gradeNum = 3;//几层的
    private int picIndex = 1;
    private boolean isRun = false;

    private String imgPathName;
    private AudioClip sound = null;

    private int leftTime;
    private Timer timer;


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                //主要的消息处理函数就是new一个新的MainForm
                try
                {
                    new MainForm();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }



    public MainForm()
    {
        //构造方法。
        JFrame frame = new JFrame("Puzzle by 松鹅");
        contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5,5,5,5));
        contentPanel.setLayout(null);

        imagePanel = new JPanel();
        imagePanel.setBackground(Color.orange);
        imagePanel.setBounds(15, 10, 495, 495);
        contentPanel.add(imagePanel);

        btnDoc.setBounds(576, 426, 113, 27);
        contentPanel.add(btnDoc);
        btnDoc.addActionListener(this);

        btnMusic.setBounds(576, 376, 113,27);
        contentPanel.add(btnMusic);
        btnMusic.addActionListener(this);

        btnStart.setBounds(576, 478, 113,27);
        contentPanel.add(btnStart);
        btnStart.addActionListener(this);

        String[] strPic = {"Choose Pic","test1", "test2", "test3"};
        boxPic = new JComboBox(strPic);
        boxPic.setBounds(576, 283, 113, 24);
        contentPanel.add(boxPic);

        String[] strGrade = {"Choose Grade","test1", "test2", "test3"};
        boxGrade = new JComboBox(strGrade);
        boxGrade.setBounds(576, 320, 113, 24);
        contentPanel.add(boxGrade);

        prePic = new JLabel("Preview Picture");
        prePic.setBounds(520,10, 220, 220);
        contentPanel.add(prePic);

        aLabel.setBounds(520,240,110,20);
        contentPanel.add(aLabel);

        lblLeftTime.setBounds(630,240,110,20);
        contentPanel.add(lblLeftTime);

        URL urlSound = this.getClass().getResource("/musics/test.wav"); //java一般不支持MP3文件
        //下面这种形式已经不建议用了- -
        sound = Applet.newAudioClip(urlSound);
//        String musicFile = "music/test.wav";
        //使用Javafx的
//        String musicFile = this.getClass().getResource("/musics/test.wav").getFile();
//
//        Media sound = new Media(new File(musicFile).toURI().toString());
//        MediaPlayer mediaPlayer = new MediaPlayer(sound);
//        mediaPlayer.play();

        frame.setLayout(null);
        frame.setBounds(100, 100, 770, 560);
        frame.setContentPane(this.contentPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        //主界面几个按钮的响应
        if(e.getSource() == btnMusic)
        {
            musicSwitch(e);
        }
        else if(e.getSource() == btnDoc)
        {
            //这个按钮改成排行榜吧...
            new RankForm();
        }
        else if(e.getSource() == btnStart)
        {
            if(isRun)
            {
                //正在运行，戳这个就说明要重新开始了。
                int n = JOptionPane.showConfirmDialog(this, "restart?");
                if (n == 0) {
                    init();
                    startThead();
                }
            }
            else
            {
                //这是第一次运行啦
                init();
                startThead();
                isRun = true;
                btnStart.setText("Play Again");
            }
        }
        else
        {
            switchImage(e);
        }

    }

    public void switchImage(ActionEvent e)
    {
        //吐槽一句，这里居然是强制类型转换的...觉得应该加一个try
        ImageButton switchBtn = (ImageButton) e.getSource();
        int row = switchBtn.getRow();
        int col = switchBtn.getCol();

        //接下来判断是否相邻
        if(Math.abs(row - blankRow) + Math.abs(col - blankCol) == 1)
        {
            //交换两个该位置上的序号就可以。同时让numField也随之变化。
            int tempNum = btnField[row][col].getNum();
            btnField[row][col].setNum(btnField[blankRow][blankCol].getNum());
            btnField[blankRow][blankCol].setNum(tempNum);
            btnField[blankRow][blankCol].updateImage(true);
            btnField[row][col].updateImage(false);

            numField[row][col] = numField[blankRow][blankCol];
            numField[blankRow][blankCol] = tempNum;

            blankCol = col;
            blankRow = row;

            //再判断一下是否游戏成功
            if (isGameOver())
            {
                JOptionPane.showMessageDialog(this,"You win!");
                //接着要把数据插入数据库。
                String name = JOptionPane.showInputDialog("What's your name?");
                if (name == null|| "".equals(name.trim()))
                    name = "No NAME";
                //这里应该为了防止SQL注入进行一些人工的判断
                //比如判断有没有特殊字符比如'之类的。
                //不过想一想，构造起来也挺难的，那我也不判断了吧hhh
                int time = 1200-leftTime;//测试数据
                MySQL database = new MySQL();
                if(!database.connectDataBase())
                    JOptionPane.showMessageDialog(this,"connect failed!");
                if(!database.insertNewItem(name, time))
                    JOptionPane.showMessageDialog(this, "insert failed!");
                database.closeDataBase();
            }
        }
    }
    public boolean isGameOver()
    {
        boolean flag = true;
        for (int i = 0; i<gradeNum; i++)
        {
            for (int j = 0; j<gradeNum; j++)
            {
                if (numField[i][j] != i*gradeNum + j + 1)
                {
                    flag = false;
                    return flag;
                }
            }
        }
        return flag;
    }
    public void init()
    {

        //游戏初始化进程
        getFormStatus();
//      初始化页面的东西
        setFormStatus();

        // 切割图片等操作
        imgPathName = this.getClass().getResource("/").getFile();

        ControlPic ctrlPic = new ControlPic(imgPathName);
        ctrlPic.deletePics();
        //为啥要用时间作为前缀？？？
        long l = System.currentTimeMillis();
        String preName = String.valueOf(l);
        //出于不同图片大小的考虑，这里的size应该修改一下
        ctrlPic.cutPicture(gradeNum, gradeNum, gradeNum, preName, picIndex+".jpg");

        //产生Field上的内容  initField();
        for (int i = 0; i<gradeNum; i++)
        {
            for (int j = 0; j<gradeNum; j++)
            {
                ImageButton btn = new ImageButton(i, j, numField[i][j],495/gradeNum, preName+"_");
                imagePanel.add(btn);
                //这里有个问题，这里的赋值btn是什么赋值过去了？如果在python，可能后来就冲突了-。-
                btnField[i][j] = btn;
                btnField[i][j].addActionListener(this);
            }
        }
        //去掉最后一个，初始化完成。
        blankRow = gradeNum-1;
        blankCol = gradeNum-1;
        btnField[blankRow][blankCol].updateImage(false);

        //这里搞一下判断测试一下hh
//        EightNum test = new EightNum(gradeNum);
//        if(test.isSolvable(numField))
//        {
//            JOptionPane.showMessageDialog(this,"it's solvable");
//        }
//        else
//            JOptionPane.showMessageDialog(this,"it isn't solvable!Play again!");

    }
    
    public void getFormStatus()
    {
        //获取现在面板上的状态；
        picIndex = boxPic.getSelectedIndex();
        if(picIndex == 0)
            picIndex = 1;//最初始的状态，就默认为第一种。
        gradeNum = boxGrade.getSelectedIndex();
        switch (gradeNum)
        {
            case 0:
            case 1:
                gradeNum = 3;
                break;
            case 2:
                gradeNum = 4;
                break;
            case 3:
                gradeNum = 5;
                break;
            default:
                gradeNum = 3;
                break;
        }
    }
    public void setFormStatus()
    {
        Random rand = new Random();
        URL url;
        //设置预览位置。
        try
        {
            url = this.getClass().getResource("/images/" + picIndex + ".jpg");
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this,"picture lost!");
            return;
        }
        ImageIcon smallPic = new ImageIcon(url);
        smallPic.setImage(smallPic.getImage().getScaledInstance(220, 220, Image.SCALE_DEFAULT));
        //设置pic的尺寸
        prePic.setIcon(smallPic);
        //初始化btnField
        btnField = new ImageButton[gradeNum][gradeNum];
        imagePanel.removeAll();
        imagePanel.setLayout(new GridLayout(gradeNum,gradeNum));

        //初始化二维数组numField
        numField = new int[gradeNum][gradeNum];
        for (int i=0; i<gradeNum; i++)
        {
            for (int j=0; j<gradeNum; j++)
            {
                //初始化数组，类似数组的计数。
                numField[i][j] = i*gradeNum + j + 1;
            }
        }

        //接下来要打乱。暂时用随机的算法，以后可以考虑用人工智能的办法。
        //这个随机算法是每一个和一个随机的位置进行交换。


        //目前测试了一下，这个算法的成功率不是很高，那就采取，不成功就重新随机的办法来弄。
        EightNum test = new EightNum(gradeNum);
        do {
            for (int i = 0; i < gradeNum; i++) {
                for (int j = 0; j < gradeNum; j++) {
                    if (i == gradeNum - 1 && j == gradeNum - 1)
                        break;//最后一个不随机。
                    //随机选中一个位置，和当前位置调换。
                    int x = rand.nextInt(gradeNum - 1);
                    int y = rand.nextInt(gradeNum - 1);
                    if (x == gradeNum - 1 && y == gradeNum - 1)
                        continue;
                    int temp = numField[i][j];
                    numField[i][j] = numField[x][y];
                    numField[x][y] = temp;

                }
            }
            //应该不会人品特别差 永远不出来吧hhh
        }while (!test.isSolvable(numField));

    }
    public void musicSwitch(ActionEvent e) {
        JButton btn = (JButton)e.getSource(); //事件源指向音乐按钮

        if("Play Music".equals(btn.getText().trim())) {
            sound.loop();
            btn.setText("Close Music");
        }else {
            sound.stop();
            btn.setText("Play Music");
        }
    }




    public class MyTask extends TimerTask {

        @Override
        public void run() {
            //-1s
            leftTime--;
            lblLeftTime.setText(leftTime+"");
            if(leftTime == 0) {
                lblLeftTime.setText(leftTime+"");
                JOptionPane.showMessageDialog(null, "You have no time!");
                this.cancel();

            }
        }

    }
    public void startThead() {
        if(timer !=null)      //避免重新游戏后多个任务同时进行
            timer.cancel();

        leftTime = 1200;
        timer = new Timer();
        timer.schedule(new MyTask(), 1000,1000); //一秒后启动线程，每隔一秒响应线程
    }
}
