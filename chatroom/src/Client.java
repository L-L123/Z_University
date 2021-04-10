
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.UnexpectedException;
import java.util.Iterator;

public class Client extends JFrame {
    //JTextArea ta = new JTextArea(10, 20);
    TextField tf = new TextField(40);
    JPanel jPanel1=new JPanel();
    JPanel jPanel = new JPanel();
    JButton send=new JButton("发送");
    JScrollPane jsp=new JScrollPane(jPanel1);
    private Socket clientsocket = null;
    private DataOutputStream dataOutputStream = null;
    private boolean isconn = false;

    File file = new File("message.txt");

    public Client() {
        super();
    }

    public void init() {
        this.setTitle("客户端窗口");
        this.setBounds(300, 300, 400, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        this.add(jsp,BorderLayout.CENTER);
        //jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        //jsp.add(jPanel1);

        //this.add(jPanel1,BorderLayout.CENTER);
        jPanel1.setLayout(new GridLayout(100,1));


        this.add(jPanel, BorderLayout.SOUTH);
        jPanel.setLayout(new FlowLayout());
        jPanel.add(tf);
        jPanel.add(send);



        //ta.setLineWrap(true);//激活自动换行功能
        // ta.setWrapStyleWord(true);//激活断行不断字功能


        try {
            clientsocket = new Socket("127.0.0.1", 8888);
            //表示连上服务器
            isconn = true;

            //按行读取内容
            String str="";   //每行的内容
            int line =1;     //行号
            FileInputStream fip = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fip);
            BufferedReader bufferedReader = new BufferedReader(reader);
            while ((str=bufferedReader.readLine())!=null) {

                JLabel jLabel1=new JLabel("");
                jLabel1.setText(str+"\n");
                jPanel1.add(jLabel1);

                line ++;
            }

            reader.close();
            fip.close();
            bufferedReader.close();


        } catch (UnexpectedException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.setVisible(true);

        new Thread(new Receive()).start();  //启动多线程


        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sstr = tf.getText();

                if (sstr.equals("")) {
                    JOptionPane.showMessageDialog(null, "消息不能为空");
                    return;
                }
                // send string
                send(sstr);
                tf.setText("");

            }
        });
    }



    public void send(String str)  {
        try {
            dataOutputStream = new DataOutputStream(clientsocket.getOutputStream());
            dataOutputStream.writeUTF(str);

            JLabel tempLabel = new JLabel(   str + " : 我 "+" "  +"\n");

            tempLabel.setHorizontalAlignment(JLabel.RIGHT);
            jPanel1.add(tempLabel);
            tempLabel.revalidate();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    class Receive implements Runnable{

        public void showMsg(String msg) {

            JLabel tempLabel = new JLabel(msg);
            jPanel1.add(tempLabel);
            jPanel1.revalidate();

        }

        @Override
        public void run() {
            try {

                while (isconn) {
                    DataInputStream dis = new DataInputStream(clientsocket.getInputStream());
                    String str = dis.readUTF();
                    this.showMsg(str);
                    System.out.println("接收到的消息："+str);
                    //ta.append(str);

                    //xieruwenjian
                    //FileOutputStream fop = new FileOutputStream(file,true);

                    //每个线程号创建一个文本记录信息
                    //String logfilename = this.hashCode()+"_log.txt";
                    // File logfile = new File(logfilename);
                    // FileOutputStream fop = new FileOutputStream(logfile,true);

                    //OutputStreamWriter writer = new OutputStreamWriter(fop,"UTF-8");
                    // writer.append(str+"\n");
                    // writer.close();
                    // fop.close();

                }

            } catch (SocketException se){
                // System.out.println("服务器意外终止！");
                //ta.append("服务器意外终止了！");

            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }


    public void showMsg(String msg, boolean isMyMsg) {

        JLabel tempLabel = new JLabel(msg);

        // if(isMyMsg) {
        //这条消息显示在右边（本窗口发出的消息）
        // tempLabel.setHorizontalAlignment(JLabel.RIGHT);
        //}else {
        // tempLabel.setHorizontalAlignment(JLabel.LEFT);
        // }
        jPanel1.add(tempLabel);
        tempLabel.revalidate();
    }
    public static void main(String[] args) throws IOException {
        Client client=new Client();
        client.init();


    }
}