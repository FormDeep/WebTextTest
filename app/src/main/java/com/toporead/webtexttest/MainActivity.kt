package com.toporead.webtexttest
import android.app.AlertDialog
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val webView: WebView = findViewById(R.id.webview)
        setupWebView(webView)
    }

    private fun setupWebView(webView: WebView) {
        val article = getString(R.string.t201901) // 获取字符串资源
        val htmlContent = wrapWordsWithSpan(article) // 将每个单词包装在<span>标签中

        webView.settings.javaScriptEnabled = true

        // 添加Javascript接口以供HTML中的Javascript调用
        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun wordClicked(word: String) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity,"the word $word is clicked",Toast.LENGTH_LONG).show()
                    showDefinitionDialog(word)
                }
            }
        }, "Android")

        val html = """
            <html>
            <head>
                <style>
                    /* 这里添加CSS样式，如果需要 */
                    span {
                        color: #333333;
                        cursor: pointer;
                    }
                </style>
            </head>
            <body>
                $htmlContent
                <script type="text/javascript">
                    function wordClicked(word) {
                        Android.wordClicked(word);
                    }
                </script>
            </body>
            </html>
        """.trimIndent()

        // 加载处理过的HTML字符串
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    private fun wrapWordsWithSpan(text: String): String {
        // 使用正则表达式为每个单词添加点击事件和<span>标签
        return text.replace("\\b(\\w+?)\\b".toRegex(), "<span onclick=\"wordClicked('$1')\">$1</span>")
    }

    private fun showDefinitionDialog(word: String) {
        AlertDialog.Builder(this)
            .setTitle("Word Clicked")
            .setMessage("You clicked on the word: $word")
            // 这里应该实现查询单词释义的逻辑，现在只是显示一个消息
            .setPositiveButton("OK", null)
            .show()
    }
}
