package de.hsos.geois.ws2021.data.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import de.hsos.geois.ws2021.data.AbstractEntity;

@Entity
public class DeviceModel extends AbstractEntity {

	private String name;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Producer producer;
	
	@OneToMany(mappedBy = "deviceModel", cascade = CascadeType.ALL, orphanRemoval = false)
	private Collection<OrderPosition> orderPositions;
	
	@OneToMany(mappedBy = "deviceModel", cascade = CascadeType.ALL, orphanRemoval = false)
	private Collection<Device> devices;
	
	@Column(unique=true)
	private String artNr;
	
	@Column(precision = 7, scale = 2)
	private BigDecimal purchasePrice, salesPrice;

	
	public DeviceModel() {
		this.orderPositions = new ArrayList<OrderPosition>();
		this.devices = new ArrayList<Device>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtNr() {
		return artNr;
	}

	public void setArtNr(String artNr) {
		this.artNr = artNr;
	}
	
	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(BigDecimal purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public BigDecimal getSalesPrice() {
		return salesPrice;
	}

	public void setSalesPrice(BigDecimal salesPrice) {
		this.salesPrice = salesPrice;
	}

	public Producer getProducer() {
		return producer;
	}

	public void setProducer(Producer producer) {
		this.producer = producer;
	}
	
	public Collection<OrderPosition> getOrderPositions() {
		return orderPositions;
	}

	public void setOrderPositions(Collection<OrderPosition> orderPositions) {
		this.orderPositions = orderPositions;
	}
	
	public Collection<Device> getDevices() {
		return devices;
	}

	public void setDevices(Collection<Device> devices) {
		this.devices = devices;
	}

	public boolean addOrderPosition(OrderPosition orderPosition) {
		return getOrderPositions().add(orderPosition);
	}
	
	public boolean removeOrderPosition(OrderPosition orderPosition) {
		return getOrderPositions().remove(orderPosition);
	}
	
	public boolean addDevice(Device device) {
		return getDevices().add(device);
	}
	
	public boolean removeDevice(Device device) {
		return getDevices().remove(device);
	}
	
	
	//TODO: Mehrere ToString Methoden anlegen 1x für Device Order 1x für Devices
//	public String toString() {
//		return getProducer() + " " + getName() + " " + getArtNr() + " " + getPurchasePrice() + " " + getSalesPrice();
//	}	
	
	public String toString() {
		return getName() +" ["+getProducer()+"]";
	}	
//	
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
