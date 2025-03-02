package com.example.application.views.list;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "home", layout = MainLayout.class)
@PageTitle("Home")
@PermitAll
public class HomeView extends VerticalLayout {

    public HomeView() {
        addClassName("home-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        H1 mainMessage = new H1("Welcome to the main page");
        mainMessage.addClassNames("home-view h1");

        H2 subliminalMessage = new H2("This is a subliminal message");
        subliminalMessage.addClassNames("home-view h2");

        add(mainMessage, subliminalMessage);
    }
}
