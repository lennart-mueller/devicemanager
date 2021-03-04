package de.hsos.geois.ws2021.data.entity;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import de.hsos.geois.ws2021.data.AbstractEntity;

@Entity
public class Producer extends AbstractEntity {

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
	
	@OneToMany(mappedBy = "producer", cascade = CascadeType.ALL, orphanRemoval = false)
	private Collection<DeviceModel> deviceModels;
	
	
	public Producer() {
		this.deviceModels = new ArrayList<DeviceModel>();
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

	public Collection<DeviceModel> getDeviceModels() {
		return deviceModels;
	}

	public void setDeviceModels(Collection<DeviceModel> deviceModels) {
		this.deviceModels = deviceModels;
	}
	
	public boolean addDeviceModel(DeviceModel deviceModel) {
		return getDeviceModels().add(deviceModel);
	}
	
	public boolean removeDeviceModel(DeviceModel deviceModel) {
		return getDeviceModels().remove(deviceModel);
	}
	
	public String toString() {
		return getCompanyName();
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
