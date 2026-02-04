package it.unicam.cs.mpgc.jbudget126603.controller;

import it.unicam.cs.mpgc.jbudget126603.model.Tag;
import java.util.List;
import java.util.Optional;

/**
 * Controller interface for managing hierarchical Tag objects.
 * Provides methods for adding, removing, updating, and retrieving tags,
 * including support for root tags and child tags.
 */
public interface TagController {

    /**
     * Adds a new tag to the system.
     *
     * @param tag the tag to add
     */
    void addTag(Tag tag);

    /**
     * Removes a tag by its ID.
     *
     * @param tagId the ID of the tag to remove
     * @return true if the tag was removed, false otherwise
     */
    boolean removeTag(String tagId);

    /**
     * Retrieves a tag by its ID.
     *
     * @param tagId the ID of the tag
     * @return an Optional containing the tag if found, or empty otherwise
     */
    Optional<Tag> getTagById(String tagId);

    /**
     * Retrieves all tags in the system.
     *
     * @return an immutable list of all tags
     */
    List<Tag> getAllTags();

    /**
     * Updates the name of a tag.
     *
     * @param tagId   the ID of the tag to update
     * @param newName the new name for the tag
     * @return true if the update was successful, false otherwise
     */
    boolean updateTagName(String tagId, String newName);

    /**
     * Retrieves all root tags (tags without a parent).
     *
     * @return a list of root tags
     */
    List<Tag> getRootTags();

    /**
     * Retrieves all child tags of a given parent.
     *
     * @param parentId the ID of the parent tag
     * @return a list of child tags belonging to the parent
     */
    List<Tag> getChildrenTags(String parentId);

    /**
     * Updates the parent of a tag.
     *
     * @param tagId       the ID of the tag to update
     * @param newParentId the ID of the new parent tag
     * @return true if the update was successful, false otherwise
     */
    boolean updateTagParent(String tagId, String newParentId);
}
