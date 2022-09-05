package models;

import models.types.AccountType;
import models.types.PortalApplicationType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jesu on 08/06/17.
 */
@Entity
public class Portal extends BaseModel {

	@NotNull
	private String name;

	private String description;

	@NotNull
	private AccountType accountType;

	@OneToOne
	@NotNull
	private Admin owner;

	@OneToMany
	private Set<Admin> administrators;

	private String homeURL;

	private String facebookURL;

	private String twitterURL;

	private String googlePlusURL;

	private String instagramURL;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "portal")
	private Set<PortalNetworkConfiguration> networkConfigurations = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL)
	private final Set<PortalLoginConfiguration> loginConfigurations = new HashSet<>();

	public void setApplications(Set<PortalApp> applications) {
		this.applications = applications;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "portal")
	private Set<PortalApp> applications = new HashSet<>();
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "portal")
	private final Set<AnalyticsQueryFilter> queryFilters = new HashSet<>();


	public Portal() {
	}
	
	public Portal(Long id) {
		this.id = id;
	}

	public Portal(String name, String description, AccountType accountType, Admin owner, String homeURL, String facebookURL,
				  String twitterURL, String googlePlusURL, String instagramURL) {
		this.name = name;
		this.description = description;
		this.accountType = accountType;
		this.owner = owner;
		this.homeURL = homeURL;
		this.facebookURL = facebookURL;
		this.twitterURL = twitterURL;
		this.googlePlusURL = googlePlusURL;
		this.instagramURL = instagramURL;
	}


	@Override
	public String toLogString() {
		return toLogString("id: " + id, "name: " + name, "admin: " + owner.toLogString(), "homeURL: " + homeURL, "facebookURL: " + facebookURL,
				"twitterURL: " + twitterURL, "googlePlusURL: " + googlePlusURL, "instagramURL: " + instagramURL);
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public Admin getOwner() {
		return owner;
	}

	public void setOwner(Admin owner) {
		this.owner = owner;
	}

	public String getHomeURL() {
		return homeURL;
	}

	public void setHomeURL(String homeURL) {
		this.homeURL = homeURL;
	}

	public String getFacebookURL() {
		return facebookURL;
	}

	public void setFacebookURL(String facebookURL) {
		this.facebookURL = facebookURL;
	}

	public String getTwitterURL() {
		return twitterURL;
	}

	public void setTwitterURL(String twitterURL) {
		this.twitterURL = twitterURL;
	}

	public String getGooglePlusURL() {
		return googlePlusURL;
	}

	public void setGooglePlusURL(String googlePlusURL) {
		this.googlePlusURL = googlePlusURL;
	}

	public String getInstagramURL() {
		return instagramURL;
	}

	public void setInstagramURL(String instagramURL) {
		this.instagramURL = instagramURL;
	}

	public Set<PortalLoginConfiguration> getLoginConfigurations() {
		return loginConfigurations;
	}

	public boolean hasSocialLoginEnabled() {
		return loginConfigurations.stream()
				.anyMatch(PortalLoginConfiguration::hasSocialLoginEnabled);
	}

	public void setNetworkConfigurations(Set<PortalNetworkConfiguration> networkConfigurations) {
		this.networkConfigurations = networkConfigurations;
	}

	public Set<PortalNetworkConfiguration> getNetworkConfigurations() {
		return networkConfigurations;
	}

	public Set<PortalApp> getApplications() {
		return applications;
	}

	public Map<PortalApplicationType, PortalApp> getApplicationsByType() {
		return applications.stream()
				.collect(Collectors.toMap(PortalApp::getType, Function.identity()));
	}
}
