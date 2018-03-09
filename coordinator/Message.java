package coordinator;

class Message{
    static int nextMsgNum;
    long timeReceived;
    int msgNum;
    String msg;
    Message(String msg_){
	msgNum=++nextMsgNum;
	msg=msg_;
	timeReceived=System.currentTimeMillis()/1000;
    }
}
