package fi.metatavu.muisti.persistence.model

import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotEmpty

/**
 * JPA entity representing exhibition device group
 *
 * @author Antti Leppä
 */
@Entity
class ExhibitionDeviceModel {

    @Id
    var id: UUID? = null

    @ManyToOne
    var exhibition: Exhibition? = null

    @NotEmpty
    @Column(nullable = false)
    var manufacturer: String? = null

    @NotEmpty
    @Column(nullable = false)
    var model: String? = null

    var dimensionWidth: Double? = null

    var dimensionHeight: Double? = null

    var heightPixels: Int? = null

    var widthPixels: Int? = null

    var density: Double? = null

    var xdpi: Double? = null

    var ydpi: Double? = null

    @Column(nullable = false)
    var capabilityTouch: Boolean = false

    @Column(nullable = false)
    var createdAt: OffsetDateTime? = null

    @Column(nullable = false)
    var modifiedAt: OffsetDateTime? = null

    @Column(nullable = false)
    var creatorId: UUID? = null

    @Column(nullable = false)
    var lastModifierId: UUID? = null

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