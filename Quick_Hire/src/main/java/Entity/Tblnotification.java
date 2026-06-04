/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tejan
 */
@Entity
@Table(name = "tblnotification")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblnotification.findAll", query = "SELECT t FROM Tblnotification t"),
    @NamedQuery(name = "Tblnotification.findByNotificationId", query = "SELECT t FROM Tblnotification t WHERE t.notificationId = :notificationId"),
    @NamedQuery(name = "Tblnotification.findByCreatedDate", query = "SELECT t FROM Tblnotification t WHERE t.createdDate = :createdDate"),
    
    @NamedQuery(name = "Tblnotification.findByReceiver", query = "SELECT t FROM Tblnotification t WHERE t.receiverUserId.userId = :userId ORDER BY t.createdDate DESC"),
    @NamedQuery(name = "Tblnotification.findUnreadByReceiver", query = "SELECT t FROM Tblnotification t WHERE t.receiverUserId.userId = :userId AND t.isRead = false ORDER BY t.createdDate DESC")
})
public class Tblnotification implements Serializable {

    @Size(max = 200)
    @Column(name = "notificationTitle")
    private String notificationTitle;
    @Lob
    @Size(max = 65535)
    @Column(name = "notificationMessage")
    private String notificationMessage;
    @Size(max = 50)
    @Column(name = "notificationType")
    private String notificationType;
    @Column(name = "isRead")
    private Boolean isRead;
    @Column(name = "readDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date readDate;
    @JoinColumn(name = "senderUserId", referencedColumnName = "userId")
    @ManyToOne
    private Tblusers senderUserId;
    @JoinColumn(name = "receiverUserId", referencedColumnName = "userId")
    @ManyToOne(optional = false)
    private Tblusers receiverUserId;

    @Column(name = "createdDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "notificationId")
    private Integer notificationId;


    public Tblnotification() {
    }

    public Tblnotification(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public Tblnotification(Integer notificationId, Date createdDate) {
        this.notificationId = notificationId;
        this.createdDate = createdDate;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (notificationId != null ? notificationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblnotification)) {
            return false;
        }
        Tblnotification other = (Tblnotification) object;
        if ((this.notificationId == null && other.notificationId != null) || (this.notificationId != null && !this.notificationId.equals(other.notificationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblnotification[ notificationId=" + notificationId + " ]";
    }


    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }


    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public Tblusers getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Tblusers senderUserId) {
        this.senderUserId = senderUserId;
    }

    public Tblusers getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Tblusers receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
    
}
