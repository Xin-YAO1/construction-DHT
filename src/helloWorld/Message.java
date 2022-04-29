package helloWorld;

import peersim.edsim.*;

public class Message {

    public final static int HELLOWORLD = 0;
    public final static int JOIN = 1;
    public final static int LEAVE = 2;
    public final static int BYE = 3;

    private int type;
    private String content;

    Message(int type, String content) {
	this.type = type;
	this.content = content;
    }

    public String getContent() {
	return this.content;
    }

    public int getType() {
	return this.type;
    }
    
}