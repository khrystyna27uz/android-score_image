package com.imagescore.ui.main.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.imagescore.R
import com.imagescore.ui.main.MainPresenter
import com.imagescore.ui.score.view.ScoreFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class MainActivity : AppCompatActivity(),
    MainView, HasSupportFragmentInjector, Navigation {

    @Inject
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter.enterWithView(this)
    }

    override fun onDestroy() {
        presenter.exitFromView()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    override fun goToScoreFragment() {
        navigate(ScoreFragment.newInstance(), resetBackStack = true)
    }

    override fun navigate(fragment: Fragment, resetBackStack: Boolean) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .apply {
                if (!resetBackStack) addToBackStack(null)
            }
            .commit()
    }

    override fun navigateBack() {
        onBackPressed()
    }
}
