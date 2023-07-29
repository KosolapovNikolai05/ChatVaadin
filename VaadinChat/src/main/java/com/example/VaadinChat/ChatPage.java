package com.example.VaadinChat;

import com.github.rjeschke.txtmark.Processor;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;


@Route("")
@Push
public class ChatPage extends VerticalLayout implements AppShellConfigurator {
    private final MessageStorage messagesStorage;
    private Registration registration;
    private Grid<Message> messagesGrid;

    private VerticalLayout login , chat;

    private String username;

    @Autowired
    public ChatPage(MessageStorage messagesStorage) {
        this.messagesStorage = messagesStorage;
        buildChat();
        buildLogin();
    }


    public void onMessage(ChatEvent event){
        if(getUI().isPresent()){
            UI ui = getUI().get();
            ui.getSession().lock();
            ui.access(()->messagesGrid.getDataProvider().refreshAll());
            ui.getPage().executeJs("$0.scrollToIndex($1)" , messagesGrid , messagesStorage.getSize());
            ui.getSession().unlock();
        }
    }
    public String renderRow (Message message) {
        return Processor.process(String.format("**%s**: %s" , message.getUsername() , message.getContent()));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        registration = messagesStorage.attachListener(this::onMessage);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        registration.remove();
    }

    public void buildChat(){
        chat = new VerticalLayout();
        add(chat);
        chat.setVisible(false);
        messagesGrid = new Grid<>();
        messagesGrid.setItems(messagesStorage.getMessageQueue());
        messagesGrid.addColumn(new ComponentRenderer<>(message -> new Html(renderRow(message)))).setAutoWidth(true);
        TextField inputMessage = new TextField();
        inputMessage.setWidth("400px");
        chat.add(messagesGrid ,
                new HorizontalLayout() {{add(inputMessage,
                        new Button("✈"){{
                            addClickListener(buttonClickEvent -> {
                                messagesStorage.addMessage(username , inputMessage.getValue());
                                inputMessage.clear();
                            });
                            addClickShortcut(Key.ENTER);
                        }}
                );
                }}

        );
    }

    private void buildLogin() {
        login = new VerticalLayout() {{
            TextField inputUsername = new TextField();
            inputUsername.setPlaceholder("Username");
            add(
                    inputUsername,
                    new Button("Join") {{
                        addClickListener(click -> {
                            login.setVisible(false);
                            chat.setVisible(true);
                            username = inputUsername.getValue();
                            addClickShortcut(Key.ENTER);
                            Notification notification = Notification
                                    .show(username + ", welcome!");
                            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        });
                    }}
            );
        }};
        add(login);
    }
}
