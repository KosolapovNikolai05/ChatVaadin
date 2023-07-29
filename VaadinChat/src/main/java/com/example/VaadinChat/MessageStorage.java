package com.example.VaadinChat;

import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Getter
public class MessageStorage {
    private final Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    private final ComponentEventBus eventBus = new ComponentEventBus(new Div());
    public void addMessage(String name , String content){
        messageQueue.add(new Message(name , content));
        eventBus.fireEvent(new ChatEvent());
    }

    public int getSize() {
        return messageQueue.size();
    }
    public Registration attachListener(ComponentEventListener<ChatEvent> messageListener) {
        return eventBus.addListener(ChatEvent.class , messageListener);
    }
}
