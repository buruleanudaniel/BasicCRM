package com.example.application.views.list;

import com.example.application.data.security.SecurityService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dashboard.Dashboard;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {
    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Daniel CRM");
        logo.addClassNames("text-l", "m-m");

        H2 quote = new H2("Quote of the day: I am gay");
        quote.addClassNames("text-c", "m-m");

        Button logOut = new Button("Log out", e -> securityService.logout());
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, quote, logOut);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink home = new RouterLink();
        Icon iconHome = new Icon(VaadinIcon.HOME);
        iconHome.getElement().getStyle().set("margin-right", "8px");
        home.add(iconHome, new Text("Home"));
        home.setRoute(HomeView.class);
        home.setHighlightCondition(HighlightConditions.sameLocation());
        home.addClassName("no-underline");

        RouterLink listView = new RouterLink();
        Icon icon = new Icon(VaadinIcon.LIST);
        icon.getElement().getStyle().set("margin-right", "8px");

        listView.add(icon, new Text("List"));
        listView.setRoute(ListView.class);
        listView.setHighlightCondition(HighlightConditions.sameLocation());
        listView.addClassName("no-underline");

        RouterLink dashboardLink = new RouterLink();
        Icon dashboardIcon = new Icon(VaadinIcon.DASHBOARD);
        dashboardIcon.getElement().getStyle().set("margin-right", "8px");

        dashboardLink.add(dashboardIcon, new Text("Dashboard"));
        dashboardLink.setRoute(DashboardView.class);
        dashboardLink.setHighlightCondition(HighlightConditions.sameLocation());
        dashboardLink.addClassName("no-underline");

        RouterLink settingsLink = new RouterLink();
        Icon settingsIcon = new Icon(VaadinIcon.COGS);
        settingsIcon.getElement().getStyle().set("margin-right", "8px");

        settingsLink.add(settingsIcon, new Text("Settings"));
        settingsLink.setRoute(SettingsView.class);
        settingsLink.addClassName("no-underline");

        RouterLink calendarLink = new RouterLink();
        Icon calendarIcon = new Icon(VaadinIcon.CALENDAR);
        calendarIcon.getElement().getStyle().set("margin-right", "8px");
        calendarLink.add(calendarIcon, new Text("Calendar"));
        calendarLink.setRoute(CalendarView.class);
        calendarLink.setHighlightCondition(HighlightConditions.sameLocation());
        calendarLink.addClassName("no-underline");

        addToDrawer(new VerticalLayout(
                home,
                listView,
                dashboardLink,
                calendarLink,
                settingsLink
        ));

    }
}
