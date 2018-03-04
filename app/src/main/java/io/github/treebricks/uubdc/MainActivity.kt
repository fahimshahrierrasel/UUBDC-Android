package io.github.treebricks.uubdc

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val activityToolbar = toolbar

        // Initialize the material drawer
        drawer {
            toolbar = activityToolbar
            accountHeader{
                background = R.drawable.header
            }
            primaryItem("Add Donor"){
                onClick { _ ->
                    startActivity(Intent(this@MainActivity, RegistrationActivity::class.java))
                    false
                }
            }
            primaryItem("Search Donor"){
                onClick { _ ->
                    startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                    false
                }
            }
            primaryItem("Available Donor"){
                onClick { _ ->

                    false
                }
            }
            footer {
                primaryItem("About"){
                    onClick { _ ->

                        false
                    }
                }
            }
        }
    }
}
