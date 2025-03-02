package com.example.application.views.list;

import com.example.application.data.event.Event;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Route(value = "calendar", layout = MainLayout.class)
@PageTitle("Calendar")
@PermitAll
public class CalendarView extends VerticalLayout {

    private List<Event> events = new ArrayList<>();
    private Grid<LocalDate> calendarGrid = new Grid<>();

    public CalendarView() {
        initializeCalendar();

        events.add(new Event("Meeting with Team", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0)));
        events.add(new Event("Lunch Break", LocalDate.now().plusDays(1), LocalTime.of(12, 0), LocalTime.of(13, 0)));

        populateCalendar();
    }

    private void initializeCalendar() {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            dates.add(today.plusDays(i));
        }

        calendarGrid.setItems(dates);
        calendarGrid.addColumn(new ComponentRenderer<>(date -> {
            Div cell = new Div();
            cell.setText(date.getDayOfWeek().toString());
            return cell;
        })).setHeader("Day");

        for (int hour = 8; hour <= 18; hour++) {
            LocalTime time = LocalTime.of(hour, 0);
            calendarGrid.addColumn(new ComponentRenderer<>(date -> {
                Div cell = new Div();
                cell.setText("");
                for (Event event : events) {
                    if (event.getDate().equals(date) && event.getStartTime().isBefore(time) && event.getEndTime().isAfter(time)) {
                        cell.setText(event.getTitle());
                        break;
                    }
                }

                cell.addClickListener(click -> {
                    if (cell.getText().isEmpty()) {
                        String title = showEventDialog(date, time);
                        if (title != null && !title.isEmpty()) {
                            events.add(new Event(title, date, time, time.plusHours(1)));
                            populateCalendar();
                        }
                    }
                });

                return cell;
            })).setHeader(time.toString());
        }

        add(calendarGrid);
    }

    private String showEventDialog(LocalDate date, LocalTime time) {
        Dialog dialog = new Dialog();

        TextField titleField = new TextField("Event Title");
        Button saveButton = new Button("Save", event -> {
            dialog.close();
        });
        Button cancelButton = new Button("Cancel", event -> {
            dialog.close();
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        VerticalLayout dialogLayout = new VerticalLayout(titleField, buttonLayout);

        dialog.add(dialogLayout);
        dialog.open();

        saveButton.addClickListener(event -> {
            dialog.close();
            if (!titleField.getValue().isEmpty()) {
                Notification.show("Event added: " + titleField.getValue());
            }
        });

        dialog.addOpenedChangeListener(event -> {
            if (!event.isOpened() && titleField.getValue().isEmpty()) {
                //
            }
        });

        return titleField.getValue();
    }

    private void populateCalendar() {
        calendarGrid.getDataProvider().refreshAll();
    }
}