package de.hsos.geois.ws2021.data.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import de.hsos.geois.ws2021.data.AbstractEntity;

@Entity
public class OrderPosition extends AbstractEntity {
	
	private int quantity;

	@ManyToOne(fetch = FetchType.LAZY)
	private DeviceModel deviceModel;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private DeviceOrder deviceOrder;
	
	
	public DeviceModel getDeviceModel() {
		return deviceModel;
	}
	
	public void setDeviceModel(DeviceModel deviceModel) {
		this.deviceModel = deviceModel;
	}
	
	public DeviceOrder getDeviceOrder() {
		return deviceOrder;
	}
	
	public void setDeviceOrder(DeviceOrder deviceOrder) {
		this.deviceOrder = deviceOrder;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}	
}
