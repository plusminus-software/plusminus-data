package software.plusminus.data.event;

/**
 * The kind of data change an entity underwent. Mirrors the {@link DataEvent} subtypes and is
 * kept as a plain enum so it can be persisted or passed around by consumers that need to
 * describe an action without holding an event instance.
 */
public enum CrudAction {
    READ,
    CREATE,
    UPDATE,
    PATCH,
    DELETE
}
