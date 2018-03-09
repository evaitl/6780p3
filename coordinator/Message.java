package coordinator;

class Message{
    static int currentMsgNum;
    long timeReceived;
    int msgNum;
    String msg;
    Message(String msg_){
	msgNum=++currentMsgNum;
	msg=msg_;
	timeReceived=System.currentTimeMillis()/1000;
    }
}
