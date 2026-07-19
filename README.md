# Plusminus Data

Simplifies the development of the data access layer in Spring Boot applications.

A multi-module library that provides a generic CRUD REST API over JPA entities, plus opt-in cross-cutting features: partial updates (PATCH), repository lifecycle events, soft delete, multitenancy, audit logging and data synchronization.

Built on Spring Boot 2.x / Spring Data JPA / Hibernate, targets Java 8. All modules share group id `software.plusminus` and version `1.0-SNAPSHOT`.

## Modules

| Module | Description |
|---|---|
| `plusminus-data` | Core module: generic CRUD services, repositories, DTO conversion, validation groups and the `/data` REST controller. |
| `plusminus-data-event` | Publishes events (`BeforeCreateEvent`, `CreateEvent`, `UpdateEvent`, `DeleteEvent`, `ReadEvent`, …) around Spring Data repository operations via an AOP aspect, so other modules can react to entity lifecycle points. |
| `plusminus-metadata` | Registry of managed entity classes (`MetadataProvider`, `MetadataContext`), used to resolve entity types by name at runtime. |
| `plusminus-patch` | Partial-update engine (`PatchService`): copies non-null properties from a patch object onto a target, with pluggable collection-merge strategies (`@CollectionPatch`, `@StringCollectionPatch`). |
| `plusminus-json` | JSON support for entities: `ApiObject`/`Classable`/`Jsog` marker interfaces, JSOG handling for cyclic object graphs, `@Uuid` annotation. |
| `plusminus-dehydration` | Jackson serializer that "dehydrates" nested entity references into lightweight references instead of full object graphs. |
| `plusminus-hibernate` | Dynamic per-request enabling of Hibernate `@Filter`s (`HibernateFilter`, `HibernateFilterService`) — the foundation for soft delete and multitenancy. |
| `plusminus-softdelete` | Soft delete via `@SoftDelete`: deletes become logical flags, and a Hibernate filter hides soft-deleted rows. |
| `plusminus-tenant` | Multitenancy via `@Tenant`: queries and writes are scoped to the current tenant, resolved by a pluggable `TenantProvider` (security context or URL). |
| `plusminus-audit` | Audit logging via `@Auditable`: CRUD actions are recorded as tenant-aware, sequenced `AuditLog` entities. |
| `plusminus-sync` | Data synchronization for offline/multi-device clients via `@Syncable`: a `/sync` REST API to read a change stream and write client changes back, with merge strategies and audit-log-based change tracking. |

In-repo dependency graph (arrows = "depends on"):

```
json        → metadata
dehydration → json
softdelete  → hibernate
tenant      → hibernate, data-event
data        → patch, metadata, data-event
audit       → tenant, data-event
sync        → data, audit, json, tenant, dehydration
```

`metadata`, `patch`, `hibernate` and `data-event` have no in-repo dependencies.

## Getting started

Add the modules you need, e.g.:

```xml
<dependency>
    <groupId>software.plusminus</groupId>
    <artifactId>plusminus-data</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

All modules ship Spring Boot auto-configurations, so they activate automatically once on the classpath.

### Generic REST controller

`DataController` exposes CRUD endpoints under `/data` for any managed entity. It is disabled by default; enable it with:

```properties
plusminus.data.controller=true
```

| Method | Path | Description |
|---|---|---|
| `GET` | `/data/{type}/{id}` | Get an entity by type name and id |
| `GET` | `/data/{type}` | Get a page of entities (supports `Pageable` params) |
| `POST` | `/data` | Create an entity (validated with the `Create` group) |
| `PUT` | `/data` | Update an entity (`Update` group) |
| `PATCH` | `/data` | Partially update an entity (`Patch` group) |
| `DELETE` | `/data` | Delete an entity (`Delete` group) |

Entity type names are resolved through `plusminus-metadata`; unknown types return `404`.

### Cross-cutting features

Annotate your entities to opt in:

```java
@Entity
@SoftDelete   // logical deletes
@Tenant       // scope to the current tenant
@Auditable    // record CRUD actions in the audit log
@Syncable     // expose through the /sync API
public class Article {
    ...
}
```

## Building

Requires JDK 8. Build with the Maven wrapper:

```
./mvnw clean install
```

The build enforces Checkstyle, PMD, SpotBugs and a JaCoCo coverage minimum (60% line and branch per module).

## License

[Apache License, Version 2.0](LICENSE)
