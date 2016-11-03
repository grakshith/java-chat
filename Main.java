import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
public class Main implements ActionListener, FocusListener
{
    JFrame main_frame = new JFrame("Chat Room");
    JButton send = new JButton("Send");
    JTextField input = new JTextField("Enter your message here",40);
    JPanel south_panel = new JPanel(new GridBagLayout());
    JPanel north_panel = new JPanel();
    JPanel east_panel = new JPanel();
    JPanel central = new JPanel(new GridBagLayout());
    JTextArea chat = new JTextArea(100,100);
    JButton jb = new JButton("Random");
    public void show()
    {
        main_frame.setVisible(true);
        main_frame.getContentPane().add(south_panel,BorderLayout.SOUTH);
        // main_frame.getContentPane().add(north_panel,BorderLayout.WEST);
        // main_frame.getContentPane().add(east_panel,BorderLayout.EAST);
        main_frame.getContentPane().add(central,BorderLayout.CENTER);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx=10;
        c.weighty=20;
        //c.ipadx=50;
        c.anchor=GridBagConstraints.NORTHWEST;
        c.fill=GridBagConstraints.HORIZONTAL;
        central.add(north_panel,c);
        central.add(east_panel,c);
        south_panel.add(input,c);
        south_panel.add(send);
        north_panel.add(chat,c);
        east_panel.add(jb);
        chat.setEditable(false);
        input.addFocusListener(this);
        main_frame.setSize(640,480);
        main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
    public void actionPerformed(ActionEvent e)
    {
        System.out.println("ActionPerformed");
    }
    public void focusGained(FocusEvent e)
    {
        if(e.getComponent()==input)
            input.setText("");
    }
    public void focusLost(FocusEvent e)
    {
        if(e.getComponent()==input)
            input.setText("Enter your message here");
    }
    public static void main(String args[])
    {
        Main obj = new Main();
        obj.show();
    }
}