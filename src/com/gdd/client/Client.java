package com.gdd.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	public static void main(String[] args) throws Exception {  
        Socket socket = new Socket("localhost", 9000);  
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
        PrintWriter out = new PrintWriter(socket.getOutputStream());  
 //       BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));  
       // out.println("create;dd;dd;dd");  
        out.println("check;dddd;ddd;ddd");
        out.flush();  
        System.out.println(in.readLine()); 
        socket.close();  
    }  
}
