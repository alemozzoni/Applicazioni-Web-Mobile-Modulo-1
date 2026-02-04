package it.unicam.cs.mpgc.jbudget126603.controller;

import it.unicam.cs.mpgc.jbudget126603.model.Tag;
import it.unicam.cs.mpgc.jbudget126603.persistency.PersistenceManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of TagController with persistence support.
 * Manages the creation, retrieval, update, and deletion of Tag objects,
 * while keeping the tag list synchronized with the underlying persistence layer.
 */
public class TagManager implements TagController {

    private final PersistenceManager persistence;
    private final List<Tag> tags;
    private final AtomicLong counter = new AtomicLong(1);

    /**
     * Creates a new TagManager with the given persistence manager.
     * Loads all existing tags from persistence and initializes the ID counter.
     *
     * @param persistence the persistence manager responsible for saving and loading tags
     */
    public TagManager(PersistenceManager persistence) {
        this.persistence = persistence;
        this.tags = new ArrayList<>(persistence.loadTags());

        tags.stream()
                .map(Tag::id)
                .mapToLong(Long::parseLong)
                .max()
                .ifPresent(counter::set);
    }

    /**
     * Generates the next unique ID for a new tag.
     *
     * @return the generated ID as a string
     */
    private String nextId() {
        return String.valueOf(counter.incrementAndGet());
    }

    @Override
    public void addTag(Tag tag) {
        tags.add(tag);
        persistence.saveTags(tags);
    }

    /**
     * Creates and persists a new tag with the given name and parent ID.
     *
     * @param name     the name of the new tag
     * @param parentId the ID of the parent tag, or null if it is a root tag
     * @return the created tag
     */
    public Tag createTag(String name, String parentId) {
        Tag tag = new Tag(nextId(), name, parentId);
        addTag(tag);
        return tag;
    }

    @Override
    public boolean removeTag(String tagId) {
        boolean removed = tags.removeIf(t -> t.id().equals(tagId));
        if (removed) persistence.saveTags(tags);
        return removed;
    }

    @Override
    public Optional<Tag> getTagById(String tagId) {
        return tags.stream().filter(t -> t.id().equals(tagId)).findFirst();
    }

    @Override
    public List<Tag> getAllTags() {
        return List.copyOf(tags);
    }

    @Override
    public boolean updateTagName(String tagId, String newName) {
        Optional<Tag> opt = getTagById(tagId);
        if (opt.isPresent()) {
            Tag tag = opt.get();
            tag.setName(newName);
            persistence.saveTags(tags);
            return true;
        }
        return false;
    }

    @Override
    public List<Tag> getRootTags() {
        return tags.stream().filter(t -> t.parentId() == null).toList();
    }

    @Override
    public List<Tag> getChildrenTags(String parentId) {
        return tags.stream().filter(t -> parentId.equals(t.parentId())).toList();
    }

    @Override
    public boolean updateTagParent(String tagId, String newParentId) {
        Optional<Tag> opt = getTagById(tagId);
        if (opt.isPresent()) {
            Tag tag = opt.get();
            tag.setParentId(newParentId);
            persistence.saveTags(tags);
            return true;
        }
        return false;
    }
}
