package io.github.achyuki.folddevtools.ui.screen

import android.content.ClipData
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import io.github.achyuki.folddevtools.preferences
import io.github.achyuki.folddevtools.ui.component.AttachPageList
import java.net.Inet4Address
import java.net.NetworkInterface

private fun getLocalIpAddress(): String {
    val bindPort = preferences.getInt("bindport", 9223)
    val interfaces = NetworkInterface.getNetworkInterfaces()
    for (intf in interfaces) {
        val addrs = intf.inetAddresses
        for (addr in addrs) {
            if (!addr.isLoopbackAddress && addr is Inet4Address) {
                return "${addr.hostAddress}:${bindPort}" ?: ""
            }
        }
    }
    return "Unavailable"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageScreen(navigator: NavController, title: String) {
    val ipAddress = remember { getLocalIpAddress() }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            TopBar(navigator, scrollBehavior, decode(title))
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Top + WindowInsetsSides.Horizontal
        )
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            IpAddressRow(ipAddress)
            AttachPageList(navigator)
        }
    }
}

@Composable
private fun IpAddressRow(ipAddress: String) {

    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "Local IP: $ipAddress",
            style = MaterialTheme.typography.bodyLarge
        )

        IconButton(
            onClick = {
                scope.launch {
                    val clipData = ClipData.newPlainText("ip_address", ipAddress)
                    clipboard.setClipEntry(ClipEntry(clipData))
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy IP"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(navigator: NavController, scrollBehavior: TopAppBarScrollBehavior, title: String) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(
                onClick = dropUnlessResumed {
                    navigator.popBackStack()
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        },
        windowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Top + WindowInsetsSides.Horizontal
        ),
        scrollBehavior = scrollBehavior
    )
}
