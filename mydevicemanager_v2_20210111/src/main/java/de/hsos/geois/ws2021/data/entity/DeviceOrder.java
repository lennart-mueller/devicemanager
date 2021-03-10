package de.hsos.geois.ws2021.data.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import de.hsos.geois.ws2021.data.AbstractEntity;

@Entity
public class DeviceOrder extends AbstractEntity {
	
	private LocalDate orderDate;
	private LocalDate deliveryDate;
	private String status;
	private int amountOfPositions;
	private int amountOfDevices;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Producer producer;
	
	@OneToMany(mappedBy = "deviceOrder", cascade = CascadeType.ALL, orphanRemoval = false)
	private Collection<OrderPosition> orderPositions;
	
	
	public DeviceOrder() {
		this.orderPositions = new ArrayList<OrderPosition>();
		this.orderDate = LocalDate.now();
		this.status = "Request sent";
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
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Producer getProducer() {
		return producer;
	}

	public void setProducer(Producer producer) {
		this.producer = producer;
	}
	
	public void setDeliveryDate(LocalDate deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Collection<OrderPosition> getOrderPositions() {
		return orderPositions;
	}

	public void setOrderPositions(Collection<OrderPosition> orderPositions) {
		this.orderPositions = orderPositions;
	}
	
	public boolean addOrderPosition(OrderPosition orderPosition) {
		return getOrderPositions().add(orderPosition);
	}
	
	public boolean removeOrderPosition(OrderPosition orderPosition) {
		return getOrderPositions().remove(orderPosition);
	}

	public void setAmountOfPositions(Collection<OrderPosition> orderPositions) {
		int amountOfPositions = 0;
		for(OrderPosition position : orderPositions) {
			amountOfPositions =	amountOfPositions + 1;
		}
		this.amountOfPositions = amountOfPositions;
	}
	
	public int getAmountOfPositions() {
		return this.amountOfPositions;
	}

	public void setAmountOfDevices(Collection<OrderPosition> orderPositions) {
		int amountOfDevices = 0;
		for(OrderPosition position : orderPositions) {
			amountOfDevices =	amountOfDevices + position.getQuantity();
		}
		this.amountOfDevices = amountOfDevices;
	}

	public int getAmountOfDevices() {
		return this.amountOfDevices;
	}
}
