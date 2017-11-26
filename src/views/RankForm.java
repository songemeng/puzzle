package views;

import javax.swing.*;
import Control.MySQL;

public class RankForm extends JFrame{

    private JPanel rankPanel;
    private JTextArea rankContent;

    public RankForm()
    {
        init();
    }

    public void init()
    {
        JFrame rankframe = new JFrame("排行榜");
        rankPanel = new JPanel();
        rankContent = new JTextArea();
        rankframe.setBounds(200, 200, 250, 250);
        rankPanel.setBounds(20,20,210,210);
        rankframe.setContentPane(rankPanel);
        rankPanel.add(rankContent);

        MySQL getItems = new MySQL();
        getItems.connectDataBase();
        String content = getItems.getAllItem();
        getItems.closeDataBase();
        rankContent.setText(null);
        rankContent.setEditable(false);
        rankContent.setText(content);


        rankframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        rankframe.setVisible(true);
    }
    //测试
    public static void main (String[] arg0)
    {
        new RankForm();
    }
}
