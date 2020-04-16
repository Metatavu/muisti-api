package fi.metatavu.muisti.pages

import com.fasterxml.jackson.databind.ObjectMapper
import fi.metatavu.muisti.api.spec.model.ExhibitionPageEventTrigger
import fi.metatavu.muisti.api.spec.model.ExhibitionPageResource
import fi.metatavu.muisti.persistence.dao.ExhibitionPageDAO
import fi.metatavu.muisti.persistence.model.Exhibition
import fi.metatavu.muisti.persistence.model.ExhibitionDevice
import fi.metatavu.muisti.persistence.model.ExhibitionPage
import fi.metatavu.muisti.persistence.model.PageLayout
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for exhibition pages
 */
@ApplicationScoped
class ExhibitionPageController() {

    @Inject
    private lateinit var exhibitionPageDAO: ExhibitionPageDAO

    /**
     * Creates new exhibition page 
     *
     * @param device device
     * @param layout layout
     * @param name name
     * @param resources resources
     * @param eventTriggers event triggers
     * @param creatorId creating user id
     * @return created exhibition page 
     */
    fun createExhibitionPage(exhibition: Exhibition, device: ExhibitionDevice, layout: PageLayout, name: String, resources: List<ExhibitionPageResource>, eventTriggers:  List<ExhibitionPageEventTrigger>, creatorId: UUID): ExhibitionPage {
        return exhibitionPageDAO.create(UUID.randomUUID(), exhibition, device, layout, name, getDataAsString(resources), getDataAsString(eventTriggers), creatorId, creatorId)
    }


    /**
     * Finds an exhibition page by id
     *
     * @param id exhibition page id
     * @return found exhibition page  or null if not found
     */
    fun findExhibitionPageById(id: UUID): ExhibitionPage? {
        return exhibitionPageDAO.findById(id)
    }

    /**
     * Lists pages in an exhibition
     *
     * @returns all pages in an exhibition
     */
    fun listExhibitionPages(exhibition: Exhibition, exhibitionDevice: ExhibitionDevice?): List<ExhibitionPage> {
        return exhibitionPageDAO.list(exhibition, exhibitionDevice)
    }

    /**
     * Lists pages by layout
     *
     * @returns all pages with given layout
     */
    fun listExhibitionLayoutPages(pageLayout: PageLayout): List<ExhibitionPage> {
        return exhibitionPageDAO.listByLayout(pageLayout)
    }

    /**
     * Updates an exhibition page 
     *
     * @param exhibitionPage exhibition page  to be updated
     * @param device device
     * @param layout layout
     * @param name name
     * @param resources resources
     * @param eventTriggers event triggers
     * @param modifierId modifying user id
     * @return updated exhibition
     */
    fun updateExhibitionPage(exhibitionPage: ExhibitionPage, device: ExhibitionDevice, layout: PageLayout, name: String, resources: List<ExhibitionPageResource>, eventTriggers: List<ExhibitionPageEventTrigger>, modifierId: UUID): ExhibitionPage {
        var result = exhibitionPageDAO.updateName(exhibitionPage, name, modifierId)
        result = exhibitionPageDAO.updateLayout(result, layout, modifierId)
        result = exhibitionPageDAO.updateDevice(result, device, modifierId)
        result = exhibitionPageDAO.updateResources(result, getDataAsString(resources), modifierId)
        result = exhibitionPageDAO.updateEventTriggers(result, getDataAsString(eventTriggers), modifierId)
        return result
    }

    /**
     * Deletes an exhibition page 
     *
     * @param exhibitionPage exhibition page to be deleted
     */
    fun deleteExhibitionPage(exhibitionPage: ExhibitionPage) {
        return exhibitionPageDAO.delete(exhibitionPage)
    }

    /**
     * Serializes the object into JSON string
     *
     * @param data object
     * @return JSON string
     */
    private fun <T> getDataAsString(data: T): String {
        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(data)
    }

}