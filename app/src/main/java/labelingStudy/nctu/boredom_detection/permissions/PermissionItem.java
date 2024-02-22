package labelingStudy.nctu.boredom_detection.permissions;

public class PermissionItem {
    String text;
    String description;
    boolean permission;

    public PermissionItem(String text, String description, boolean permission) {
        this.text = text;
        this.description = description;
        this.permission = permission;
    }
}
