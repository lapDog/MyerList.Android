package com.juniperphoton.myerlist.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.OnClick
import com.juniperphoton.myerlist.R
import com.juniperphoton.myerlist.extension.getResString
import com.juniperphoton.myerlist.extension.getVersionName
import com.juniperphoton.myerlist.extension.startActivitySafely
import kotlinx.android.synthetic.main.activity_about.*
import moe.feng.alipay.zerosdk.AlipayZeroSdk

@Suppress("unused", "unused_parameter")
class AboutActivity : BaseActivity() {
    val versionTextView: TextView by lazy {
        version_text_view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        ButterKnife.bind(this)

        updateVersion()
    }

    private fun updateVersion() {
        versionTextView.text = "${this.getVersionName()}"
    }

    @OnClick(R.id.email_view)
    internal fun onClickEmail(view: View) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "message/rfc822"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_dev)))

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MyerList for Android ${this.getVersionName()} feedback")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")

        startActivitySafely(emailIntent)
    }

    @OnClick(R.id.rate_view)
    internal fun onClickRate() {
        val uri = Uri.parse("market://details?id=$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivitySafely(intent)
    }

    @OnClick(R.id.donate_view)
    internal fun onClickDonate() {
        if (AlipayZeroSdk.hasInstalledAlipayClient(this)) {
            AlipayZeroSdk.startAlipayClient(this, "aex09127b4dbo4o7fbvcyb0")
        }
    }

    @OnClick(R.id.github_view)
    internal fun onClickGithub() {
        val uri = Uri.parse(R.string.github_url.getResString())
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivitySafely(intent)
    }

    @OnClick(R.id.twitter_item)
    internal fun onClickTwitter() {
        val uri = Uri.parse(getString(R.string.twitter_url))
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivitySafely(intent)
    }

    @OnClick(R.id.weibo_item)
    internal fun onClickWeibo() {
        val uri = Uri.parse(getString(R.string.weibo_url))
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivitySafely(intent)
    }
}
