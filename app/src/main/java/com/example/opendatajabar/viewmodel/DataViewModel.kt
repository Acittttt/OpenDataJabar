package com.example.opendatajabar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.opendatajabar.data.local.AppDatabase
import com.example.opendatajabar.data.local.DataEntity
import com.example.opendatajabar.data.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).dataDao()
    private val apiService = RetrofitClient.instance  // ✅ Fix: Panggil `instance` dari RetrofitClient

    private val _rowCount = MutableLiveData<Int>(0)
    val rowCount: LiveData<Int> = _rowCount

    private val _dataList = MutableLiveData<List<DataEntity>>()
    val dataList: LiveData<List<DataEntity>> = _dataList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchDataFromApi()
        fetchRowCount()
    }

    fun fetchRowCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = dao.getCount()
            withContext(Dispatchers.Main) {
                _rowCount.value = count
            }
        }
    }

    fun fetchDataFromApi() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            try {
                val response = apiService.getData()  // ✅ Fix: Panggil API dengan benar
                if (response.error == 0) {  // ✅ Fix: Pastikan respon API benar
                    dao.insertAll(response.data) // Simpan ke Room Database
                    _dataList.postValue(response.data) // Update LiveData
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun insertData(
        kodeProvinsi: Int,
        namaProvinsi: String,
        kodeKabupatenKota: Int,
        namaKabupatenKota: String,
        total: Double,
        satuan: String,
        tahun: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val entity = DataEntity(
                kodeProvinsi = kodeProvinsi,
                namaProvinsi = namaProvinsi,
                kodeKabupatenKota = kodeKabupatenKota,
                namaKabupatenKota = namaKabupatenKota,
                rataRataLamaSekolah = total,
                satuan = satuan,
                tahun = tahun
            )
            dao.insert(entity)

            val count = dao.getCount() // Ambil jumlah data terbaru
            withContext(Dispatchers.Main) {
                _rowCount.value = count
            }

            fetchDataFromApi() // Ambil data terbaru setelah insert
        }
    }

    fun updateData(data: DataEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(data)
            fetchDataFromApi() // Refresh data setelah update
        }
    }

    fun deleteData(data: DataEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(data)
            val count = dao.getCount()
            withContext(Dispatchers.Main) {
                _rowCount.value = count
            }
            fetchDataFromApi() // Ambil data terbaru setelah delete
        }
    }

    suspend fun getDataById(id: Int): DataEntity? {
        return withContext(Dispatchers.IO) {
            dao.getById(id)
        }
    }
}