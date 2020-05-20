package aiss.model.ircchat;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonIgnoreProperties(ignoreUnknown = true)
public enum Rol {
	EDIT ("ch:edit"),
	READ ("ch:read"),
	CONNECT ("ch:connect");
	
	private String rolName;
	
	private static List<Rol> defaultUserRoles;
	private static List<Rol> defaultCreatorRoles;
	
	static {
		defaultUserRoles = new ArrayList<Rol>();
		defaultUserRoles.add(CONNECT);
		defaultUserRoles.add(READ);
		
		defaultCreatorRoles = new ArrayList<Rol>();
		defaultCreatorRoles.addAll(defaultUserRoles);
		defaultCreatorRoles.add(EDIT);
	}

	private Rol(String rolName) {
		this.rolName = rolName;
	}

	@JsonValue
	public String getRolName() {
		return rolName;
	}

	public void setRolName(String rolName) {
		this.rolName = rolName;
	}

	public static List<Rol> getDefaultUserRoles() {
		return defaultUserRoles;
	}

	public static List<Rol> getDefaultCreatorRoles() {
		return defaultCreatorRoles;
	}
}
