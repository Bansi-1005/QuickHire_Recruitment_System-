/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author tejan
 */
@Entity
@Table(name = "tblskills")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblskills.findAll", query = "SELECT t FROM Tblskills t"),
    @NamedQuery(name = "Tblskills.findBySkillId", query = "SELECT t FROM Tblskills t WHERE t.skillId = :skillId"),
    @NamedQuery(name = "Tblskills.findBySkillName", query = "SELECT t FROM Tblskills t WHERE t.skillName = :skillName"),
    @NamedQuery(name = "Tblskills.findBySkillCategory", query = "SELECT t FROM Tblskills t WHERE t.skillCategory = :skillCategory"),

    @NamedQuery(name = "Tblskills.findByCategory", query = "SELECT t FROM Tblskills t WHERE t.skillCategory = :category")
})
public class Tblskills implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "skillId")
    private Integer skillId;
    @Size(max = 200)
    @Column(name = "skillName")
    private String skillName;
    @Size(max = 100)
    @Column(name = "skillCategory")
    private String skillCategory;
    @ManyToMany(mappedBy = "tblskillsCollection")
    private Collection<Tblcandidates> tblcandidatesCollection;
    @ManyToMany(mappedBy = "tblskillsCollection")
    private Collection<Tbljob> tbljobCollection;

    public Tblskills() {
    }

    public Tblskills(Integer skillId) {
        this.skillId = skillId;
    }

    public Integer getSkillId() {
        return skillId;
    }

    public void setSkillId(Integer skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillCategory() {
        return skillCategory;
    }

    public void setSkillCategory(String skillCategory) {
        this.skillCategory = skillCategory;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tblcandidates> getTblcandidatesCollection() {
        return tblcandidatesCollection;
    }

    public void setTblcandidatesCollection(Collection<Tblcandidates> tblcandidatesCollection) {
        this.tblcandidatesCollection = tblcandidatesCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Tbljob> getTbljobCollection() {
        return tbljobCollection;
    }

    public void setTbljobCollection(Collection<Tbljob> tbljobCollection) {
        this.tbljobCollection = tbljobCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (skillId != null ? skillId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblskills)) {
            return false;
        }
        Tblskills other = (Tblskills) object;
        if ((this.skillId == null && other.skillId != null) || (this.skillId != null && !this.skillId.equals(other.skillId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Tblskills[ skillId=" + skillId + " ]";
    }
    
}
