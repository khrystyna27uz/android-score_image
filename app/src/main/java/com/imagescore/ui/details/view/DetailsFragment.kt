package com.imagescore.ui.details.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.imagescore.R
import com.imagescore.ui.details.DetailsPresenter
import com.imagescore.ui.main.view.MainActivity
import com.imagescore.ui.score.model.ImageScoreModel
import com.imagescore.ui.score.view.BUNDLE_IMAGE_ID
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_detail.*
import javax.inject.Inject

class DetailsFragment : Fragment(R.layout.fragment_detail), DetailsView,
    EditTitleDialog.EditDialogInteraction {

    @Inject
    lateinit var presenter: DetailsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AndroidSupportInjection.inject(this)
        presenter.enterWithView(this)
    }

    override fun onDestroy() {
        presenter.exitFromView()
        super.onDestroy()
    }

    // Set up back button for top toolbar
    // Receive image id from previous Fragment and pass in to current presenter
    override fun setUpUI() {
        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        editIV.setOnClickListener { presenter.onEditTitleClicked(titleTV.text.toString()) }
        presenter.imageIdReceived(arguments!!.getLong(BUNDLE_IMAGE_ID))
    }

    // Received image data from database and setup UI
    override fun setScoreData(imageScore: ImageScoreModel) {
        titleTV.text = imageScore.title
        ratingBar.rating = imageScore.score.toFloat()
        Glide.with(this)
            .load(imageScore.imagePath)
            .placeholder(R.drawable.no_photo)
            .into(pictureIV)
    }

    override fun goToEditTitleDialog(currentTitle: String) {
        EditTitleDialog.show(this, currentTitle)
    }

    override fun applyEdit(updatedTitle: String) {
        presenter.onTitleEdited(updatedTitle)
    }


}