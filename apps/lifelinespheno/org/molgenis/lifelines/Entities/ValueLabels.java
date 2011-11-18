package org.molgenis.lifelines.Entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 *
 * @author jorislops
 */
@Entity(name="PUBLIC_VALUE_LABELS")
public class ValueLabels implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="TABNAAM")
    private String tableName;
    @Id
    @Column(name="NAAM")
    private String columnName;
    @Id
    @Column(name="VALLABEL")
    private String valLabel;

    
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name="TABNAAM", referencedColumnName="TABNAAM"),
        @JoinColumn(name="NAAM", referencedColumnName="NAAM")
    })
    private PublicDictionary publicDictionary;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public PublicDictionary getPublicDictionary() {
        return publicDictionary;
    }

    public void setPublicDictionary(PublicDictionary publicDictionary) {
        this.publicDictionary = publicDictionary;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getValLabel() {
        return valLabel;
    }

    public void setValLabel(String valLabel) {
        this.valLabel = valLabel;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValueLabels other = (ValueLabels) obj;
        if ((this.tableName == null) ? (other.tableName != null) : !this.tableName.equals(other.tableName)) {
            return false;
        }
        if ((this.columnName == null) ? (other.columnName != null) : !this.columnName.equals(other.columnName)) {
            return false;
        }
        if ((this.valLabel == null) ? (other.valLabel != null) : !this.valLabel.equals(other.valLabel)) {
            return false;
        }
        if (this.publicDictionary != other.publicDictionary && (this.publicDictionary == null || !this.publicDictionary.equals(other.publicDictionary))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.tableName != null ? this.tableName.hashCode() : 0);
        hash = 67 * hash + (this.columnName != null ? this.columnName.hashCode() : 0);
        hash = 67 * hash + (this.valLabel != null ? this.valLabel.hashCode() : 0);
        hash = 67 * hash + (this.publicDictionary != null ? this.publicDictionary.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ValueLabels{" + "tableName=" + tableName + ", columnName=" + columnName + ", publicDictionary=" + publicDictionary + '}';
    }

    
    

    
 

 
    
}
