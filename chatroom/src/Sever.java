import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

public class Sever extends JFrame {
    JTextArea serverta=new JTextArea();
    //JLabel jLabel = new JLabel();
    JPanel jp =new JPanel();
    JButton startBtn=new JButton("启动");
    JButton stopBtn=new JButton("停止");
    JScrollPane jsp=new JScrollPane(serverta);

    private ServerSocket serverSocket=null;
    private Socket socket=null;

    private ArrayList<ClientConn> cclist=new ArrayList<ClientConn>();
    private boolean isstart=false;
    private DataInputStream dis=null;

    File file = new File("message.txt");

    public Sever() throws Exception {
        this.setTitle("服务器端");
        this.add(jsp, BorderLayout.CENTER);
        this.add(jp, BorderLayout.SOUTH);
        jp.add(startBtn);
        jp.add(stopBtn);
        this.setBounds(0, 0, 500, 500);
        serverta.setEditable(false);
        this.setVisible(true);


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (isstart) {
            serverta.append("服务器已经启动！\n");
        } else {
            serverta.append("服务器还没有启动，请点击启动按钮！\n");
        }

        stopBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {


                try {
                    if(serverSocket!=null){
                        serverSocket.close();
                        isstart=false;
                    }

                    System.out.println("服务器停止服务！");
                    System.exit(0);
                }catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });


        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (serverSocket == null) {
                        serverSocket = new ServerSocket(8888);
                    }
                    isstart = true;
                    serverta.append("服务器已经启动！" + "\n");


                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        startsever();

    }

    //startsever
    public void startsever() throws Exception{
        try {
            try {
                serverSocket = new ServerSocket(8888);
                isstart=true;
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            while (isstart) {
                socket = serverSocket.accept(); //一个连接
                cclist.add(new ClientConn(socket)); //加入到连接对象
                System.out.println("一个客户端连接了服务器，" + socket.getInetAddress() + "/" + socket.getPort());
                serverta.append("一个客户端连接了服务器，" + socket.getInetAddress() + "/" + socket.getPort()+"\n");
                //recivestr();
            }
        } catch (SocketException e) {
            //System.out.println("服务器终断了！！！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //服务器停止的方法


    //服务器端接收数据的方法
    //服务器的接收应该是多线程的接收
    /*public void recivestr(){
        try{
            dataInputStream=new DataInputStream(socket.getInputStream());
            String str=dataInputStream.readUTF();
            System.out.print(str);
            serverta.append(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/
//属于服务器端的一个连接对象
  class ClientConn implements Runnable {
        Socket socket = null;

        public ClientConn(Socket socket) {
            this.socket = socket;
            (new Thread(this)).start();
        }

        //同时接收客户端信息，多线程接收数据
        @Override
        public void run() {
            try {
                DataInputStream  dis = new DataInputStream(socket.getInputStream());
                //为了让服务器能够接收到每个客户端的多条信息
                while (isstart) {
                    String str = dis.readUTF();  //阻塞性方法
                    System.out.println(" "+" 编号为："+socket.getPort() + "说: " + str + "\n");
                    serverta.append((" "+" 编号为："+socket.getPort() + "说: " + str + "\n"));
                    String sendstr = " "+" 编号为："+socket.getPort() + "说: " + str + "\n";


                    //在文本中写入信息
                    FileOutputStream fop = new FileOutputStream(file,true);
                    OutputStreamWriter writer = new OutputStreamWriter(fop,"UTF-8");
                    writer.append(sendstr);
                    writer.close();
                    fop.close();

                    //bian li cclist send string,客户端是多线程的接收
                    Iterator<ClientConn> it =cclist.iterator();
                    while(it.hasNext()) {
                        ClientConn next=it.next();
                        if(!(next.socket.getPort()==this.socket.getPort())) {
                           // System.out.println(this.socket.getPort()+"---->"+next.socket.getPort()+"MSG is: "+str);
                            next.send(sendstr);
                        }
                    }
                }
            } catch (SocketException se){
                System.out.println("一个客户端下线了");
                serverta.append(socket.getPort() +"客户端下线了"+"\n");
            } catch (IOException e) {
                e.printStackTrace();
                if(cclist.isEmpty()){
                    try{
                        dis.close();
                    }catch (IOException e1){
                        System.out.println("din关闭失败");
                        e1.printStackTrace();
                    }
                }
            }
        }
            //每个连接对象发送数据的方法
            public void send(String str){
                try {
                    DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());
                    dos.writeUTF(str);
                } catch (SocketException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();

                }
            }

        }
    public static void main(String[] args){
        try {
            Sever sever= new Sever();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
