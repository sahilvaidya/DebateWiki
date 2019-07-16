package com.sahilvaidya.debatewiki

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.view.Window
import android.webkit.*
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    lateinit var webView: WebView
    private val url = "https://hspolicy.debatecoaches.org/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = WebView(this)
        setWebContainerSettings()
        setContentView(webView)
        webView.loadUrl(url)
    }

    private fun setWebContainerSettings(){
        val settings = webView.settings

        settings.allowFileAccess = true

        // Create a WebViewClient
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                Toast.makeText(applicationContext, "Errorrr! $description", Toast.LENGTH_SHORT).show()
            }
        }

        webView.webChromeClient = object : WebChromeClient(){}

        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            //getting file name from url
            val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)
            //Alertdialog
            val builder = AlertDialog.Builder(this@MainActivity)
            //title for AlertDialog
            builder.setTitle("Download")
            //message of AlertDialog
            builder.setMessage("Do you want to save $filename")
            //When YES button clicks
            builder.setPositiveButton("Yes") { dialog, which ->
                //DownloadManager.Request created with url.
                val request = DownloadManager.Request(Uri.parse(url))
                //cookie
                val cookie = CookieManager.getInstance().getCookie(url)
                //Add cookie and User-Agent to request
                request.addRequestHeader("Cookie",cookie)
                request.addRequestHeader("User-Agent",userAgent)
                //file scanned by MediaScannar
                request.allowScanningByMediaScanner()
                //Download is visible and its progress, after completion too.
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                //DownloadManager created
                val downloadmanager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                //Saving file in Download folder
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename)
                //download enqued
                downloadmanager.enqueue(request)
            }
            builder.setNegativeButton("Cancel")
            {dialog, which ->
                //cancel the dialog if Cancel clicks
                dialog.cancel()
            }

            val dialog:AlertDialog = builder.create()
            //alertdialog shows
            dialog.show()



        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent) : Boolean{
        if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
