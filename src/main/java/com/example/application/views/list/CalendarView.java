package com.example.application.views.list;

import com.example.application.data.event.Event;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Route(value = "calendar", layout = MainLayout.class)
@PageTitle("Calendar")
@CssImport("./themes/my-app/styles.css")
@PermitAll
public class CalendarView extends VerticalLayout {

    private List<Event> events = new ArrayList<>();
    private Grid<LocalDate> calendarGrid = new Grid<>();
    private VerticalLayout calendarLayout = new VerticalLayout();
    private Tabs viewTypeTabs;
    private String currentView = "week"; // Default view
    private LocalDate currentDate = LocalDate.now();

    public CalendarView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Create sample events
        events.add(new Event("Meeting with Team", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0)));
        events.add(new Event("Lunch Break", LocalDate.now().plusDays(1), LocalTime.of(12, 0), LocalTime.of(13, 0)));

        // Create view mode selector
        createViewSelector();

        // Initialize calendar
        initializeCalendar();
    }

    private void createViewSelector() {
        Tab dayTab = new Tab("Day");
        Tab weekTab = new Tab("Week");
        Tab monthTab = new Tab("Month");

        viewTypeTabs = new Tabs(dayTab, weekTab, monthTab);
        viewTypeTabs.setSelectedTab(weekTab); // Default to week view

        Button previousButton = new Button("Previous", e -> {
            if ("day".equals(currentView)) {
                currentDate = currentDate.minusDays(1);
            } else if ("week".equals(currentView)) {
                currentDate = currentDate.minusWeeks(1);
            } else if ("month".equals(currentView)) {
                currentDate = currentDate.minusMonths(1);
            }
            initializeCalendar();
        });

        Button todayButton = new Button("Today", e -> {
            currentDate = LocalDate.now();
            initializeCalendar();
        });

        Button nextButton = new Button("Next", e -> {
            if ("day".equals(currentView)) {
                currentDate = currentDate.plusDays(1);
            } else if ("week".equals(currentView)) {
                currentDate = currentDate.plusWeeks(1);
            } else if ("month".equals(currentView)) {
                currentDate = currentDate.plusMonths(1);
            }
            initializeCalendar();
        });

        HorizontalLayout navigationLayout = new HorizontalLayout(previousButton, todayButton, nextButton);
        navigationLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout toolbarLayout = new HorizontalLayout(navigationLayout, viewTypeTabs);
        toolbarLayout.setWidthFull();
        toolbarLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        viewTypeTabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            if (selectedTab.equals(dayTab)) {
                currentView = "day";
            } else if (selectedTab.equals(weekTab)) {
                currentView = "week";
            } else if (selectedTab.equals(monthTab)) {
                currentView = "month";
            }
            initializeCalendar();
        });

        add(toolbarLayout);
    }

    private void initializeCalendar() {
        // Remove previous calendar if exists
        if (calendarLayout.getParent().isPresent()) {
            remove(calendarLayout);
        }

        calendarLayout = new VerticalLayout();
        calendarLayout.setSizeFull();
        calendarLayout.setPadding(false);
        calendarLayout.setSpacing(false);

        // Create the appropriate view
        if ("day".equals(currentView)) {
            createDayView();
        } else if ("week".equals(currentView)) {
            createWeekView();
        } else if ("month".equals(currentView)) {
            createMonthView();
        }

        add(calendarLayout);
    }

    private void createDayView() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        Div header = new Div();
        header.setText(currentDate.format(formatter));
        header.addClassName(LumoUtility.FontSize.LARGE);
        header.addClassName(LumoUtility.Padding.MEDIUM);

        Grid<LocalTime> dayGrid = new Grid<>();
        dayGrid.setHeight("600px");
        dayGrid.addClassName("responsive-grid");

        List<LocalTime> hours = new ArrayList<>();
        for (int hour = 8; hour <= 18; hour++) {
            hours.add(LocalTime.of(hour, 0));
        }

        dayGrid.setItems(hours);

        dayGrid.addColumn(time -> time.format(DateTimeFormatter.ofPattern("HH:mm")))
                .setHeader("Time")
                .setFlexGrow(0)
                .setWidth("100px")
                .setFrozen(true);

        dayGrid.addColumn(new ComponentRenderer<>(time -> {
            Div cell = new Div();
            cell.setWidthFull();
            cell.setHeight("60px");

            boolean hasEvent = false;
            for (Event event : events) {
                if (event.getDate().equals(currentDate) &&
                        (time.equals(event.getStartTime()) ||
                                (time.isAfter(event.getStartTime()) && time.isBefore(event.getEndTime())))) {
                    cell.setText(event.getTitle());
                    cell.getStyle().set("background-color", "var(--lumo-primary-color-10pct)");
                    cell.getStyle().set("border-left", "3px solid var(--lumo-primary-color)");
                    cell.getStyle().set("padding", "5px");
                    hasEvent = true;
                    break;
                }
            }

            if (!hasEvent) {
                cell.getStyle().set("cursor", "pointer");
                cell.addClickListener(click -> {
                    showEventDialog(currentDate, time);
                });
            }

            return cell;
        })).setHeader("Events").setFlexGrow(1);

        calendarLayout.add(header, dayGrid);
    }

    private void createWeekView() {
        LocalDate startOfWeek = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1);

        Grid<LocalTime> weekGrid = new Grid<>();
        weekGrid.setHeight("600px");
        weekGrid.addClassName("responsive-grid");

        List<LocalTime> hours = new ArrayList<>();
        for (int hour = 8; hour <= 18; hour++) {
            hours.add(LocalTime.of(hour, 0));
        }

        weekGrid.setItems(hours);

        weekGrid.addColumn(time -> time.format(DateTimeFormatter.ofPattern("HH:mm")))
                .setHeader("Time")
                .setFlexGrow(0)
                .setWidth("80px")
                .setFrozen(true);

        // Create columns for each day of the week
        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            final int dayIndex = i;

            weekGrid.addColumn(new ComponentRenderer<>(time -> {
                        Div cell = new Div();
                        cell.setWidthFull();
                        cell.setHeight("60px");

                        boolean hasEvent = false;
                        for (Event event : events) {
                            if (event.getDate().equals(day) &&
                                    (time.equals(event.getStartTime()) ||
                                            (time.isAfter(event.getStartTime()) && time.isBefore(event.getEndTime())))) {
                                cell.setText(event.getTitle());
                                cell.getStyle().set("background-color", "var(--lumo-primary-color-10pct)");
                                cell.getStyle().set("border-left", "3px solid var(--lumo-primary-color)");
                                cell.getStyle().set("padding", "5px");
                                hasEvent = true;
                                break;
                            }
                        }

                        if (!hasEvent) {
                            cell.getStyle().set("cursor", "pointer");
                            cell.addClickListener(click -> {
                                showEventDialog(day, time);
                            });
                        }

                        return cell;
                    }))
                    .setHeader(day.getDayOfWeek().toString() + " " + day.format(DateTimeFormatter.ofPattern("M/d")))
                    .setKey("day-" + i)
                    .setFlexGrow(1);
        }

        calendarLayout.add(weekGrid);
    }

    private void createMonthView() {
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        int daysInMonth = currentDate.lengthOfMonth();

        Div monthHeader = new Div();
        monthHeader.setText(currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        monthHeader.addClassName(LumoUtility.FontSize.LARGE);
        monthHeader.addClassName(LumoUtility.Padding.MEDIUM);

        Grid<Integer> monthGrid = new Grid<>();
        monthGrid.setHeight("600px");
        monthGrid.addClassName("responsive-grid");
        monthGrid.addClassName("month-grid");

        // Create a list of day numbers
        List<Integer> days = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            days.add(i);
        }

        // Add extra days to fill the grid nicely
        int startDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
        for (int i = 1; i < startDayOfWeek; i++) {
            days.add(0, -i); // Add days from previous month
        }

        // Make sure we have complete weeks (7 days per row)
        while (days.size() % 7 != 0) {
            days.add(days.size() + 1); // Add days from next month
        }

        monthGrid.setItems(days);

        // Create day header columns
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < 7; i++) {
            final int dayIndex = i;
            monthGrid.addColumn(new ComponentRenderer<>(day -> {
                        if ((days.indexOf(day) % 7) != dayIndex) {
                            return new Div(); // Empty for non-matching days
                        }

                        LocalDate date;
                        if (day <= 0) {
                            // Previous month
                            date = firstDayOfMonth.minusDays(-day);
                        } else if (day > daysInMonth) {
                            // Next month
                            date = firstDayOfMonth.plusMonths(1).withDayOfMonth(day - daysInMonth);
                        } else {
                            // Current month
                            date = firstDayOfMonth.withDayOfMonth(day);
                        }

                        VerticalLayout cellLayout = new VerticalLayout();
                        cellLayout.setPadding(false);
                        cellLayout.setSpacing(false);
                        cellLayout.setHeight("100px");

                        Div dayNumber = new Div();
                        dayNumber.setText(String.valueOf(Math.abs(day)));

                        if (day <= 0 || day > daysInMonth) {
                            dayNumber.getStyle().set("color", "var(--lumo-tertiary-text-color)");
                        }

                        if (date.equals(LocalDate.now())) {
                            dayNumber.getStyle().set("background-color", "var(--lumo-primary-color)");
                            dayNumber.getStyle().set("color", "white");
                            dayNumber.getStyle().set("border-radius", "50%");
                            dayNumber.getStyle().set("width", "25px");
                            dayNumber.getStyle().set("height", "25px");
                            dayNumber.getStyle().set("display", "flex");
                            dayNumber.getStyle().set("align-items", "center");
                            dayNumber.getStyle().set("justify-content", "center");
                        }

                        cellLayout.add(dayNumber);

                        // Add events for this day
                        for (Event event : events) {
                            if (event.getDate().equals(date)) {
                                Div eventDiv = new Div();
                                eventDiv.setText(event.getTitle());
                                eventDiv.getStyle().set("background-color", "var(--lumo-primary-color-10pct)");
                                eventDiv.getStyle().set("border-left", "3px solid var(--lumo-primary-color)");
                                eventDiv.getStyle().set("padding", "2px 5px");
                                eventDiv.getStyle().set("margin-top", "2px");
                                eventDiv.getStyle().set("font-size", "small");
                                eventDiv.getStyle().set("overflow", "hidden");
                                eventDiv.getStyle().set("text-overflow", "ellipsis");
                                eventDiv.getStyle().set("white-space", "nowrap");

                                cellLayout.add(eventDiv);
                            }
                        }

                        // Add click listener to create new events
                        cellLayout.addClickListener(click -> {
                            showEventDialog(date, LocalTime.of(9, 0));
                        });

                        return cellLayout;
                    }))
                    .setHeader(dayNames[i])
                    .setFlexGrow(1);
        }

        calendarLayout.add(monthHeader, monthGrid);
    }

    private void showEventDialog(LocalDate date, LocalTime time) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        TextField titleField = new TextField("Event Title");
        titleField.setWidthFull();

        Button saveButton = new Button("Save", event -> {
            if (!titleField.getValue().isEmpty()) {
                events.add(new Event(titleField.getValue(), date, time, time.plusHours(1)));
                Notification.show("Event added: " + titleField.getValue());
                initializeCalendar(); // Refresh to show the new event
            }
            dialog.close();
        });

        Button cancelButton = new Button("Cancel", event -> {
            dialog.close();
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Span dateTimeText = new Span(date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")) +
                " at " + time.format(DateTimeFormatter.ofPattern("HH:mm")));

        VerticalLayout dialogLayout = new VerticalLayout(
                new Div(dateTimeText),
                titleField,
                buttonLayout
        );

        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);

        dialog.add(dialogLayout);
        dialog.open();
    }
}