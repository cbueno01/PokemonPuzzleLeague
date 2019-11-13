package com.productions.gizzmoo.pokemonpuzzleleague.settings

import android.os.Parcel
import android.os.Parcelable
import android.preference.Preference

class SavedState(superState: Parcelable) : Preference.BaseSavedState(superState) {
    internal var value: Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        dest.writeInt(value)
    }
}
