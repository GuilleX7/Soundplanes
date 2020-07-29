package aiss.model.ircchat;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonIgnoreProperties(ignoreUnknown = true)
public enum Permission {
	EDIT ("channel:edit"),
	READ ("channel:read"),
	WRITE ("chat:write");
	
	private String permission;
	
	private static List<Permission> defaultUserPermissions;
	private static List<Permission> defaultCreatorPermissions;
	
	static {
		defaultUserPermissions = new ArrayList<Permission>();
		defaultUserPermissions.add(WRITE);
		defaultUserPermissions.add(READ);
		
		defaultCreatorPermissions = new ArrayList<Permission>();
		defaultCreatorPermissions.addAll(defaultUserPermissions);
		defaultCreatorPermissions.add(EDIT);
	}

	private Permission(String permission) {
		this.permission = permission;
	}

	@JsonValue
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public static List<Permission> getDefaultUserRoles() {
		return defaultUserPermissions;
	}

	public static List<Permission> getDefaultCreatorRoles() {
		return defaultCreatorPermissions;
	}
}
