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
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author tejan
 */
@Entity
@Table(name = "tblresume")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblresume.findAll", query = "SELECT t FROM Tblresume t"),
    @NamedQuery(name = "Tblresume.findByResumeId", query = "SELECT t FROM Tblresume t WHERE t.resumeId = :resumeId"),
    @NamedQuery(name = "Tblresume.findByResumeFile", query = "SELECT t FROM Tblresume t WHERE t.resumeFile = :resumeFile"),
    @NamedQuery(name = "Tblresume.findByUploadDate", query = "SELECT t FROM Tblresume t WHERE t.uploadDate = :uploadDate"),
    @NamedQuery(name = "Tblresume.findByIsActive", query = "SELECT t FROM Tblresume t WHERE t.isActive = :isActive")})
public class Tblresume implements Serializable {

    @Size(max = 255)
    @Column(name = "resumeFile")
    private String resumeFile;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "resumeId")
    private Integer resumeId;
    @Column(name = "uploadDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;
    @Column(name = "isActive")
    private Boolean isActive;
    @JoinColumn(name = "candidateId", referencedColumnName = "candidateId")
    @ManyToOne
    private Tblcandidates candidateId;

    public Tblresume() {
    }

    public Tblresume(Integer resumeId) {
        this.resumeId = resumeId;
    }

    public Integer getResumeId() {
        return resumeId;
    }

    public void setResumeId(Integer resumeId) {
        this.resumeId = resumeId;
    }


    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Tblcandidates getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Tblcandidates candidateId) {
        this.candidateId = candidateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (resumeId != null ? resumeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblresume)) {
            return false;
        }
        Tblresume other = (Tblresume) object;
        if ((this.resumeId == null && other.resumeId != null) || (this.resumeId != null && !this.resumeId.equals(other.resumeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblresume[ resumeId=" + resumeId + " ]";
    }

    public String getResumeFile() {
        return resumeFile;
    }

    public void setResumeFile(String resumeFile) {
        this.resumeFile = resumeFile;
    }
    
}
