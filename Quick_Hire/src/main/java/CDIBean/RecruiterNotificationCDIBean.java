/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package CDIBean;

import Client.RecruiterJerseyClient;
import Entity.Tblnotification;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author RINKAL
 */
@Named(value = "recruiterNotificationCDIBean")
@ViewScoped
public class RecruiterNotificationCDIBean implements Serializable {

    /**
     * Creates a new instance of recruiterNotificationCDIBean
     */
   
    private List<Tblnotification> notifications = new ArrayList<>();
    private final RecruiterJerseyClient client = new RecruiterJerseyClient();

    @Inject
    LoginCDIBean loginBean;

    @PostConstruct
    public void init() {
        loadNotifications();
    }

    public void loadNotifications() {
        try {
            client.setToken(loginBean.getToken());
            Collection<Tblnotification> data = client.getNotifications(
                    new GenericType<Collection<Tblnotification>>() {},
                    String.valueOf(loginBean.getUserId())
            );
            notifications = data != null ? new ArrayList<>(data) : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            notifications = new ArrayList<>();
            addError("Unable to load notifications.");
        }
    }

    public void markAsRead(Integer notificationId) {
        try {
            if (notificationId == null || notificationId <= 0) {
                addError("Invalid notification.");
                return;
            }

            client.setToken(loginBean.getToken());
            Response response = client.markNotificationRead(notificationId, loginBean.getUserId());

            if (response.getStatus() == 200) {
                addInfo("Notification marked as read.");
                loadNotifications();
            } else {
                addError(response.readEntity(String.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            addError("Unable to mark notification as read.");
        }
    }

    public void markAllAsRead() {
        try {
            client.setToken(loginBean.getToken());
            Response response = client.markAllNotificationsRead(loginBean.getUserId());

            if (response.getStatus() == 200) {
                addInfo("All notifications marked as read.");
                loadNotifications();
            } else {
                addError(response.readEntity(String.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            addError("Unable to mark all notifications as read.");
        }
    }

    public int getTotalCount() {
        return notifications != null ? notifications.size() : 0;
    }

    public int getUnreadCount() {
        int count = 0;
        for (Tblnotification item : notifications) {
            if (item != null && !Boolean.TRUE.equals(item.getIsRead())) {
                count++;
            }
        }
        return count;
    }

    public int getReadCount() {
        return getTotalCount() - getUnreadCount();
    }

    public int getRecentCount() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, -6);
        Date sevenDaysAgo = cal.getTime();

        int count = 0;
        for (Tblnotification item : notifications) {
            if (item != null && item.getCreatedDate() != null
                    && !item.getCreatedDate().before(sevenDaysAgo)) {
                count++;
            }
        }
        return count;
    }

    public List<String> getNotificationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Tblnotification item : notifications) {
            if (item != null && item.getNotificationType() != null
                    && !item.getNotificationType().trim().isEmpty()) {
                types.add(item.getNotificationType().trim());
            }
        }
        return new ArrayList<>(types);
    }

    public String formatDisplayDate(Date date) {
        return date == null ? "" : new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(date);
    }

    public String formatIsoDate(Date date) {
        return date == null ? "" : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date);
    }

    public String formatNotificationType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return "General";
        }
        String text = type.trim().replace('_', ' ').toLowerCase(Locale.ENGLISH);
        StringBuilder result = new StringBuilder();
        for (String part : text.split("\\s+")) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1))
                        .append(' ');
            }
        }
        return result.toString().trim();
    }

    public String getTypeInitial(String type) {
        String formatted = formatNotificationType(type);
        return formatted.isEmpty() ? "N" : formatted.substring(0, 1).toUpperCase();
    }

    public String getTypeClass(String type) {
        String value = type == null ? "" : type.toLowerCase(Locale.ENGLISH);
        if (value.contains("application")) return "notification-icon-application";
        if (value.contains("interview")) return "notification-icon-interview";
        if (value.contains("job")) return "notification-icon-job";
        if (value.contains("system")) return "notification-icon-system";
        return "notification-icon-default";
    }

    private void addInfo(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", message));
    }

    private void addError(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    public List<Tblnotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Tblnotification> notifications) {
        this.notifications = notifications;
    }
}
    
