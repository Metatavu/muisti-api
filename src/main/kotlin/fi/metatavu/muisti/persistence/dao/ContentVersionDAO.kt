package fi.metatavu.muisti.persistence.dao

import fi.metatavu.muisti.persistence.model.ContentVersion
import fi.metatavu.muisti.persistence.model.Exhibition
import fi.metatavu.muisti.persistence.model.ContentVersion_
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

/**
 * DAO class for ContentVersion
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class ContentVersionDAO() : AbstractDAO<ContentVersion>() {

    /**
     * Creates new ContentVersion
     *
     * @param id id
     * @param exhibition exhibition
     * @param name name
     * @param language language code
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @return created contentVersion
     */
    fun create(id: UUID, exhibition: Exhibition, name: String, language: String, creatorId: UUID, lastModifierId: UUID): ContentVersion {
        val contentVersion = ContentVersion()
        contentVersion.id = id
        contentVersion.name = name
        contentVersion.language = language
        contentVersion.exhibition = exhibition
        contentVersion.creatorId = creatorId
        contentVersion.lastModifierId = lastModifierId
        return persist(contentVersion)
    }

    /**
     * Lists ContentVersions by exhibition
     *
     * @param exhibition exhibition
     * @return List of ContentVersions
     */
    fun listByExhibition(exhibition: Exhibition): List<ContentVersion> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria: CriteriaQuery<ContentVersion> = criteriaBuilder.createQuery(ContentVersion::class.java)
        val root: Root<ContentVersion> = criteria.from(ContentVersion::class.java)
        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(ContentVersion_.exhibition), exhibition))
        val query: TypedQuery<ContentVersion> = entityManager.createQuery<ContentVersion>(criteria)
        return query.resultList
    }

    /**
     * Updates name
     *
     * @param contentVersion content version
     * @param name name
     * @param lastModifierId last modifier's id
     * @return updated contentVersion
     */
    fun updateName(contentVersion: ContentVersion, name: String, lastModifierId: UUID): ContentVersion {
        contentVersion.lastModifierId = lastModifierId
        contentVersion.name = name
        return persist(contentVersion)
    }

    /**
     * Updates language
     *
     * @param contentVersion content version
     * @param language language code
     * @param lastModifierId last modifier's id
     * @return updated contentVersion
     */
    fun updateLanguage(contentVersion: ContentVersion, language: String, lastModifierId: UUID): ContentVersion {
        contentVersion.lastModifierId = lastModifierId
        contentVersion.language = language
        return persist(contentVersion)
    }

}