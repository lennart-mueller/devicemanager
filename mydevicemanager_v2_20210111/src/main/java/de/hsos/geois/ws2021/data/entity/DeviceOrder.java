package de.hsos.geois.ws2021.data.entity;

import java.time.LocalDate;

import javax.persistence.Entity;

import de.hsos.geois.ws2021.data.AbstractEntity;

@Entity
public class DeviceOrder extends AbstractEntity {

	private String producer;
	private String deviceModel;
	private int quantity;
	private LocalDate orderDate;
	private LocalDate deliveryDate;
	
	
	public DeviceOrder() {
		this.orderDate = LocalDate.now();
	}

	public String getProducer() {
		return producer;
	}
	
	public void setProducer(String producer) {
		this.producer = producer;
	}
	
	public String getDeviceModel() {
		return deviceModel;
	}
	
	public void setDeviceModel(String deviceModel) {
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
