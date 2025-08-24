package com.ghostly.android.ui.components

import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun HtmlWebView(
    htmlContent: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = false
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(false)
                settings.displayZoomControls = false
                settings.builtInZoomControls = false
            }
        },
        update = { webView ->
            // Process HTML to add table styles
            val processedHtmlContent = htmlContent
                .replace(
                    regex = Regex("<table(?![^>]*style)([^>]*)>", RegexOption.IGNORE_CASE),
                    replacement = "<table$1 style=\"table-layout: fixed; box-sizing: border-box;\">"
                )
                .replace(
                    regex = Regex("<table([^>]*style=\")([^\"]*)\"", RegexOption.IGNORE_CASE),
                    replacement = "<table$1$2; table-layout: fixed; box-sizing: border-box;\""
                )
            
            val styledHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                    <style>
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
                            font-size: 16px;
                            line-height: 1.6;
                            color: #000;
                            margin: 0;
                            padding: 0;
                            word-wrap: break-word;
                        }
                        img {
                            max-width: 100%;
                            height: auto;
                            display: block;
                            margin: 16px 0;
                        }
                        p {
                            margin: 16px 0;
                        }
                        h1, h2, h3, h4, h5, h6 {
                            margin: 24px 0 16px 0;
                            font-weight: 600;
                        }
                        blockquote {
                            margin: 16px 0;
                            padding-left: 16px;
                            border-left: 3px solid #ccc;
                            color: #666;
                        }
                        pre {
                            background: #f5f5f5;
                            padding: 12px;
                            border-radius: 4px;
                            overflow-x: auto;
                        }
                        code {
                            background: #f5f5f5;
                            padding: 2px 4px;
                            border-radius: 3px;
                            font-family: 'Courier New', monospace;
                        }
                        a {
                            color: #0066cc;
                            text-decoration: none;
                        }
                        ul, ol {
                            margin: 16px 0;
                            padding-left: 24px;
                        }
                        li {
                            margin: 8px 0;
                        }
                        table {
                            width: 100%;
                            border-collapse: collapse;
                            margin: 16px 0;
                        }
                        td, th {
                            padding: 8px;
                            border: 1px solid #ddd;
                        }
                    </style>
                </head>
                <body>
                    $processedHtmlContent
                </body>
                </html>
            """.trimIndent()
            webView.loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null)
        }
    )
}