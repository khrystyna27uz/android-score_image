package com.imagescore.ui.details.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ShareCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.imagescore.R
import com.imagescore.ui.details.DetailsPresenter
import com.imagescore.ui.main.view.MainActivity
import com.imagescore.ui.score.model.ImageScoreModel
import com.imagescore.utils.toDateString
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_detail.*
import javax.inject.Inject

private const val SHARE_TYPE = "image/jpg"
private const val BUNDLE_IMAGE_ID = "image_id"

class DetailsFragment : Fragment(R.layout.fragment_detail), DetailsView,
    EditTitleDialog.EditDialogInteraction {

    companion object {
        fun newInstance(id: Long) = DetailsFragment().apply {
            arguments = bundleOf(
                BUNDLE_IMAGE_ID to id
            )
        }
    }

    @Inject
    lateinit var presenter: DetailsPresenter

    private inline val imageId get() = requireArguments().getLong(BUNDLE_IMAGE_ID)

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
        // TODO: Don't cast activity to a specific type, use an interface
        (activity as MainActivity).setSupportActionBar(toolbar)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        editIV.setOnClickListener { presenter.onEditTitleClicked(titleTV.text.toString()) }
        shareFB.setOnClickListener { presenter.onSharePhotoClicked() }
        presenter.imageIdReceived(imageId)
    }

    // Received image data from database and setup UI
    override fun setScoreData(imageScore: ImageScoreModel) {
        titleTV.text = imageScore.title
        dateTV.text = String.format(
            getString(R.string.imageDate),
            imageScore.details.date.toDateString()
        )
        storageTV.text = String.format(
            getString(R.string.imageStorageSize),
            imageScore.details.storageSize
        )
        heightTV.text = String.format(
            getString(R.string.imageHeight),
            imageScore.details.height
        )
        widthTV.text = String.format(
            getString(R.string.imageWidth),
            imageScore.details.width
        )
        fileFormatTV.text = String.format(
            getString(R.string.imageFileFormat),
            imageScore.details.fileFormat
        )
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

    // Share photo with possibility to way how to share
    override fun sharePhoto(imagePath: String) {
        val intent = ShareCompat.IntentBuilder.from(requireActivity())
            .setType(SHARE_TYPE)
            .setStream(Uri.parse(imagePath))
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

}