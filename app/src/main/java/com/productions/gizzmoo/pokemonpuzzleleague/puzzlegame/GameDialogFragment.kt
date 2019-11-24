package com.productions.gizzmoo.pokemonpuzzleleague.puzzlegame

import android.app.AlertDialog
import android.app.Dialog
import androidx.fragment.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.productions.gizzmoo.pokemonpuzzleleague.R

class GameDialogFragment : DialogFragment() {
    private var mDidWin: Boolean = false
    private var mListener: GameEndingDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDidWin = arguments?.getBoolean(ARG_PARAM1) ?: false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.game_fragment_ending_dialog, container, false)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        if (mDidWin) {
            builder.setTitle(R.string.win)
        } else {
            builder.setTitle(R.string.lose)
        }

        builder.setPositiveButton(R.string.okay) { _, _ ->
            mListener?.onGameEndingDialogResponse(mDidWin)
        }

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GameEndingDialogListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnGameEndingDialogFragmentReturnListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener?.onGameEndingDialogResponse(mDidWin)
        mListener = null
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        fun newInstance(didWin: Boolean): GameDialogFragment {
            val fragment = GameDialogFragment()
            val args = Bundle().apply {
                putBoolean(ARG_PARAM1, didWin)
            }
            fragment.arguments = args
            return fragment
        }
    }
}