package fi.metatavu.muisti.persistence.dao

import fi.metatavu.muisti.api.spec.model.ScreenOrientation
import fi.metatavu.muisti.persistence.model.*
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * DAO class for sub layout
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class SubLayoutDAO() : AbstractDAO<SubLayout>() {

    /**
     * Creates new sub layout
     *
     * @param id id
     * @param name name
     * @param data data
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @return created sub layout
     */
    fun create(id: UUID, name: String, data: String, creatorId: UUID, lastModifierId: UUID): SubLayout {
        val subLayout = SubLayout()
        subLayout.id = id
        subLayout.name = name
        subLayout.data = data
        subLayout.creatorId = creatorId
        subLayout.lastModifierId = lastModifierId
        return persist(subLayout)
    }

    /**
     * Updates name
     *
     * @param name name
     * @param lastModifierId last modifier's id
     * @return updated sub layout
     */
    fun updateName(subLayout: SubLayout, name: String, lastModifierId: UUID): SubLayout {
        subLayout.lastModifierId = lastModifierId
        subLayout.name = name
        return persist(subLayout)
    }

    /**
     * Updates data
     *
     * @param data data
     * @param lastModifierId last modifier's id
     * @return updated sub layout
     */
    fun updateData(subLayout: SubLayout, data: String, lastModifierId: UUID): SubLayout {
        subLayout.lastModifierId = lastModifierId
        subLayout.data = data
        return persist(subLayout)
    }
}