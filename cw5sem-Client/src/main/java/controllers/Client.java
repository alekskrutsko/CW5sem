package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import serverMSG.ServerMSG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerMSG sendMessage(ServerMSG msg) {
        String answerString = new String();
        ServerMSG answer = new ServerMSG();
        try {
            out.println(fromServerCommandToString(msg));

            answerString = in.readLine();
            answer = fromStringToServerCommand(answerString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ServerMSG fromStringToServerCommand(String json){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        ServerMSG serverMSG = gson.fromJson(json, ServerMSG.class);
        return serverMSG;
    }

    private String fromServerCommandToString(ServerMSG serverMSG){
        Gson gson = new Gson();
        String json = gson.toJson(serverMSG);
        System.out.println(json);
        return json;
    }
}