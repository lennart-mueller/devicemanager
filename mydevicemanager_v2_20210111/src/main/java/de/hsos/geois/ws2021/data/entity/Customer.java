package de.hsos.geois.ws2021.data.entity;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import de.hsos.geois.ws2021.data.AbstractEntity;

@Entity
public class Customer extends AbstractEntity {

	private String companyName = "";
	
	// contact person
	private String salutation = "";
	private String firstName = "";
	private String lastName = "";
	
	private String addressSecondLine = "";
	private String streetAndNr = "";
	
	private String place = "";
	private String zipCode = "";

	private String email = "";
	private String phone = "";
	
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = false)
	private Collection<Device> devices;
	
	public Customer() {
		this.devices = new ArrayList<Device>();
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddressSecondLine() {
		return addressSecondLine;
	}

	public void setAddressSecondLine(String addressSecondLine) {
		this.addressSecondLine = addressSecondLine;
	}

	public String getStreetAndNr() {
		return streetAndNr;
	}

	public void setStreetAndNr(String streetAndNr) {
		this.streetAndNr = streetAndNr;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Collection<Device> getDevices() {
		return devices;
	}

	public void setDevices(Collection<Device> devices) {
		this.devices = devices;
	}
	
	public boolean addDevice(Device device) {
		return getDevices().add(device);
	}
	
	public boolean removeDevice(Device device) {
		return getDevices().remove(device);
	}
	
	public String toString() {
		return getLastName() + ", " + getFirstName();
	}
	
//	@Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Customer )) return false;
//        return getId() != null && getId().equals(((Customer) o).getId());
//    }
// 
//    @Override
//    public int hashCode() {
//        return getClass().hashCode();
//    }
	
}
