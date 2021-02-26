package de.hsos.geois.ws2021.data.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import de.hsos.geois.ws2021.data.AbstractEntity;

@Entity
public class Device extends AbstractEntity {

	private String name;
	private String artNr;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Customer customer;
	
	@Column(unique=true)
	private String serialNr;
	
	@Column(precision = 7, scale = 2)
	private BigDecimal purchasePrice, salesPrice;

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

	public String getSerialNr() {
		return serialNr;
	}

	public void setSerialNr(String serialNr) {
		this.serialNr = serialNr;
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

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
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
