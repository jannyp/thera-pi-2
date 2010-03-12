package org.thera_pi.nebraska;

import java.util.regex.Pattern;

/**
 * This class represents a principal (subject or issuer of a certificate).
 * It is used to split the distinguished name string into the usual fields.
 *    
 * @author bodo
 *
 */
public class NebraskaPrincipal {
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getInstitutionID() {
		return institutionID;
	}

	public void setInstitutionID(String institutionID) {
		this.institutionID = institutionID;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getAlias() {
		if(institutionID != null && institutionID.length() > 0)
		{
			return institutionID;
		}
		return distinguishedName.replace(" ", "");
	}
	
	private String country;
	private String organization;
	private String institutionName;
	private String institutionID;
	private String personName;
	private String distinguishedName;
	
	public NebraskaPrincipal() {
		
	}
	
	public NebraskaPrincipal(String country, String organization,
			String institutionName, String institutionID, String personName) {
		this.country = country;
		this.organization = organization;
		this.institutionName = institutionName;
		this.institutionID = institutionID;
		this.personName = personName;
	}
	
	public NebraskaPrincipal(String distinguishedName) {
		this.distinguishedName = distinguishedName;
		String[] dnParts = distinguishedName.split(",");
		for(int i = 0; i < dnParts.length; i++) {
			String[] keyVal = dnParts[i].trim().split(" *= *", 2);
			if(keyVal.length == 2) {
				if("CN".equals(keyVal[0])) {
					personName = keyVal[1];
				} else if("OU".equals(keyVal[0])) {
					if(Pattern.matches("^IK[0-9]+$", keyVal[1])) {
						institutionID = NebraskaUtil.normalizeIK(keyVal[1]);
					} else {
						institutionName = keyVal[1];
					}
				} else if("C".equals(keyVal[0])) {
					country = keyVal[1];
				} else if("O".equals(keyVal[0])) {
					organization = keyVal[1];
				}
			}
		}
	}
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(!(o instanceof NebraskaPrincipal)) return false;

		NebraskaPrincipal other = (NebraskaPrincipal) o;
		if((this.country == null) != (other.country == null)) return false;
		if((this.organization == null) != (other.organization == null)) return false;
		if((this.institutionID == null) != (other.institutionID == null)) return false;
		if((this.institutionName == null) != (other.institutionName == null)) return false;
		if((this.personName == null) != (other.personName == null)) return false;
		
		if(this.country != null && !this.country.equals(other.country)) return false;
		if(this.organization != null && !this.organization.equals(other.organization)) return false;
		if(this.institutionID != null && !this.institutionID.equals(other.institutionID)) return false;
		if(this.institutionName != null && !this.institutionName.equals(other.institutionName)) return false;
		if(this.personName != null && !this.personName.equals(other.personName)) return false;

		return true;
	}
}
