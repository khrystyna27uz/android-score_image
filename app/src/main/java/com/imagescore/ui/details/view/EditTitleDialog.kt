package com.imagescore.ui.details.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.imagescore.R
import kotlinx.android.synthetic.main.dialog_edit_title.view.*
import kotlinx.android.synthetic.main.dialog_edit_title.view.titleET

const val EDIT_TITLE_DIALOG_TAG = "EditTitleDialog"
const val BUNDLE_TITLE = "title"

class EditTitleDialog : DialogFragment() {

    private lateinit var interaction: EditDialogInteraction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? = inflater.inflate(R.layout.dialog_edit_title, container, false)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_title, null)
        view.titleET.setText("")
        view.titleET.append(arguments!!.getString(BUNDLE_TITLE))
        view.applyB.setOnClickListener {
            interaction.applyEdit(view.titleET.text.toString())
            dialog?.dismiss()
        }
        view.closeIV.setOnClickListener { dialog?.dismiss() }
        view.cancelB.setOnClickListener { dialog?.dismiss() }
        view.titleET.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                view.applyB.isEnabled = view.titleET.text!!.isNotEmpty()
            }
        })
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            interaction = targetFragment as EditDialogInteraction
        } catch (e: ClassCastException) {
            throw IllegalArgumentException("$context should implements EditDialogInteraction")
        }
    }

    companion object {
        fun show(fm: Fragment, title: String) {
            EditTitleDialog().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_TITLE, title)
                }
                setTargetFragment(fm, -1)
            }
                .show(
                    fm.requireFragmentManager(),
                    EDIT_TITLE_DIALOG_TAG
                )
        }
    }

    interface EditDialogInteraction {
        fun applyEdit(updatedTitle: String)
    }
}