package xyz.mb;

public interface ConnectionEventListener {

    void onConnect(Connection tcpConnection);

    void onDisconnect(Connection tcpConnection);

    void onException(Connection tcpConnection, Exception e);

}
