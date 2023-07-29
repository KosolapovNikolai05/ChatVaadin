package com.example.VaadinChat;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.html.Div;

public class ChatEvent extends ComponentEvent<Div> {

    public ChatEvent() {
        super(new Div(), false);
    }
}
