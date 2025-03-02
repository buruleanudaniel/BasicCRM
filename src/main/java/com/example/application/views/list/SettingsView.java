package com.example.application.views.list;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "settings", layout = MainLayout.class)
@PageTitle("Settings")
@PermitAll
public class SettingsView extends VerticalLayout {

    public SettingsView() {
        addClassName("settings-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        // Placeholder content
        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        Button saveButton = new Button("Save");

        add(usernameField, passwordField, saveButton);
    }
}