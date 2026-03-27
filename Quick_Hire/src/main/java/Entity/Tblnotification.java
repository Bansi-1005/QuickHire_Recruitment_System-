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
    @NamedQuery(name = "Tblnotification.findByMessage", query = "SELECT t FROM Tblnotification t WHERE t.message = :message"),
    @NamedQuery(name = "Tblnotification.findByCreatedDate", query = "SELECT t FROM Tblnotification t WHERE t.createdDate = :createdDate"),
    @NamedQuery(name = "Tblnotification.findByNotificationStatus", query = "SELECT t FROM Tblnotification t WHERE t.notificationStatus = :notificationStatus")})
public class Tblnotification implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "notificationId")
    private Integer notificationId;
    @Size(max = 500)
    @Column(name = "message")
    private String message;
    @Basic(optional = false)
    @NotNull
    @Column(name = "createdDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Size(max = 50)
    @Column(name = "notificationStatus")
    private String notificationStatus;
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    @ManyToOne
    private Tblusers userId;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public Tblusers getUserId() {
        return userId;
    }

    public void setUserId(Tblusers userId) {
        this.userId = userId;
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
    
}
