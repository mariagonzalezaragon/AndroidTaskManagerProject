package com.example.taskmanager_project;

public class Role {
    private String roleId;
    private String roleName;
    private String roleDescription;
    private boolean fullAccess;

    public Role(String roleId, String roleName, String roleDescription, boolean fullAccess) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
        this.fullAccess = fullAccess;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public boolean isFullAccess() {
        return fullAccess;
    }

}
