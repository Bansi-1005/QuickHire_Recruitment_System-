/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import Entity.Tbljob;
import Entity.Tblskills;
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
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author RINKAL
 */
@Entity
@Table(name = "tbljobskillweightage")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tbljobskillweightage.findAll", query = "SELECT t FROM Tbljobskillweightage t"),
    @NamedQuery(name = "Tbljobskillweightage.findByJobSkillWeightageId", query = "SELECT t FROM Tbljobskillweightage t WHERE t.jobSkillWeightageId = :jobSkillWeightageId"),
    @NamedQuery(name = "Tbljobskillweightage.findBySkillWeightage", query = "SELECT t FROM Tbljobskillweightage t WHERE t.skillWeightage = :skillWeightage")})
public class Tbljobskillweightage implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "jobSkillWeightageId")
    private Integer jobSkillWeightageId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "skillWeightage")
    private int skillWeightage;
    @JoinColumn(name = "jobId", referencedColumnName = "jobId")
    @ManyToOne(optional = false)
    private Tbljob jobId;
    @JoinColumn(name = "skillId", referencedColumnName = "skillId")
    @ManyToOne(optional = false)
    private Tblskills skillId;

    public Tbljobskillweightage() {
    }

    public Tbljobskillweightage(Integer jobSkillWeightageId) {
        this.jobSkillWeightageId = jobSkillWeightageId;
    }

    public Tbljobskillweightage(Integer jobSkillWeightageId, int skillWeightage) {
        this.jobSkillWeightageId = jobSkillWeightageId;
        this.skillWeightage = skillWeightage;
    }

    public Integer getJobSkillWeightageId() {
        return jobSkillWeightageId;
    }

    public void setJobSkillWeightageId(Integer jobSkillWeightageId) {
        this.jobSkillWeightageId = jobSkillWeightageId;
    }

    public int getSkillWeightage() {
        return skillWeightage;
    }

    public void setSkillWeightage(int skillWeightage) {
        this.skillWeightage = skillWeightage;
    }

    public Tbljob getJobId() {
        return jobId;
    }

    public void setJobId(Tbljob jobId) {
        this.jobId = jobId;
    }

    public Tblskills getSkillId() {
        return skillId;
    }

    public void setSkillId(Tblskills skillId) {
        this.skillId = skillId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (jobSkillWeightageId != null ? jobSkillWeightageId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tbljobskillweightage)) {
            return false;
        }
        Tbljobskillweightage other = (Tbljobskillweightage) object;
        if ((this.jobSkillWeightageId == null && other.jobSkillWeightageId != null) || (this.jobSkillWeightageId != null && !this.jobSkillWeightageId.equals(other.jobSkillWeightageId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CDIBean.Tbljobskillweightage[ jobSkillWeightageId=" + jobSkillWeightageId + " ]";
    }
    
}
