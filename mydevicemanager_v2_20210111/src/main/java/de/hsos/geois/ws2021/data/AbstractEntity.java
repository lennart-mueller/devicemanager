package de.hsos.geois.ws2021.data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEntity {
    
	@Id  //Moved up from getId()
    @GeneratedValue
	private Long id;
	
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
    	
        if (!(obj instanceof AbstractEntity)) {
            return false; // null or other class
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }

        AbstractEntity other = (AbstractEntity) obj;

        if (id != null) {
            return id.equals(other.id);
        }
        return super.equals(other);
    }
}