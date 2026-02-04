package it.unicam.cs.mpgc.jbudget126603.model;

import java.util.Objects;

/**
 * Represents a tag that can optionally have a parent tag, allowing
 * a hierarchical categorization structure.
 * Tags are identified by a unique id, have a name,
 * and may optionally reference a parentId, which links them
 * to a parent tag in the hierarchy.
 */
public class Tag {
    private final String id;
    private String name;
    private String parentId;

    /**
     * Creates a new Tag.
     *
     * @param id       the unique identifier of the tag (cannot be null)
     * @param name     the name of the tag (cannot be null)
     * @param parentId the ID of the parent tag, or null if this is a root tag
     * @throws NullPointerException if id or name are null
     */
    public Tag(String id, String name, String parentId) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.parentId = parentId;
    }

    /**
     * Returns the unique identifier of the tag.
     *
     * @return the tag ID
     */
    public String id() {
        return id;
    }

    /**
     * Returns the name of the tag.
     *
     * @return the tag name
     */
    public String name() {
        return name;
    }

    /**
     * Updates the name of the tag.
     *
     * @param name the new name (cannot be null)
     * @throws NullPointerException if name is null
     */
    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    /**
     * Returns the ID of the parent tag, or null if this is a root tag.
     *
     * @return the parent tag ID or null
     */
    public String parentId() {
        return parentId;
    }

    /**
     * Updates the parent ID of the tag.
     *
     * @param parentId the new parent tag ID, or null if this should be a root tag
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Returns a string representation of the tag.
     * If the tag has no parent, only its name is shown.
     * If it has a parent, the parent ID is included in the string.
     *
     * @return a string describing the tag
     */
    @Override
    public String toString() {
        return parentId == null ? name : name + " (child of " + parentId + ")";
    }
}
