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
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author tejan
 */
@Entity
@Table(name = "tblscreeningscore")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblscreeningscore.findAll", query = "SELECT t FROM Tblscreeningscore t"),
    @NamedQuery(name = "Tblscreeningscore.findByScoreId", query = "SELECT t FROM Tblscreeningscore t WHERE t.scoreId = :scoreId"),
    @NamedQuery(name = "Tblscreeningscore.findByMatchingScore", query = "SELECT t FROM Tblscreeningscore t WHERE t.matchingScore = :matchingScore"),
    @NamedQuery(name = "Tblscreeningscore.findByScreeningLevel", query = "SELECT t FROM Tblscreeningscore t WHERE t.screeningLevel = :screeningLevel"),
    @NamedQuery(name = "Tblscreeningscore.findByRemarks", query = "SELECT t FROM Tblscreeningscore t WHERE t.remarks = :remarks"),
    @NamedQuery(name = "Tblscreeningscore.findByScoreDate", query = "SELECT t FROM Tblscreeningscore t WHERE t.scoreDate = :scoreDate"),
    
    @NamedQuery(name = "Tblscreeningscore.findByApplication", query = "SELECT t FROM Tblscreeningscore t WHERE t.applicationId.applicationId = :applicationId"),
    })
public class Tblscreeningscore implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "scoreId")
    private Integer scoreId;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "matchingScore")
    private BigDecimal matchingScore;
    @Size(max = 50)
    @Column(name = "screeningLevel")
    private String screeningLevel;
    @Size(max = 100)
    @Column(name = "remarks")
    private String remarks;
    @Basic(optional = false)
    @NotNull
    @Column(name = "scoreDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date scoreDate;
    @JoinColumn(name = "applicationId", referencedColumnName = "applicationId")
    @ManyToOne
    private Tblapplication applicationId;

    public Tblscreeningscore() {
    }

    public Tblscreeningscore(Integer scoreId) {
        this.scoreId = scoreId;
    }

    public Tblscreeningscore(Integer scoreId, Date scoreDate) {
        this.scoreId = scoreId;
        this.scoreDate = scoreDate;
    }

    public Integer getScoreId() {
        return scoreId;
    }

    public void setScoreId(Integer scoreId) {
        this.scoreId = scoreId;
    }

    public BigDecimal getMatchingScore() {
        return matchingScore;
    }

    public void setMatchingScore(BigDecimal matchingScore) {
        this.matchingScore = matchingScore;
    }

    public String getScreeningLevel() {
        return screeningLevel;
    }

    public void setScreeningLevel(String screeningLevel) {
        this.screeningLevel = screeningLevel;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getScoreDate() {
        return scoreDate;
    }

    public void setScoreDate(Date scoreDate) {
        this.scoreDate = scoreDate;
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
        hash += (scoreId != null ? scoreId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblscreeningscore)) {
            return false;
        }
        Tblscreeningscore other = (Tblscreeningscore) object;
        if ((this.scoreId == null && other.scoreId != null) || (this.scoreId != null && !this.scoreId.equals(other.scoreId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblscreeningscore[ scoreId=" + scoreId + " ]";
    }
    
}
