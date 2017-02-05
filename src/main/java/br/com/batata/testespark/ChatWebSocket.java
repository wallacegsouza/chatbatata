package br.com.batata.testespark;


import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author r0g0
 */

@WebSocket
public class ChatWebSocket {
    
    static final Logger logger =  Logger.getLogger(ChatWebSocket.class.getName());
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    @OnWebSocketConnect
    public void connected(Session session) {
        sessions.add(session);
        try {
            //TODO criar uma classa para tratar as messagens
            session.getRemote().sendString(
                "<div class='msg' style='color: rgba(80, 80, 80, 1);" +
                "border-color: rgba(125, 125, 125, 1);'>"
                + "Numero de usuarios no chat : " + sessions.size() + "</div>" );
        } catch (IOException ex) { }
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, final String message) throws IOException {

        logger.log(Level.INFO, "Got: {0}", message);

        sessions.stream().filter(Session::isOpen).forEach( (Session c) -> {
            try {
                c.getRemote().sendString(message);
            } catch (IOException ex) {
               logger.log(Level.SEVERE, "Error no envio de msg.", ex);
            }
        });
    }
}
