package com.example.trekkly.data.repository

import com.example.trekkly.data.local.dao.TrekDao
import com.example.trekkly.data.local.entity.FavouriteEntity
import com.example.trekkly.data.mapper.toDomain
import com.example.trekkly.data.mapper.toEntity
import com.example.trekkly.data.remote.TrekRemoteDataSource
import com.example.trekkly.data.remote.UserTrekRemoteDataSource
import com.example.trekkly.domain.model.Trek
import com.example.trekkly.domain.model.TrekStatus
import com.example.trekkly.domain.model.UserTrek
import com.example.trekkly.domain.repository.AuthRepository
import com.example.trekkly.domain.repository.TrekRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrekRepositoryImpl @Inject constructor(
    private val trekDao: TrekDao,
    private val remote: TrekRemoteDataSource,
    private val userRemote: UserTrekRemoteDataSource,
    private val authRepository: AuthRepository
) : TrekRepository {

    // ---- Catalog ----

    override fun observeTreks(): Flow<List<Trek>> =
        combine(trekDao.observeTreks(), trekDao.observeFavoritesIds()) { treks, favouriteIds ->
            val favourites = favouriteIds.toSet()
            treks.map { entity -> entity.toDomain(isFavourite = entity.id in favourites) }
        }

    override suspend fun syncTrek() {
        remote.observeTreks().collect { dtos ->
            trekDao.upsertTreks(dtos.map { it.toEntity() })
        }
    }

    override suspend fun setFavourite(trekId: String, favourite: Boolean) {
        if (favourite) trekDao.addFavourite(FavouriteEntity(trekId))
        else trekDao.removeFavourites(trekId)
    }

    // ---- Expeditions ----

    private fun observeUserTreksDomain(): Flow<List<UserTrek>> =
        combine(observeTreks(), trekDao.observeUserTreks()) { treks, userEntities ->
            val trekById = treks.associateBy { it.id }
            userEntities.mapNotNull { entity ->
                trekById[entity.trekId]?.let { entity.toDomain(it) }
            }
        }

    override fun observeActiveExpedition(): Flow<UserTrek?> =
        observeUserTreksDomain().map { list -> list.firstOrNull { it.status == TrekStatus.ACTIVE } }

    override fun observeUpcomingTreks(): Flow<List<UserTrek>> =
        observeUserTreksDomain().map { list ->
            list.filter { it.status == TrekStatus.UPCOMING }
                .sortedBy { it.scheduleDate ?: Long.MAX_VALUE }
        }

    override suspend fun syncUserTreks() {
        val uid = authRepository.currentUserId() ?: return
        userRemote.observeUserTreks(uid).collect { dtos ->
            trekDao.clearUserTreks()
            trekDao.upsertUserTreks(dtos.map { it.toEntity() })
        }
    }

    override suspend fun scheduleTrek(trekId: String, scheduleDate: Long) {
        val uid = authRepository.currentUserId() ?: return
        userRemote.scheduleTrek(uid, trekId, scheduleDate)
    }

    override suspend fun startTrek(trekId: String) {
        val uid = authRepository.currentUserId() ?: return
        userRemote.startTrek(uid, trekId)
    }

    override suspend fun updateProgress(trekId: String, currentDay: Int) {
        val uid = authRepository.currentUserId() ?: return
        userRemote.updateProgress(uid, trekId, currentDay)
    }

    override suspend fun completeTrek(trekId: String) {
        val uid = authRepository.currentUserId() ?: return
        userRemote.completeTrek(uid, trekId)
    }

    override suspend fun removeUserTrek(trekId: String) {
        val uid = authRepository.currentUserId() ?: return
        userRemote.removeUserTrek(uid, trekId)
    }
}
