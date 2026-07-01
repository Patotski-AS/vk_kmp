package com.vk.kmp.feature.login.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vk.kmp.feature.login.api.LoginModel

@Composable
fun LoginScreen(
    model: LoginModel,
    onLoginClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "VK KMP")
        if (model.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        } else {
            Button(
                onClick = onLoginClick,
                modifier = Modifier.padding(top = 16.dp),
            ) {
                Text(text = "Войти через VK ID")
            }
        }
        model.error?.let { error ->
            Text(text = error, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
