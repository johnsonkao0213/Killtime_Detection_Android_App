package labelingStudy.nctu.boredom_detection.labelImage;

import java.util.Objects;

import labelingStudy.nctu.minuku.logger.Log;

public class LabelImage {
    public static final int LABEL_PRIVATE = -2;
    public static final int LABEL_UNLABEL = -1;
    private String id;
    private String dir;
    private String name;
    private int label;
    private boolean check;

    // Constructor
    public LabelImage(String id, String dir, String name) {
        this.id = id;
        this.dir = dir;
        this.name = name;
        this.label = LABEL_UNLABEL;
        this.check = false;
    }
    public LabelImage(LabelImage original) {

        this.id = original.id;
        this.dir = original.dir;
        this.name = original.name;
        this.label = original.label;
        this.check = original.check;
        // Copy other fields if needed
    }
    // Setters
    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    // Getters
    public String getDir() {
        return dir;
    }

    public String getName() {
        return name;
    }

    public int getLabel() {
        return label;
    }

    public String getId() {
        return id;
    }

    public boolean isCheck() {
        return check;
    }

    public boolean isLabeled() {
        return this.label != LABEL_UNLABEL;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LabelImage other = (LabelImage) obj;
        Log.d("LabelImage", "Comparing: " + id + ", " + other.id);
        return id.equals(other.id) &&
                dir.equals(other.dir) &&
                name.equals(other.name) &&
                label == other.label &&
                check == other.check;
    }
    @Override
    public int hashCode() {
        return Objects.hash(dir, name, label, id, check);
    }
}
