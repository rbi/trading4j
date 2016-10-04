package de.voidnode.trading4j.server.protocol.messages;

/**
 * Indicates that no more messages will be send to the client in response to the last incoming messages.
 * 
 * <p>
 * With this message the control flow is returned to the client.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class EventHandlingFinishedMessage implements Message {

}
