package org.molgenis.lifelines.Entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author joris lops
 */
@Entity(name="PUBLIC_DICTIONARY")
public class PublicDictionary implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition="ID")
    private Long id;
    
    @Column(name="TABNAAM")
    private String tableName;
    @Column(name="NAAM")
    private String columnName;
    @Column(name="DATTYPE")
    @Enumerated(EnumType.ORDINAL)
    private eDataType dataType;

    @OneToMany(mappedBy="publicDictionary")
    List<ValueLabels> valueLabels;
    
    
    public enum eDataType {
        NUMBER(5),
        DATE(23),
        TEXT(1);
        
        private int value;

        private eDataType(int value) {
            this.value = value;
        }
    }
    

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public eDataType getDataType() {
        return dataType;
    }

    public void setDataType(eDataType dataType) {
        this.dataType = dataType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ValueLabels> getValueLabels() {
        return valueLabels;
    }

    public void setValueLabels(List<ValueLabels> valueLabels) {
        this.valueLabels = valueLabels;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PublicDictionary other = (PublicDictionary) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.tableName == null) ? (other.tableName != null) : !this.tableName.equals(other.tableName)) {
            return false;
        }
        if ((this.columnName == null) ? (other.columnName != null) : !this.columnName.equals(other.columnName)) {
            return false;
        }
        if ((this.dataType == null) ? (other.dataType != null) : !this.dataType.equals(other.dataType)) {
            return false;
        }
        if (this.valueLabels != other.valueLabels && (this.valueLabels == null || !this.valueLabels.equals(other.valueLabels))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 13 * hash + (this.tableName != null ? this.tableName.hashCode() : 0);
        hash = 13 * hash + (this.columnName != null ? this.columnName.hashCode() : 0);
        hash = 13 * hash + (this.dataType != null ? this.dataType.hashCode() : 0);
        hash = 13 * hash + (this.valueLabels != null ? this.valueLabels.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "PublicDictionary{" + "id=" + id + ", tableName=" + tableName + ", columnName=" + columnName + ", dataType=" + dataType + '}';
    }

    
    
    
    
    

    
}
