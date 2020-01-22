package fi.metatavu.muisti.persistence.dao

import org.slf4j.Logger
import java.lang.RuntimeException
import java.lang.reflect.ParameterizedType
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceContext
import javax.persistence.Query
import javax.persistence.TypedQuery

/**
 * Abstract base class for all DAO classes
 *
 * @author Antti Leppä
 *
 * @param <T> entity type
</T> */
open abstract class AbstractDAO<T>() {

    @Inject
    private lateinit var logger: Logger

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Returns entity by id
     *
     * @param id entity id
     * @return entity or null if non found
     */
    open fun findById(id: UUID): T {
        return entityManager.find(genericTypeClass, id)
    }

    /**
     * Returns entity by id
     *
     * @param id entity id
     * @return entity or null if non found
     */
    open fun findById(id: Long): T {
        return entityManager.find(genericTypeClass, id)
    }

    /**
     * Lists all entities from database
     *
     * @return all entities from database
     */
    open fun listAll(): List<T> {
        val genericTypeClass: Class<*>? = genericTypeClass
        val query: Query = entityManager.createQuery("select o from " + genericTypeClass!!.name + " o")
        return query.getResultList() as List<T>
    }

    /**
     * Lists all entities from database limited by firstResult and maxResults parameters
     *
     * @param firstResult first result
     * @param maxResults max results
     * @return all entities from database limited by firstResult and maxResults parameters
     */
    open fun listAll(firstResult: Int, maxResults: Int): List<T> {
        val genericTypeClass: Class<*>? = genericTypeClass
        val query: Query = entityManager.createQuery("select o from " + genericTypeClass!!.name + " o")
        query.setFirstResult(firstResult)
        query.setMaxResults(maxResults)
        return query.getResultList() as List<T>
    }

    /**
     * Deletes entity
     *
     * @param e entity
     */
    open fun delete(e: T) {
        entityManager.remove(e)
        flush()
    }

    /**
     * Flushes persistence context state
     */
    open fun flush() {
        entityManager.flush()
    }

    /**
     * Persists an entity
     *
     * @param object entity to be persisted
     * @return persisted entity
     */
    protected open fun persist(`object`: T): T {
        entityManager.persist(`object`)
        return `object`
    }

    /**
     * Returns single result entity or null if result is empty
     *
     * @param query query
     * @return entity or null if result is empty
     */
    protected open fun <X> getSingleResult(query: TypedQuery<X>): X? {
        val list: List<X> = query.getResultList()
        if (list.isEmpty()) return null
        if (list.size > 1) {
            logger.error(String.format("SingleResult query returned %d elements from %s", list.size, genericTypeClass!!.name))
        }
        return list[list.size - 1]
    }

    protected open val genericTypeClass: Class<T>?
        get() {
            val genericSuperclass = javaClass.genericSuperclass
            if (genericSuperclass is ParameterizedType) {
                return getFirstTypeArgument(genericSuperclass)
            } else {
                if (genericSuperclass is Class<*> && AbstractDAO::class.java.isAssignableFrom(genericSuperclass)) {
                    return getFirstTypeArgument(genericSuperclass.genericSuperclass as ParameterizedType)
                }
            }
            return null
        }

    protected open fun getFirstTypeArgument(parameterizedType: ParameterizedType): Class<T> {
        return parameterizedType.actualTypeArguments[0] as Class<T>
    }

}