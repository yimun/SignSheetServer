package com.gdd.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	public static void main(String[] args) throws Exception {  
        Socket socket = new Socket("localhost", 9000);  
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
        PrintWriter out = new PrintWriter(socket.getOutputStream());  
        
        OutputStream os = socket.getOutputStream();
 //       BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); 
        String str = "changemm;张林伟;755213;201281084;120115";
       // String str = "check;张林伟;201281084;755213";
        out.println(str);  
        //out.println("check;dddd;ddd;ddd");
        out.flush();  
        System.out.println(in.readLine()); 
        socket.close();  
    }  
}
