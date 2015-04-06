package com.example.aron.battlebot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Aron on 4/4/2015.
 */
public class Play extends Activity {


    Thread network;
    String port;
    String hostname;
    TextView output;
    String userInput;
    String[] status;
    double enemyx;  //closest enemy x
    double enemyy;  //closest enemy y
    double enemyd;  //closest enemy distance
    double x; //your x
    double y; //your y
    double powerx; //powerup x
    double powery; //powerup y
    ImageButton up;
    ImageButton down;
    ImageButton left;
    ImageButton right;
    ImageButton upleft;
    ImageButton upright;
    ImageButton downleft;
    ImageButton downright;
    Button noop;
    Button firepowerup;
    Button scan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        output = (TextView) findViewById(R.id.textView_debug);
        output.append("\n");
        Intent intent = getIntent();
        hostname = intent.getStringExtra("IP");
        port = intent.getStringExtra("port");

        up = (ImageButton) findViewById(R.id.imageButton_up);
        down = (ImageButton) findViewById(R.id.imageButton_down);
        left = (ImageButton) findViewById(R.id.imageButton_left);
        right = (ImageButton) findViewById(R.id.imageButton_right);
        upright = (ImageButton) findViewById(R.id.imageButton_upright);
        upleft = (ImageButton) findViewById(R.id.imageButton_upleft);
        downright = (ImageButton) findViewById(R.id.imageButton_downright);
        downleft = (ImageButton) findViewById(R.id.imageButton_downleft);
        noop = (Button) findViewById(R.id.button_noop);
        firepowerup = (Button) findViewById(R.id.button_firepowerup);
        scan = (Button) findViewById(R.id.button_scan);


        doNetwork stuff = new doNetwork();
        network = new Thread(stuff);
        network.start();

    }



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            output.append(msg.getData().getString("msg"));
        }

    };
    public void mkmsg(String str) {
        //handler junk, because thread can't update screen!
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("msg", str);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    public void moveUp(View view){
        userInput = "move 0 -1";
    }
    public void moveUpLeft(View view){
        userInput = "move -1 -1";
    }
    public void moveUpRight(View view){
        userInput = "move 1 -1";
    }
    public void moveLeft(View view){
        userInput = "move -1 0";
    }
    public void moveRight(View view){
        userInput = "move 1 0";
    }
    public void moveDown(View view){
        userInput = "move 0 1";
    }
    public void moveDownLeft(View view){
        userInput = "move -1 1";
    }
    public void moveDownRight(View view){
        userInput = "move 1 1";
    }
    public void noop(View view){
        userInput = "noop";
    }
    public void firePowerUp(View view){
        userInput = "fire " + Angle((int) x,(int) y,(int) powerx,(int) powery);
    }
    public void scan(View view){
        userInput = "scan";
    }

    //find the distance to the target
    public double Distance (double x1, double y1, double x2, double y2){
        double d = Math.sqrt(Math.pow(x1 - x2,2) + Math.pow(y1 - y2,2));
        return d;
    }

    //find the angle to the target
    public int Angle(int x1, int y1, int x2, int y2) {
        float dx = (float) (x2-x1);
        float dy = (float) (y2-y1);
        double angle=0.0d;

        // Calculate angle
        if (dx == 0.0) {
            if (dy == 0.0)
                angle = 0.0;
            else if (dy > 0.0)
                angle = Math.PI / 2.0;
            else
                angle = Math.PI * 3.0 / 2.0;
        } else if (dy == 0.0) {
            if  (dx > 0.0)
                angle = 0.0;
            else
                angle = Math.PI;
        } else {
            if  (dx < 0.0)
                angle = Math.atan(dy/dx) + Math.PI;
            else if (dy < 0.0)
                angle = Math.atan(dy/dx) + (2*Math.PI);
            else
                angle = Math.atan(dy/dx);
        }

        // Convert to degrees
        angle = angle * 180 / Math.PI;

        // Return
        return (int) angle;
    }

    class doNetwork  implements Runnable {
        public PrintWriter out;
        public  BufferedReader in;
        String str;


        public void run() {


            int p = Integer.parseInt(port);
            String h = hostname;
            mkmsg("host is "+h +"\n");
            mkmsg(" Port is " +p + "\n");
            try {
                InetAddress serverAddr = InetAddress.getByName(h);
                mkmsg("Attempt Connecting..." + h +"\n");
                Socket socket = new Socket(serverAddr, p);
                String message = "Hello from Client android emulator";

                //made connection, setup the read (in) and write (out)
                out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //now send a message to the server and then read back the response.
                try {


                    //read back a message from the server.
                    mkmsg("Attempting to receive a message ...\n");
                    str = in.readLine();
                    mkmsg("received a message:\n" + str+"\n");

                    message = "EpicBot 0 1 4";  //send setup message back
                    out.println(message);
                    mkmsg("sent" + message + "\n");


                    while(true){

                        //mkmsg("about to read");
                        str = in.readLine();  //read in message
                         //mkmsg("working to here " + str + "\n");
                        //incoming(str);  //interpret message
                        if(userInput == ""){
                            out.println("noop");
                            userInput = "";
                        }
                        else {
                            out.println(userInput);
                            userInput = "";
                        }
                    }  //(status[4] != "0"); //as long as im not dead, keep getting and interpreting


                    //mkmsg("We are done, closing connection\n");
                } catch(Exception e) {
                    mkmsg("Error happened sending/receiving\n");

                } finally {
                    in.close();
                    out.close();
                    socket.close();
                }

            } catch (Exception e) {
                mkmsg("Unable to connect...\n");
            }
        }

        //handles the scan
        public void incoming(String str){
            if(str.contains("Status")){  //if it is a status message
                //mkmsg("STATUS DETECTED");
                status = str.split("");  //split it up
                x = Double.parseDouble(status[1]);  //parse the x position
                y = Double.parseDouble(status[2]);  //parse the y position
                mkmsg("status received\n");

            }
            else if(str.contains("bot")){  //if it is a bot
                String[] temp = str.split("");  //split it up
                double tempx = Double.parseDouble(temp[3]);  //parse detected enemies x position
                double tempy = Double.parseDouble(temp[4]);  //parse detected enemies y position
                double tempnum = Distance(tempx,tempy,x,y);  //find distance to detected enemy
                if( tempnum < enemyd){ //if detected enemy is closer, then replace the old one with the new
                    enemyd = tempnum;
                    enemyx = tempx;
                    enemyy = tempy;
                }
                mkmsg("bot detected\n");
            }
            else if(str.contains("powerup")){
                String[] temp = str.split("");
                powerx = Double.parseDouble(temp[3]);
                powery = Double.parseDouble(temp[4]);
                mkmsg("powerup detected");
            }
            else{
                mkmsg("found nothing\n");
                return;}

        }


        //select a move
        private String chooseMove(){
            String temp;

            //could use a switch statement after a split, but decided not to
            if(userInput != ""){
                if(userInput == "noop"){
                    temp = userInput;
                    userInput = "";
                    return temp;
                }
                else if(userInput.contains("move") && status[3] == "0"){
                    temp = userInput;
                    userInput = "";
                    return temp;
                }
                else if(userInput.contains("fire") && status[4] == "0"){
                    temp = userInput;
                    userInput = "";
                    return temp;
                }
                else if(userInput.contains("scan")){
                    userInput = "";
                    return "scan";
                }
                else if(status[4] == "0"){
                    return "fire " + Angle((int) x,(int) y,(int) enemyx,(int) enemyy);
                }
                else{
                    return "scan";
                }
            }
            else if(status[4] == "0"){
                return "fire " + Angle((int) x,(int) y,(int) enemyx,(int) enemyy);
            }
            else{
                return "scan";
            }

        } //end of choosemove

    }



}
