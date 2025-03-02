package com.example.opendatajabar.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.opendatajabar.viewmodel.DataViewModel

@Composable
fun HomeScreen(viewModel: DataViewModel
) {
    val rowCount by viewModel.rowCount.observeAsState(0)
    LaunchedEffect(key1 = true){
        viewModel.fetchRowCount()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "data yang telah diinputkan: $rowCount")
    }
}