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
	private Collection<DeviceOrder> deviceOrders;
	
	@Column(unique=true)
	private String artNr;
	
	@Column(precision = 7, scale = 2)
	private BigDecimal purchasePrice, salesPrice;

	
	public DeviceModel() {
		this.deviceOrders = new ArrayList<DeviceOrder>();
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
	
	public Collection<DeviceOrder> getDeviceOrders() {
		return deviceOrders;
	}

	public void setDeviceOrders(Collection<DeviceOrder> deviceOrders) {
		this.deviceOrders = deviceOrders;
	}
	
	public boolean addDeviceOrder(DeviceOrder deviceOrder) {
		return getDeviceOrders().add(deviceOrder);
	}
	
	public boolean removeDeviceOrder(DeviceOrder deviceOrder) {
		return getDeviceOrders().remove(deviceOrder);
	}
	
	public String toString() {
		return getName();
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