package de.hsos.geois.ws2021.data.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import de.hsos.geois.ws2021.data.AbstractEntity;

@Entity
public class DeviceOrder extends AbstractEntity {
	
	private int quantity;
	private LocalDate orderDate;
	private LocalDate deliveryDate;

	@ManyToOne(fetch = FetchType.LAZY)
	private DeviceModel deviceModel;
	
	
	public DeviceOrder() {
		this.orderDate = LocalDate.now();
	}
	
	public DeviceModel getDeviceModel() {
		return deviceModel;
	}
	
	public void setDeviceModel(DeviceModel deviceModel) {
		this.deviceModel = deviceModel;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}
	
	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}
	
	public LocalDate getDeliveryDate() {
		return deliveryDate;
	}
	
	public void setDeliveryDate(LocalDate deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	
}
