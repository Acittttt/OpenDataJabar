package com.example.opendatajabar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.opendatajabar.data.AppDatabase
import com.example.opendatajabar.data.DataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).dataDao()

    private val _rowCount = MutableLiveData<Int>(0)
    val rowCount: LiveData<Int> = _rowCount

    fun fetchRowCount() {
        viewModelScope.launch {
            _rowCount.value = dao.getCount()
        }
    }
    val dataList: LiveData<List<DataEntity>> = dao.getAll()


    fun insertData(
        kodeProvinsi: String,
        namaProvinsi: String,
        kodeKabupatenKota: String,
        namaKabupatenKota: String,
        total: String,
        satuan: String,
        tahun: String
    ) {
        viewModelScope.launch {
            val totalValue = total.toDoubleOrNull() ?: 0.0
            val tahunValue = tahun.toIntOrNull() ?: 0
            dao.insert(
                DataEntity(
                    kodeProvinsi = kodeProvinsi,
                    namaProvinsi = namaProvinsi,
                    kodeKabupatenKota = kodeKabupatenKota,
                    namaKabupatenKota = namaKabupatenKota,
                    total = totalValue,
                    satuan = satuan,
                    tahun = tahunValue
                )
            )
            withContext(Dispatchers.IO) {
                _rowCount.postValue(dao.getCount())
            }
        }
    }

    fun updateData(data: DataEntity) {
        viewModelScope.launch {
            dao.update(data)
        }
    }


    fun deleteData(data: DataEntity) {
        viewModelScope.launch {
            dao.delete(data)
            withContext(Dispatchers.IO) {
                _rowCount.postValue(dao.getCount())
            }
        }
    }

    suspend fun getDataById(id: Int): DataEntity? {
        return withContext(Dispatchers.IO) {
            dao.getById(id)
        }
    }

}
