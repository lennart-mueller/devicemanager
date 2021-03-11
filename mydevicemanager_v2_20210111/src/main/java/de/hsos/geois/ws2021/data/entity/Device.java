package de.hsos.geois.ws2021.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import de.hsos.geois.ws2021.data.AbstractEntity;

// Tracking Device

@Entity
public class Device extends AbstractEntity {
	
	@ManyToOne(fetch = FetchType.LAZY)
	private DeviceModel deviceModel;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Customer customer;
	
	@Column(unique=true)
	private String serialNr;
	
	private String isDelivered;

	public String getIsDelivered() {
		return isDelivered;
	}

	public void setIsDelivered(String isDelivered) {
		this.isDelivered = isDelivered;
	}

	public String getSerialNr() {
		return serialNr;
	}

	public void setSerialNr(String serialNr) {
		this.serialNr = serialNr;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public DeviceModel getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(DeviceModel deviceModel) {
		this.deviceModel = deviceModel;
	}
	
//	@Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Device )) return false;
//        return getId() != null && getId().equals(((Device) o).getId());
//    }
// 
//    @Override
//    public int hashCode() {
//        return getClass().hashCode();
//    }


}
