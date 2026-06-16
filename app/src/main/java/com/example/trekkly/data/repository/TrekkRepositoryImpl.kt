package com.example.trekkly.data.repository

import com.example.trekkly.data.local.TrekkAssetDataSource
import com.example.trekkly.data.local.dao.TrekkDao
import com.example.trekkly.data.mapper.toDomain
import com.example.trekkly.data.mapper.toEntity
import com.example.trekkly.domain.model.Trekk
import com.example.trekkly.domain.repository.TrekkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrekkRepositoryImpl @Inject constructor(
    private val dao: TrekkDao,
    private val trekkAssetDataSource: TrekkAssetDataSource
) : TrekkRepository{
    override fun getTrekks(): Flow<List<Trekk>> {
        return  dao.getAllTrekks().map { list-> list.map { it.toDomain() } }
    }

    override fun observeByStatus(status: String): Flow<List<Trekk>> {
        return dao.trekksByStatus(status).map { list-> list.map{it.toDomain()} }
    }

    override suspend fun seedIfEmpty() {
        if (dao.count() > 0) return
        val now = System.currentTimeMillis()
        val entities = trekkAssetDataSource.loadSeedTrekks().map { it.toEntity(now) }
        dao.upsertAll(entities)
    }

}