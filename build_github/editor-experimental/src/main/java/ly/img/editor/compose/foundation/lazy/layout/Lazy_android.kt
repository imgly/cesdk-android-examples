/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ly.img.editor.compose.foundation.lazy.layout

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import ly.img.editor.compose.internal.OriginallyExpect

/**
 * This creates an object meeting following requirements:
 * 1) Objects created for the same index are equals and never equals for different indexes.
 * 2) This class is saveable via a default SaveableStateRegistry on the platform.
 * 3) This objects can't be equals to any object which could be provided by a user as a custom key.
 */
@OriginallyExpect
fun getDefaultLazyLayoutKey(index: Int): Any = DefaultLazyKey(index)

@SuppressLint("BanParcelableUsage")
private data class DefaultLazyKey(private val index: Int) : Parcelable {
    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeInt(index)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<DefaultLazyKey> =
            object : Parcelable.Creator<DefaultLazyKey> {
                override fun createFromParcel(parcel: Parcel) = DefaultLazyKey(parcel.readInt())

                override fun newArray(size: Int) = arrayOfNulls<DefaultLazyKey?>(size)
            }
    }
}
