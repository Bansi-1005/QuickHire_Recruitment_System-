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
@Table(name = "tblapplicationstatushistory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblapplicationstatushistory.findAll", query = "SELECT t FROM Tblapplicationstatushistory t"),
    @NamedQuery(name = "Tblapplicationstatushistory.findByStatusHistoryId", query = "SELECT t FROM Tblapplicationstatushistory t WHERE t.statusHistoryId = :statusHistoryId"),
    @NamedQuery(name = "Tblapplicationstatushistory.findByOldStatus", query = "SELECT t FROM Tblapplicationstatushistory t WHERE t.oldStatus = :oldStatus"),
    @NamedQuery(name = "Tblapplicationstatushistory.findByNewStatus", query = "SELECT t FROM Tblapplicationstatushistory t WHERE t.newStatus = :newStatus"),
    @NamedQuery(name = "Tblapplicationstatushistory.findByStatusUpdatedDate", query = "SELECT t FROM Tblapplicationstatushistory t WHERE t.statusUpdatedDate = :statusUpdatedDate")})
public class Tblapplicationstatushistory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "statusHistoryId")
    private Integer statusHistoryId;
    @Size(max = 50)
    @Column(name = "oldStatus")
    private String oldStatus;
    @Size(max = 50)
    @Column(name = "newStatus")
    private String newStatus;
    @Basic(optional = false)
    @NotNull
    @Column(name = "statusUpdatedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusUpdatedDate;
    @JoinColumn(name = "applicationId", referencedColumnName = "applicationId")
    @ManyToOne
    private Tblapplication applicationId;

    public Tblapplicationstatushistory() {
    }

    public Tblapplicationstatushistory(Integer statusHistoryId) {
        this.statusHistoryId = statusHistoryId;
    }

    public Tblapplicationstatushistory(Integer statusHistoryId, Date statusUpdatedDate) {
        this.statusHistoryId = statusHistoryId;
        this.statusUpdatedDate = statusUpdatedDate;
    }

    public Integer getStatusHistoryId() {
        return statusHistoryId;
    }

    public void setStatusHistoryId(Integer statusHistoryId) {
        this.statusHistoryId = statusHistoryId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public Date getStatusUpdatedDate() {
        return statusUpdatedDate;
    }

    public void setStatusUpdatedDate(Date statusUpdatedDate) {
        this.statusUpdatedDate = statusUpdatedDate;
    }

    public Tblapplication getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Tblapplication applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (statusHistoryId != null ? statusHistoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblapplicationstatushistory)) {
            return false;
        }
        Tblapplicationstatushistory other = (Tblapplicationstatushistory) object;
        if ((this.statusHistoryId == null && other.statusHistoryId != null) || (this.statusHistoryId != null && !this.statusHistoryId.equals(other.statusHistoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblapplicationstatushistory[ statusHistoryId=" + statusHistoryId + " ]";
    }
    
}
