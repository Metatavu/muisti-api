package fi.metatavu.muisti.persistence.model

import fi.metatavu.muisti.api.spec.model.VisitorSessionState
import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*

/**
 * JPA entity representing visitor session
 *
 * @author Antti Leppä
 */
@Entity
@Inheritance (strategy = InheritanceType.JOINED)
open class VisitorSession {

    @Id
    var id: UUID? = null

    @Column(nullable = false)
    var createdAt: OffsetDateTime? = null

    @Column(nullable = false)
    var modifiedAt: OffsetDateTime? = null

    @Column(nullable = false)
    var creatorId: UUID? = null

    @Column(nullable = false)
    var lastModifierId: UUID? = null

    @ManyToOne
    var exhibition: Exhibition? = null

    var state: VisitorSessionState? = null

    /**
     * JPA pre-persist event handler
     */
    @PrePersist
    fun onCreate() {
        createdAt = OffsetDateTime.now()
        modifiedAt = OffsetDateTime.now()
    }

    /**
     * JPA pre-update event handler
     */
    @PreUpdate
    fun onUpdate() {
        modifiedAt = OffsetDateTime.now()
    }
}