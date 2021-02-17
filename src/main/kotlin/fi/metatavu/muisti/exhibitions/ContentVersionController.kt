package fi.metatavu.muisti.exhibitions

import fi.metatavu.muisti.persistence.dao.ContentVersionDAO
import fi.metatavu.muisti.persistence.dao.ContentVersionRoomDAO
import fi.metatavu.muisti.persistence.model.*
import fi.metatavu.muisti.utils.CopyException
import fi.metatavu.muisti.utils.IdMapper
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for content versions
 */
@ApplicationScoped
class ContentVersionController {

    @Inject
    private lateinit var contentVersionDAO: ContentVersionDAO

    @Inject
    private lateinit var contentVersionRoomDAO: ContentVersionRoomDAO

    /**
     * Creates new content version
     *
     * @param name content version name
     * @param language language code
     * @param creatorId creating user id
     * @return created exhibition content version
     */
    fun createContentVersion(exhibition: Exhibition, name: String, language: String, creatorId: UUID): ContentVersion {
        return contentVersionDAO.create(UUID.randomUUID(), exhibition, name, language, creatorId, creatorId)
    }

    /**
     * Creates a copy of a content version
     *
     * @param sourceContentVersion source content version
     * @param idMapper id mapper
     * @param namePrefix name prefix for the copied device (e.g. Copy of original device)
     * @param creatorId id of user that created the copy
     * @return a copy of a content version
     */
    fun copyContentVersion(
        sourceContentVersion: ContentVersion,
        idMapper: IdMapper,
        namePrefix: String,
        creatorId: UUID
    ): ContentVersion {
        val id = idMapper.getNewId(sourceContentVersion.id) ?: throw CopyException("Target content version id not found")
        val result = contentVersionDAO.create(
            id = id,
            exhibition = sourceContentVersion.exhibition ?: throw CopyException("Source content version exhibition not found"),
            name = "$namePrefix${sourceContentVersion.name}",
            language = sourceContentVersion.language ?: throw CopyException("Source content version language not found"),
            creatorId = creatorId,
            lastModifierId = creatorId
        )

        val sourceContentVersionRooms = contentVersionRoomDAO.listRoomsByContentVersion(sourceContentVersion)
            .mapNotNull { contentVersionRoom -> contentVersionRoom.exhibitionRoom }

        setContentVersionRooms(
            contentVersion = result,
            rooms = sourceContentVersionRooms
        )

        return result
    }

    /**
     * Finds content version by id
     *
     * @param id content version id
     * @return found content version or null if not found
     */
    fun findContentVersionById(id: UUID): ContentVersion? {
        return contentVersionDAO.findById(id)
    }

    /**
     * Lists content versions in an exhibitions
     * @param exhibition exhibition
     * @param exhibitionRoom exhibition room
     * @returns list of content versions
     */
    fun listContentVersions(
        exhibition: Exhibition,
        exhibitionRoom: ExhibitionRoom?
    ): List<ContentVersion> {

        if (exhibitionRoom != null) {
            return contentVersionRoomDAO.listContentVersionsByRoom(exhibitionRoom)
        }

        return contentVersionDAO.listByExhibition(exhibition)
    }

    /**
     * Sets content version rooms
     *
     * @param contentVersion content version
     * @param rooms list of exhibition rooms
     */
    fun setContentVersionRooms(contentVersion: ContentVersion, rooms: List<ExhibitionRoom>) {
        val existingContentVersionRooms = contentVersionRoomDAO.listRoomsByContentVersion(contentVersion).toMutableList()

        for (room in rooms) {
            val existingContentVersionRoom = existingContentVersionRooms.find { it.exhibitionRoom?.id == room.id }
            if (existingContentVersionRoom == null) {
                contentVersionRoomDAO.create(UUID.randomUUID(), contentVersion, room)
            } else {
                existingContentVersionRooms.remove(existingContentVersionRoom)
            }
        }
        existingContentVersionRooms.forEach(contentVersionRoomDAO::delete)
    }

    /**
     * Updates content version
     *
     * @param contentVersion content version to be updated
     * @param name group name
     * @param language language code
     * @param modifierId modifying user id
     * @return updated ContentVersion
     */
    fun updateContentVersion(contentVersion: ContentVersion, name: String, language: String, modifierId: UUID): ContentVersion {
        var result = contentVersionDAO.updateName(contentVersion, name, modifierId)
        result = contentVersionDAO.updateLanguage(result, language, modifierId)
        return result
    }

    /**
     * Deletes a content version and all relations
     *
     * @param contentVersion content version to be deleted
     */
    fun deleteContentVersion(contentVersion: ContentVersion) {
        contentVersionRoomDAO.listRoomsByContentVersion(contentVersion).forEach(contentVersionRoomDAO::delete)
        contentVersionDAO.delete(contentVersion)
    }
}